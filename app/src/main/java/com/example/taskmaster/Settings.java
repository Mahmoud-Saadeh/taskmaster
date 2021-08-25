package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Team;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Settings extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor preferenceEditor = settings.edit();

        ((TextView) findViewById(R.id.userName)).setText(settings.getString("userName", ""));

        Spinner teamSpinner = findViewById(R.id.task_team_spinner_settings);
        List<String> teamNameList = new ArrayList<>();

        Amplify.API.query(ModelQuery.list(Team.class),
                response -> {
                    for (Team team: response.getData()) {
                        teamNameList.add(team.getName());
                    }
                    //create an adapter to describe how the items are displayed, adapters are used in several places in android.
                    ArrayAdapter<String> teamSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, teamNameList);
                    runOnUiThread(() -> {
                        //set the spinners adapter to the previously created one.
                        teamSpinner.setAdapter(teamSpinnerAdapter);
                    });

                },
                error -> {
                    Log.e("TEAM_ERROR", "onCreate: ", error);
                });

        findViewById(R.id.addTaskMenu).setOnClickListener(view -> {
            Intent goToAddTask = new Intent(getBaseContext(), AddTask.class);
            startActivity(goToAddTask);
        });

        findViewById(R.id.saveUserName).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textInputLayout = ((TextView) findViewById(R.id.userName)).getText().toString();
                String taskTeam = ((Spinner) findViewById(R.id.task_team_spinner_settings)).getSelectedItem().toString();
//                String textInputLayout = Objects.requireNonNull(((TextInputLayout) findViewById(R.id.userName)).getEditText()).getText().toString();

                // Set this up in the UI thread.

                Handler mHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        Toast.makeText(getApplicationContext(), textInputLayout  + " has been saved", Toast.LENGTH_SHORT).show();
                    }
                };

                Amplify.API.query(ModelQuery.list(Team.class, Team.NAME.eq(taskTeam)),
                        res -> {
                            List<Team> teamList1 = new ArrayList<>();
                            Log.i("TEAM", "onClick: " + res.getData().toString());
                            for (Team team: res.getData()) {
                                teamList1.add(team);
                            }
                            preferenceEditor.putString("userName", textInputLayout);
                            preferenceEditor.putString("teamId", teamList1.get(0).getId());
                            preferenceEditor.putString("teamName", taskTeam);
                            Message message = mHandler.obtainMessage(1);
                            message.sendToTarget();
                            preferenceEditor.apply();
                        },
                        err -> {
                            Log.e("ERROR", "onClick: ",err );
                        });

            }
        });
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Settings");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Settings");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Page");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_app_bar, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
////     Handle item selection
//        if (item.getItemId() == R.id.addTaskMenu) {
//            Intent goToAddTask = new Intent(getBaseContext(), AddTask.class);
//            startActivity(goToAddTask);
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

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