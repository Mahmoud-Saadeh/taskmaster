package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.TextView;

import com.amplifyframework.core.Amplify;

public class ConfirmSignUpActivity extends AppCompatActivity {
    private Boolean isCodeEmpty = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_sign_up);

        Intent intent = getIntent();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);


        findViewById(R.id.btnVerificationCode).setEnabled(!isCodeEmpty);

        TextView confirmCode = findViewById(R.id.verificationCodeInput);
        confirmCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isCodeEmpty = editable.toString().equals("");

                findViewById(R.id.btnVerificationCode).setEnabled(!isCodeEmpty);
            }
        });

        findViewById(R.id.btnVerificationCode).setOnClickListener(view -> {
            Amplify.Auth.confirmSignUp(
                    intent.getStringExtra("userName"),
                    confirmCode.getText().toString(),
                    result -> {
                        Log.i("AuthQuickstart", result.isSignUpComplete() ? "Confirm signUp succeeded" : "Confirm sign up not complete");
                        silentSignIn(intent.getStringExtra("userName"),intent.getStringExtra("password"), preferences);
                    },
                    error -> Log.e("AuthQuickstart", error.toString())
            );
        });
    }

    private void silentSignIn(String userName, String password,SharedPreferences preferences){
        Amplify.Auth.signIn(
                userName,
                password,
                result -> {
                    Log.i("AuthQuickstart", result.isSignInComplete() ? "Sign in succeeded" : "Sign in not complete");
                    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor preferenceEditor = preferences.edit();
                    preferenceEditor.putString("userName", userName);

                    Intent signInToHome = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(signInToHome);
                },
                error -> Log.e("AuthQuickstart", error.toString())
        );
    }
}