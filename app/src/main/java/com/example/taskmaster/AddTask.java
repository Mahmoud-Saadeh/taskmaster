package com.example.taskmaster;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
//import com.example.taskmaster.room.AppDatabase;
//import com.example.taskmaster.room.TaskDao;
import com.amplifyframework.datastore.generated.model.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AddTask extends AppCompatActivity {
//    private AppDatabase db;
//    private TaskDao taskDao;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task);

        //get the spinner from the xml.
        Spinner stateSpinner = findViewById(R.id.task_state_spinner);
        //create a list of items for the spinner.
        String[] stateListSpinner = new String[]{TaskStates.NEW.toString(),
                TaskStates.COMPLETE.toString(),
                TaskStates.ASSIGNED.toString(),
                TaskStates.IN_PROGRESS.toString()};

        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        ArrayAdapter<String> stateSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, stateListSpinner);
        //set the spinners adapter to the previously created one.
        stateSpinner.setAdapter(stateSpinnerAdapter);

        Spinner teamSpinner = findViewById(R.id.task_team_spinner);
        List<String> teamNameList = new ArrayList<>();
        List<Team> teamList = new ArrayList<>();
        Amplify.API.query(ModelQuery.list(Team.class),
                response -> {
                    for (Team team: response.getData()) {
                        teamNameList.add(team.getName());
                        teamList.add(team);
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

//        db = Room.databaseBuilder(getApplicationContext(),AppDatabase.class, "Task").allowMainThreadQueries().build();
//        taskDao = db.taskDao();

//        ((TextView) findViewById(R.id.tasks_count)).setText("Total tasks: "+ taskDao.findAll().size());

        findViewById(R.id.addTask).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {

                String taskTitle = ((TextView) findViewById(R.id.taskTitle)).getText().toString();
                String taskBody = ((TextView) findViewById(R.id.taskDescription)).getText().toString();
                String taskState = ((Spinner) findViewById(R.id.task_state_spinner)).getSelectedItem().toString();
                String taskTeam = ((Spinner) findViewById(R.id.task_team_spinner)).getSelectedItem().toString();
                String teamId = "";
                for (Team team: teamList) {
                    if (team.getName().equals(taskTeam)){
                        teamId = team.getId();
                    }
                }

                  Task task = Task.builder().title(taskTitle).teamId(teamId).body(taskBody).state(taskState).build();

                Amplify.API.mutate(ModelMutation.create(task),
                        response -> {
                            Log.i("MyAmplifyApp", "Todo with id: " + response.getData().getId());
//                            taskDao.addTask(task);
                        },
                        error -> Log.e("MyAmplifyApp", "Create failed", error)
                );

                Toast.makeText(getBaseContext(), "submitted!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}