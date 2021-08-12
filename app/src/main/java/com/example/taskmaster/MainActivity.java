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

import com.example.taskmaster.room.AppDatabase;
import com.example.taskmaster.room.TaskDao;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private List<Task> taskList = new ArrayList<>();
    private AppDatabase db;
    private TaskDao taskDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        taskList.add(new Task("Do 100 push-ups","Training", TaskStates.IN_PROGRESS));
//        taskList.add(new Task("Solve ASAC assignments","Studying", TaskStates.COMPLETE));
//        taskList.add(new Task("Buy Coffee","Shopping", TaskStates.ASSIGNED));
//        taskList.add(new Task("play CS GO","Playing", TaskStates.NEW));
//        taskList.add(new Task("Eat dinner","Eating", TaskStates.COMPLETE));

    }


    @Override
    public void onResume() {
        super.onResume();
        RecyclerView taskRecyclerView = findViewById(R.id.task_list);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Log.i("HOME", "onResume: " + preferences.getString("userName", "My Tasks"));

        ((TextView) findViewById(R.id.homePageTitle)).setText(preferences.getString("userName", "My Tasks") + "'s Tasks");

        db = Room.databaseBuilder(getApplicationContext(),AppDatabase.class, "Task").allowMainThreadQueries().build();
        taskDao = db.taskDao();

        taskList = taskDao.findAll();

        TaskViewAdapter adapter = new TaskViewAdapter(taskList, position -> {
            Intent goToDetailsIntent = new Intent(getBaseContext(), TaskDetail.class);
            goToDetailsIntent.putExtra("task_uid", taskList.get(position).getUid());
            startActivity(goToDetailsIntent);
        });


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.VERTICAL,
                false);

        taskRecyclerView.setLayoutManager(linearLayoutManager);
        taskRecyclerView.setAdapter(adapter);

        findViewById(R.id.settingsButton).setOnClickListener(view14 -> {
            Intent settings = new Intent(getBaseContext(),Settings.class);
            startActivity(settings);
        });

        findViewById(R.id.addTaskMenu).setOnClickListener(view -> {
            Intent goToAddTask = new Intent(getBaseContext(),AddTask.class);
            startActivity(goToAddTask);
        });
    }
}