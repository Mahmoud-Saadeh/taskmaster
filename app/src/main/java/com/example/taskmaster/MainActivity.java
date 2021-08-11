package com.example.taskmaster;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

//    private AppBarConfiguration appBarConfiguration;
//    private ActivityMainBinding binding;
    private List<Task> taskList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
        setContentView(R.layout.activity_main);
//        setSupportActionBar(binding.toolbar);
//
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        RecyclerView taskRecyclerView = findViewById(R.id.task_list);

        taskList.add(new Task("Do 100 push-ups","Training", TaskStates.IN_PROGRESS));
        taskList.add(new Task("Solve ASAC assignments","Studying", TaskStates.COMPLETE));
        taskList.add(new Task("Buy Coffee","Shopping", TaskStates.ASSIGNED));
        taskList.add(new Task("play CS GO","Playing", TaskStates.NEW));
        taskList.add(new Task("Eat dinner","Eating", TaskStates.COMPLETE));

        TaskViewAdapter adapter = new TaskViewAdapter(taskList, new TaskViewAdapter.OnTaskItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                Intent goToDetailsIntent = new Intent(getBaseContext(), TaskDetail.class);
                goToDetailsIntent.putExtra("task_title", taskList.get(position).getTitle());
                goToDetailsIntent.putExtra("task_body", taskList.get(position).getBody());
                goToDetailsIntent.putExtra("task_state", taskList.get(position).getState().toString());
                startActivity(goToDetailsIntent);
            }
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

        findViewById(R.id.addTaskMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToAddTask = new Intent(getBaseContext(),AddTask.class);
                startActivity(goToAddTask);
            }
        });
    }


//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        return NavigationUI.navigateUp(navController, appBarConfiguration)
//                || super.onSupportNavigateUp();
//    }


//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }

    @Override
    public void onResume() {
        super.onResume();
        //homePageTitle
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Log.i("HOME", "onResume: " + preferences.getString("userName", "My Tasks"));

        ((TextView) findViewById(R.id.homePageTitle)).setText(preferences.getString("userName", "My Tasks") + "'s Tasks");
    }
}