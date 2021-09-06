package com.kmk.motatawera.student.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kmk.motatawera.student.R;
import com.kmk.motatawera.student.storage.SharedPrefManager;
import com.kmk.motatawera.student.ui.splash.SplashActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firestore = FirebaseFirestore.getInstance();
        initNav();

    }



    private void initNav() {

        BottomNavigationView navigation = findViewById(R.id.navigation);
        AppBarConfiguration configuration = new AppBarConfiguration.Builder(
                R.id.nav_subject,
                R.id.nav_quiz,
                R.id.nav_schedule,
                R.id.nav_profile
        ).build();


        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navigation, navController);

        NavigationUI.setupActionBarWithNavController(this, navController, configuration);

        navigation.setOnNavigationItemSelectedListener(menuItem -> {

            switch (menuItem.getItemId()) {
                case R.id.nav_subject:
                    navController.navigate(R.id.nav_subject);
                    return true;

                case R.id.nav_quiz:
                    navController.navigate(R.id.nav_quiz);
                    return true;

                case R.id.nav_schedule:
                    navController.navigate(R.id.nav_schedule);
                    return true;

                case R.id.nav_profile:
                    navController.navigate(R.id.nav_profile);
                    return true;
            }

            return false;
        });

    }

    private void updateToken() {
        String uid = SharedPrefManager.getInstance().getUser(this).getId();
        if (uid != null) {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            String token = task.getResult();
                            FirebaseFirestore.getInstance().collection("student").document(uid).get()
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            DocumentReference db = FirebaseFirestore.getInstance()
                                                    .collection("student")
                                                    .document(uid);
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("token", token);
                                            db.update(map);
                                        }
                                    });
                        }
                    });
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        updateToken();
        checkValidationTime();
    }

    private void checkValidationTime() {
        firestore.collection("time_validation")
                .document("1")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean isOnline = task.getResult().getBoolean("isOnline");
                        if (!isOnline) {
                            new AlertDialog.Builder(this)
                                    .setMessage(R.string.time_out)
                                    .setTitle(R.string.error)
                                    .setPositiveButton(R.string.ok, (dialog, which) -> {
                                        getSharedPreferences("login", MODE_PRIVATE)
                                                .edit()
                                                .clear()
                                                .apply();
                                        startActivity(new Intent(this, SplashActivity.class));

                                        finish();
                                    })
                                    .create().show();
                        }
                    } else
                        Toast.makeText(this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                });

    }
}