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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

        findViewById(R.id.addTaskMenu).setOnClickListener(view -> {
            Intent goToAddTask = new Intent(getBaseContext(), AddTask.class);
            startActivity(goToAddTask);
        });

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