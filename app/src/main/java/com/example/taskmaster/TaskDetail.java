package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.taskmaster.room.AppDatabase;
import com.example.taskmaster.room.TaskDao;

public class TaskDetail extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private AppDatabase db;
    private TaskDao taskDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        Intent intent = getIntent();
        db = Room.databaseBuilder(getApplicationContext(),AppDatabase.class, "Task").allowMainThreadQueries().build();
        taskDao = db.taskDao();

        Task task = taskDao.findTaskByUid(intent.getExtras().getLong("task_uid"));

        ((TextView) findViewById(R.id.taskDetailTitle)).setText(task.getTitle());
        ((TextView) findViewById(R.id.taskDetailBody)).setText(task.getBody());
        ((TextView) findViewById(R.id.taskDetailState)).setText(task.getState());
    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        return NavigationUI.navigateUp(navController, appBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
}