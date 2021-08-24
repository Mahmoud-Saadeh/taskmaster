package com.example.taskmaster;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.auth.AuthUser;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    protected List<Task> taskListAmp = new ArrayList<>();
    protected TaskViewAdapter adapter;
    public static final String TAG = "NOTIFICATION";
    private static PinpointManager pinpointManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("TEST", "onCreate: STARTED");

        // Initialize PinpointManager
        getPinpointManager(getApplicationContext());

        Amplify.API.query(ModelQuery.list(Team.class),
                response -> {
                    int count = 0;
                    for (Team ignored : response.getData()) {
                        count++;
                    }
                    if (count == 0) {
                        Team team1 = Team.builder().name("team1").build();
                        Team team2 = Team.builder().name("team2").build();
                        Team team3 = Team.builder().name("team3").build();

                        Amplify.API.mutate(ModelMutation.create(team1), success -> {
                        }, failure -> Log.e("save", "onCreate: ", failure));
                        Amplify.API.mutate(ModelMutation.create(team2), success -> {
                        }, failure -> Log.e("save", "onCreate: ", failure));
                        Amplify.API.mutate(ModelMutation.create(team3), success -> {
                        }, failure -> Log.e("save", "onCreate: ", failure));
                    }
                },
                error -> Log.e("TEAM", "onCreate: ", error));

        findViewById(R.id.settingsButton).setOnClickListener(view14 -> {
            Intent settings = new Intent(getBaseContext(), Settings.class);
            startActivity(settings);
        });

        findViewById(R.id.addTaskMenu).setOnClickListener(view -> {
            Intent goToAddTask = new Intent(getBaseContext(), AddTask.class);
            startActivity(goToAddTask);
        });

    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        ((TextView) findViewById(R.id.homePageTitle)).setText(preferences.getString("userName", "My Tasks") + "'s Tasks");
        ((TextView) findViewById(R.id.homePageTitle)).setText(getCurrentUser().getUsername() + "'s Tasks");
        ((TextView) findViewById(R.id.home_page_filter)).setText("Filtered by: " + preferences.getString("teamName", "All"));
        String teamId = preferences.getString("teamId", "");
//        Amplify.API.query(TaskByDate.)
        if (teamId.equals("")) {
            fetchAllData();
        } else {
            fetchDataByTeamId(teamId);
        }

    }

    private AuthUser getCurrentUser() {
        return Amplify.Auth.getCurrentUser();
    }

//    @SuppressLint("NotifyDataSetChanged")

    private void fetchAllData() {

        Amplify.API.query(
                ModelQuery.list(Task.class),
                response -> {
                    Log.i("test", "onResume: " + response.getData());

                    taskListAmp = new ArrayList<>();
                    for (Task task : (response.getData() == null ? taskListAmp : response.getData())) {
//
                        taskListAmp.add(task);
                    }
                    RecyclerView taskRecyclerView = findViewById(R.id.task_list);

                    adapter = new TaskViewAdapter(taskListAmp, position -> {
                        Intent goToDetailsIntent = new Intent(getBaseContext(), TaskDetail.class);
                        goToDetailsIntent.putExtra("task_id", taskListAmp.get(position).getId());
                        startActivity(goToDetailsIntent);
                    });


                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                            getApplicationContext(),
                            LinearLayoutManager.VERTICAL,
                            false);
                    runOnUiThread(() -> {
                        // Stuff that updates the UI
                        taskRecyclerView.setLayoutManager(linearLayoutManager);
                        taskRecyclerView.setAdapter(adapter);

                    });
                },
                error -> Log.e("MyAmplifyApp", "Query failure", error)
        );
    }

    private void fetchDataByTeamId(String teamId) {

        Amplify.API.query(
                ModelQuery.list(Task.class, Task.TEAM_ID.eq(teamId)),
                response -> {
                    Log.i("test", "onResume: " + response.getData());

                    taskListAmp = new ArrayList<>();
                    for (Task task : (response.getData() == null ? taskListAmp : response.getData())) {
//
                        taskListAmp.add(task);
                    }
                    RecyclerView taskRecyclerView = findViewById(R.id.task_list);

                    adapter = new TaskViewAdapter(taskListAmp, position -> {
                        Intent goToDetailsIntent = new Intent(getBaseContext(), TaskDetail.class);
                        goToDetailsIntent.putExtra("task_id", taskListAmp.get(position).getId());
                        goToDetailsIntent.putExtra("fileName", taskListAmp.get(position).getFileName());
                        startActivity(goToDetailsIntent);
                    });


                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                            getApplicationContext(),
                            LinearLayoutManager.VERTICAL,
                            false);
                    runOnUiThread(() -> {
                        // Stuff that updates the UI
                        taskRecyclerView.setLayoutManager(linearLayoutManager);
                        taskRecyclerView.setAdapter(adapter);

                    });
                },
                error -> Log.e("MyAmplifyApp", "Query failure", error)
        );
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

    public static PinpointManager getPinpointManager(final Context applicationContext) {
        if (pinpointManager == null) {
            final AWSConfiguration awsConfig = new AWSConfiguration(applicationContext);
            AWSMobileClient.getInstance().initialize(applicationContext, awsConfig, new Callback<UserStateDetails>() {
                @Override
                public void onResult(UserStateDetails userStateDetails) {
                    Log.i("INIT", String.valueOf(userStateDetails.getUserState()));
                }

                @Override
                public void onError(Exception e) {
                    Log.e("INIT", "Initialization error.", e);
                }
            });

            PinpointConfiguration pinpointConfig = new PinpointConfiguration(
                    applicationContext,
                    AWSMobileClient.getInstance(),
                    awsConfig);

            pinpointManager = new PinpointManager(pinpointConfig);

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                return;
                            }
                            final String token = task.getResult();
                            Log.d(TAG, "Registering push notifications token: " + token);
                            pinpointManager.getNotificationClient().registerDeviceToken(token);
                        }
                    });
        }
        return pinpointManager;
    }
}