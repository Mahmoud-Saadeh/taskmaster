package com.example.taskmaster;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.Team;
//import com.example.taskmaster.room.AppDatabase;
//import com.example.taskmaster.room.TaskDao;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    protected List<Task> taskListAmp = new ArrayList<>();
    protected TaskViewAdapter adapter;
//    private List<Task> taskList = new ArrayList<>();
//    private AppDatabase db;
//    private TaskDao taskDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("TEST", "onCreate: STARTED");

        try {
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());

            Log.i("Tutorial", "Initialized Amplify");
        } catch (AmplifyException e) {
            Log.e("Tutorial", "Could not initialize Amplify", e);
        }

        Amplify.API.query(ModelQuery.list(Team.class),
                response -> {
                    int count = 0;
                    for (Team team: response.getData()){
                        count++;
                    }
                    if (count == 0){
                        Team team1 = Team.builder().name("team1").build();
                        Team team2 = Team.builder().name("team2").build();
                        Team team3 = Team.builder().name("team3").build();

                        Amplify.API.mutate(ModelMutation.create(team1),success ->{}, failure ->{
                            Log.e("save", "onCreate: ", failure );
                        });
                        Amplify.API.mutate(ModelMutation.create(team2),success ->{}, failure ->{
                            Log.e("save", "onCreate: ", failure );
                        });
                        Amplify.API.mutate(ModelMutation.create(team3),success ->{}, failure ->{
                            Log.e("save", "onCreate: ", failure );
                        });
                    }
                },
                error -> {
                    Log.e("TEAM", "onCreate: ", error);
                });

        findViewById(R.id.settingsButton).setOnClickListener(view14 -> {
            Intent settings = new Intent(getBaseContext(),Settings.class);
            startActivity(settings);
        });

        findViewById(R.id.addTaskMenu).setOnClickListener(view -> {
            Intent goToAddTask = new Intent(getBaseContext(),AddTask.class);
            startActivity(goToAddTask);
        });

    }


//    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    protected void onResume() {
        super.onResume();
//        if (taskListAmp.size() == 0){
//            Log.i("test", "onResume: ");
//        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ((TextView) findViewById(R.id.homePageTitle)).setText(preferences.getString("userName", "My Tasks") + "'s Tasks");
        ((TextView) findViewById(R.id.home_page_filter)).setText("Filtered by: "+preferences.getString("teamName", "All"));
        String teamId = preferences.getString("teamId", "");
//        Amplify.API.query(TaskByDate.)
        if (teamId.equals("")){
            fetchAllData();
        }else {
            fetchDataByTeamId(teamId);
        }

    }

//    @SuppressLint("NotifyDataSetChanged")

    private void fetchAllData(){

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
                error -> {
                    Log.e("MyAmplifyApp", "Query failure", error);
                }
        );
    }
    private void fetchDataByTeamId(String teamId){

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
                error -> {
                    Log.e("MyAmplifyApp", "Query failure", error);
                }
        );
    }
}