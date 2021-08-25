package com.example.taskmaster;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
//import com.example.taskmaster.room.AppDatabase;
//import com.example.taskmaster.room.TaskDao;
import com.amplifyframework.datastore.generated.model.Team;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddTask extends AppCompatActivity {
    private String uploadedFileName;
    private FirebaseAnalytics mFirebaseAnalytics;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task);



//        uploadFile();

        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            onChooseFile(result);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        //get the spinner from the xml.
        Spinner stateSpinner = findViewById(R.id.task_state_spinner);
        //create a list of items for the spinner.
        String[] stateListSpinner = new String[]{TaskStates.NEW.toString(),
                TaskStates.COMPLETE.toString(),
                TaskStates.ASSIGNED.toString(),
                TaskStates.IN_PROGRESS.toString()};

        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        ArrayAdapter<String> stateSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, stateListSpinner);
        //set the spinners adapter to the previously created one.
        stateSpinner.setAdapter(stateSpinnerAdapter);

        Spinner teamSpinner = findViewById(R.id.task_team_spinner);
        List<String> teamNameList = new ArrayList<>();
        List<Team> teamList = new ArrayList<>();
        Amplify.API.query(ModelQuery.list(Team.class),
                response -> {
                    for (Team team: response.getData()) {
                        teamNameList.add(team.getName());
                        teamList.add(team);
                    }
                    //create an adapter to describe how the items are displayed, adapters are used in several places in android.
                    ArrayAdapter<String> teamSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, teamNameList);
                    runOnUiThread(() -> {
                        //set the spinners adapter to the previously created one.
                        teamSpinner.setAdapter(teamSpinnerAdapter);
                    });

                },
                error -> Log.e("TEAM_ERROR", "onCreate: ", error));

        findViewById(R.id.btnUploadFile).setOnClickListener(view -> {
            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("*/*");
            chooseFile = Intent.createChooser(chooseFile, "Choose a file");
            someActivityResultLauncher.launch(chooseFile);
        });

        findViewById(R.id.addTask).setOnClickListener(view -> {

            String taskTitle = ((TextView) findViewById(R.id.taskTitle)).getText().toString();
            String taskBody = ((TextView) findViewById(R.id.taskDescription)).getText().toString();
            @SuppressLint("CutPasteId") String taskState = ((Spinner) findViewById(R.id.task_state_spinner)).getSelectedItem().toString();
            @SuppressLint("CutPasteId") String taskTeam = ((Spinner) findViewById(R.id.task_team_spinner)).getSelectedItem().toString();
            String teamId = "";
            for (Team team: teamList) {
                if (team.getName().equals(taskTeam)){
                    teamId = team.getId();
                }
            }

            String fileNameIfThere = uploadedFileName == null ? "" : uploadedFileName;

            Task task = Task.builder().title(taskTitle).teamId(teamId).body(taskBody).state(taskState).fileName(fileNameIfThere).build();

            Amplify.API.mutate(ModelMutation.create(task),
                    response -> {
                        Log.i("MyAmplifyApp", "Todo with id: " + response.getData().getId());
//                            taskDao.addTask(task);
                    },
                    error -> Log.e("MyAmplifyApp", "Create failed", error)
            );

            Toast.makeText(getBaseContext(), "submitted!", Toast.LENGTH_SHORT).show();
        });

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "AddTask");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "AddTask");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Page");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void onChooseFile(ActivityResult activityResult) throws IOException {

        Uri uri = null;
        if (activityResult.getData() != null) {
            uri = activityResult.getData().getData();
        }
        assert uri != null;
        uploadedFileName = new Date().toString() + "." + getMimeType(getApplicationContext(),uri);

        File uploadFile = new File(getApplicationContext().getFilesDir(), "uploadFile");

        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            FileUtils.copy(inputStream, new FileOutputStream(uploadFile));
        } catch (Exception exception) {
            Log.e("onChooseFile", "onActivityResult: file upload failed" + exception.toString());
        }

        Amplify.Storage.uploadFile(
                uploadedFileName,
                uploadFile,
                success -> Log.i("onChooseFile", "uploadFileToS3: succeeded " + success.getKey()),
                error -> Log.e("onChooseFile", "uploadFileToS3: failed " + error.toString())
        );
    }

    public static String getMimeType(Context context, Uri uri) {
        String extension;

        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_app_bar, menu);
        return true;
    }

    public void signOutHandler(MenuItem item) {
        Amplify.Auth.signOut(
                () -> {
                    Log.i("AuthQuickstart", "Signed out successfully");
                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                    startActivity(intent);
                },
                error -> Log.e("AuthQuickstart", error.toString())
        );
    }
}