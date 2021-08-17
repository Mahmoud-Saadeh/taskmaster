package com.example.taskmaster;

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
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.example.taskmaster.room.AppDatabase;
import com.example.taskmaster.room.TaskDao;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private List<com.amplifyframework.datastore.generated.model.Task> taskListAmp = new ArrayList<>();
    private List<Task> taskList = new ArrayList<>();
    private AppDatabase db;
    private TaskDao taskDao;

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

        findViewById(R.id.settingsButton).setOnClickListener(view14 -> {
            Intent settings = new Intent(getBaseContext(),Settings.class);
            startActivity(settings);
        });

        findViewById(R.id.addTaskMenu).setOnClickListener(view -> {
            Intent goToAddTask = new Intent(getBaseContext(),AddTask.class);
            startActivity(goToAddTask);
        });

//        taskList.add(new Task("Do 100 push-ups","Training", TaskStates.IN_PROGRESS));
//        taskList.add(new Task("Solve ASAC assignments","Studying", TaskStates.COMPLETE));
//        taskList.add(new Task("Buy Coffee","Shopping", TaskStates.ASSIGNED));
//        taskList.add(new Task("play CS GO","Playing", TaskStates.NEW));
//        taskList.add(new Task("Eat dinner","Eating", TaskStates.COMPLETE));

    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ((TextView) findViewById(R.id.homePageTitle)).setText(preferences.getString("userName", "My Tasks") + "'s Tasks");

        Log.i("TEST", "onResume: started" + taskListAmp);
        Amplify.API.query(
                ModelQuery.list(com.amplifyframework.datastore.generated.model.Task.class, com.amplifyframework.datastore.generated.model.Task.TITLE.ne("")),
                response -> {
                    taskListAmp = new ArrayList<>();
                    for (com.amplifyframework.datastore.generated.model.Task task : response.getData()) {
//                        Log.i("MyAmplifyApp", todo.getName());
                        taskListAmp.add(task);
                    }
                    RecyclerView taskRecyclerView = findViewById(R.id.task_list);

                    TaskViewAdapter adapter = new TaskViewAdapter(taskListAmp, position -> {
                        Intent goToDetailsIntent = new Intent(getBaseContext(), TaskDetail.class);
                        goToDetailsIntent.putExtra("task_id", taskListAmp.get(position).getId());
                        startActivity(goToDetailsIntent);
                    });


                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                            getApplicationContext(),
                            LinearLayoutManager.VERTICAL,
                            false);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            // Stuff that updates the UI
                            taskRecyclerView.setLayoutManager(linearLayoutManager);
                            taskRecyclerView.setAdapter(adapter);
                        }
                    });



                },
                error -> {
                    Log.e("MyAmplifyApp", "Query failure", error);
                    db = Room.databaseBuilder(getApplicationContext(),AppDatabase.class, "Task").allowMainThreadQueries().build();
                    taskDao = db.taskDao();

                    taskList = taskDao.findAll();
                }
        );

    }
}