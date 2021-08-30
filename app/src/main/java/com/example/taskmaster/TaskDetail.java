package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.StorageItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Objects;
//import com.example.taskmaster.room.AppDatabase;
//import com.example.taskmaster.room.TaskDao;

public class TaskDetail extends AppCompatActivity implements OnMapReadyCallback {
    private AppBarConfiguration appBarConfiguration;
    private String fileURL;
    private FirebaseAnalytics mFirebaseAnalytics;
//    private AppDatabase db;
//    private TaskDao taskDao;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);


        findViewById(R.id.addTaskMenu).setOnClickListener(view -> {
            Intent goToAddTask = new Intent(getBaseContext(), AddTask.class);
            startActivity(goToAddTask);
        });

        TextView fileLinkDetail = findViewById(R.id.fileLinkDetail);
        fileLinkDetail.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(fileURL));
            startActivity(i);
        });

        Intent intent = getIntent();
//        db = Room.databaseBuilder(getApplicationContext(),AppDatabase.class, "Task").allowMainThreadQueries().build();
//        taskDao = db.taskDao();

        if (intent.getStringExtra("fileName") != null) {

            Amplify.Storage.getUrl(
                    intent.getStringExtra("fileName"),
                    result -> {
                        Log.i("MyAmplifyApp", "Successfully generated: " + result.getUrl());
                        runOnUiThread(() -> {
                            if (intent.getStringExtra("fileName").endsWith("png")
                                    || intent.getStringExtra("fileName").endsWith("jpg")
                                    || intent.getStringExtra("fileName").endsWith("jpeg")
                                    || intent.getStringExtra("fileName").endsWith("gif")) {
                                ImageView taskImageDetail = findViewById(R.id.taskImageDetail);

                                Picasso.get().load(String.valueOf(result.getUrl())).into(taskImageDetail);

                                taskImageDetail.setVisibility(View.VISIBLE);
                            }else{
                                fileURL = String.valueOf(result.getUrl());
//                                String link = "<a href=\""+ result.getUrl() + "\">Download the linked file</a>";
                                fileLinkDetail.setVisibility(View.VISIBLE);
                            }
                        });
                    },
                    error -> Log.e("MyAmplifyApp", "URL generation failure", error)
            );
        }

        ((TextView) findViewById(R.id.taskDetailTitle)).setText(intent.getStringExtra("task_title"));
        ((TextView) findViewById(R.id.taskDetailBody)).setText(intent.getStringExtra("task_body"));
        ((TextView) findViewById(R.id.taskDetailState)).setText(intent.getStringExtra("task_state"));
        ((TextView) findViewById(R.id.latTextView)).setText(String.valueOf(intent.getDoubleExtra("lat", 0)) );
        ((TextView) findViewById(R.id.lonTextView)).setText(String.valueOf(intent.getDoubleExtra("lon", 0)));

//        Amplify.API.query(
//                ModelQuery.get(com.amplifyframework.datastore.generated.model.Task.class, intent.getExtras().getString("task_id")),
//                response -> {
//                    runOnUiThread(() -> {
//
//                        ((TextView) findViewById(R.id.taskDetailTitle)).setText(response.getData().getTitle());
//                        ((TextView) findViewById(R.id.taskDetailBody)).setText(response.getData().getBody());
//                        ((TextView) findViewById(R.id.taskDetailState)).setText(response.getData().getState());
//
//                    });
//
//                },
//                error -> Log.e("MyAmplifyApp", error.toString(), error)
//        );

//        Task task = taskDao.findTaskByUid(intent.getExtras().getString("task_id"));

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "TaskDetail");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "TaskDetail");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Page");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng sydney = new LatLng(getIntent().getDoubleExtra("lat", 0), getIntent().getDoubleExtra("lon", 0));
        googleMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}