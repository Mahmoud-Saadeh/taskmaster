package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor preferenceEditor = settings.edit();

        findViewById(R.id.saveUserName).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textInputLayout = ((TextView) findViewById(R.id.userName)).getText().toString();
//                String textInputLayout = Objects.requireNonNull(((TextInputLayout) findViewById(R.id.userName)).getEditText()).getText().toString();

                preferenceEditor.putString("userName", textInputLayout);

                Toast.makeText(getApplicationContext(), textInputLayout  + " has been saved", Toast.LENGTH_SHORT).show();
                preferenceEditor.apply();
            }
        });


    }
}