package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Team;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Settings extends AppCompatActivity {

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


    }
}