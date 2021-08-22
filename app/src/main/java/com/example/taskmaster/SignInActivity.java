package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;

public class SignInActivity extends AppCompatActivity {
    private Boolean isUserEmpty = true;
    private Boolean isPasswordEmpty = true;
    private static final String TAG = "SIGNIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        configAmplify();
//        Amplify.Auth.resetPassword(
//                "mahmoud",
//                result -> Log.i("AuthQuickstart", result.toString()),
//                error -> Log.e("AuthQuickstart", error.toString())
//        );
//        Amplify.Auth.confirmResetPassword(
//                "123456789",
//                "117160",
//                () -> Log.i("AuthQuickstart", "New password confirmed"),
//                error -> Log.e("AuthQuickstart", error.toString())
//        );

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor preferenceEditor = preferences.edit();
        findViewById(R.id.btnSignIn).setEnabled(!isPasswordEmpty && !isUserEmpty);

        TextView userNameSignIn = findViewById(R.id.userNameSignIn);
        userNameSignIn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //findViewById(R.id.btnSignIn).setEnabled(false);
                isUserEmpty = editable.toString().equals("");
                findViewById(R.id.btnSignIn).setEnabled(!isPasswordEmpty && !isUserEmpty);

            }
        });
        TextView passwordSignIn = findViewById(R.id.passwordSignIn);
        passwordSignIn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isPasswordEmpty = editable.toString().equals("");

                findViewById(R.id.btnSignIn).setEnabled(!isPasswordEmpty && !isUserEmpty);
            }
        });

        findViewById(R.id.btnSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = ((TextView) findViewById(R.id.userNameSignIn)).getText().toString();
                String password = ((TextView) findViewById(R.id.passwordSignIn)).getText().toString();

                Amplify.Auth.signIn(
                        userName,
                        password,
                        result -> {
                            Log.i("AuthQuickstart", result.isSignInComplete() ? "Sign in succeeded" : "Sign in not complete");

                            preferenceEditor.putString("userName", userName);
                            Intent signInToHome = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(signInToHome);
                        },
                        error -> Log.e("AuthQuickstart", error.toString())
                );
            }
        });

        findViewById(R.id.btnSignupSignIn).setOnClickListener(view -> {
            Intent signInToSignUp = new Intent(getApplicationContext(), SignupActivity.class);
            startActivity(signInToSignUp);
        });
    }

    private void configAmplify(){
        try {
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.configure(getApplicationContext());

            Log.i("Tutorial", "Initialized Amplify");
        } catch (AmplifyException e) {
            Log.e("Tutorial", "Could not initialize Amplify", e);
        }
    }
}