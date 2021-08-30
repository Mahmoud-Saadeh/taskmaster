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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnCompleteListener;

public class AddTask extends AppCompatActivity {
    private String uploadedFileName;
    private FirebaseAnalytics mFirebaseAnalytics;
    private String currentUser = "";

    // initializing
    // FusedLocationProviderClient
    // object
    FusedLocationProviderClient mFusedLocationClient;

    // Initializing other items
    // from layout file
    TextView latitudeTextView, longitTextView;
    double latitude, longitude;
    int PERMISSION_ID = 44;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint({"SetTextI18n", "MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task);

        try {
            currentUser = Amplify.Auth.getCurrentUser().getUsername();
        }catch (RuntimeException error){
            Log.i("currentUser", "onCreate: " + currentUser);
            if (currentUser.equals("")){
                Intent goToSignIn = new Intent(this, SignInActivity.class);
                startActivity(goToSignIn);
            }
        }
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        latitudeTextView = findViewById(R.id.latTextView);
        longitTextView = findViewById(R.id.lonTextView);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // method to get the location
        getLastLocation();

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        Log.i("getIntent", "onCreate: " + type);
        Log.i("getIntent", "onCreate: " + intent.getParcelableExtra(Intent.EXTRA_STREAM));
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                try {
                    onChooseFile(intent.getParcelableExtra(Intent.EXTRA_STREAM)); // Handle single image being sent
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
//        uploadFile();

        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            assert result.getData() != null;
                            onChooseFile(result.getData().getData());
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

            Task task = Task.builder().title(taskTitle).teamId(teamId).body(taskBody).state(taskState).fileName(fileNameIfThere).lat(latitude).lon(longitude).build();

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
    private void onChooseFile(Uri uri) throws IOException {

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
                success -> {
                    Log.i("onChooseFile", "uploadFileToS3: succeeded " + success.getKey());
                    Toast.makeText(getApplicationContext(), "Image Successfully Uploaded", Toast.LENGTH_SHORT).show();

                },
                error -> {
                    Log.e("onChooseFile", "uploadFileToS3: failed " + error.toString());
                    Toast.makeText(getApplicationContext(), "Image Upload failed", Toast.LENGTH_SHORT).show();

                }
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

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            latitudeTextView.setText("Lat: " + location.getLatitude());
                            longitTextView.setText("Lon: " + location.getLongitude());
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();

            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            latitudeTextView.setText("Lat: " + mLastLocation.getLatitude());
            longitTextView.setText("Lon: " + mLastLocation.getLongitude());
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}