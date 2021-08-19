package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
//import com.example.taskmaster.room.AppDatabase;
//import com.example.taskmaster.room.TaskDao;

public class TaskDetail extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
//    private AppDatabase db;
//    private TaskDao taskDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        Intent intent = getIntent();
//        db = Room.databaseBuilder(getApplicationContext(),AppDatabase.class, "Task").allowMainThreadQueries().build();
//        taskDao = db.taskDao();

        Amplify.API.query(
                ModelQuery.get(com.amplifyframework.datastore.generated.model.Task.class, intent.getExtras().getString("task_id")),
                response -> {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            ((TextView) findViewById(R.id.taskDetailTitle)).setText(response.getData().getTitle());
                            ((TextView) findViewById(R.id.taskDetailBody)).setText(response.getData().getBody());
                            ((TextView) findViewById(R.id.taskDetailState)).setText(response.getData().getState());

                        }
                    });

                },
                error -> Log.e("MyAmplifyApp", error.toString(), error)
        );

//        Task task = taskDao.findTaskByUid(intent.getExtras().getString("task_id"));


    }

//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        return NavigationUI.navigateUp(navController, appBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
}