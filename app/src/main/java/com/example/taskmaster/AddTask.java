package com.example.taskmaster;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.taskmaster.room.AppDatabase;
import com.example.taskmaster.room.TaskDao;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class AddTask extends AppCompatActivity {
    private AppDatabase db;
    private TaskDao taskDao;

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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, stateListSpinner);
        //set the spinners adapter to the previously created one.

        stateSpinner.setAdapter(adapter);

        db = Room.databaseBuilder(getApplicationContext(),AppDatabase.class, "Task").allowMainThreadQueries().build();
        taskDao = db.taskDao();

        ((TextView) findViewById(R.id.tasks_count)).setText("Total tasks: "+ taskDao.findAll().size());

        findViewById(R.id.addTask).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                String taskTitle = Objects.requireNonNull(((TextInputLayout) findViewById(R.id.taskTitle)).getEditText()).getText().toString();
                String taskBody = Objects.requireNonNull(((TextInputLayout) findViewById(R.id.taskDescription)).getEditText()).getText().toString();
                String taskState = ((Spinner) findViewById(R.id.task_state_spinner)).getSelectedItem().toString();
                Task newTask = new Task(taskBody, taskTitle, taskState);

                ((TextView) findViewById(R.id.tasks_count)).setText("Total tasks: "+ taskDao.findAll().size());

                taskDao.addTask(newTask);

                Toast.makeText(getBaseContext(), "submitted!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}