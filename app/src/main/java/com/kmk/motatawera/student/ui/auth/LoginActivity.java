package com.kmk.motatawera.student.ui.auth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.kmk.motatawera.student.R;
import com.kmk.motatawera.student.databinding.ActivityLoginBinding;
import com.kmk.motatawera.student.model.StudentModel;
import com.kmk.motatawera.student.storage.SharedPrefManager;
import com.kmk.motatawera.student.ui.main.MainActivity;

import static com.kmk.motatawera.student.util.Hide_Keyboard.hideKeyboard;
import static com.kmk.motatawera.student.util.ShowAlert.SHOW_ALERT;

public class LoginActivity extends AppCompatActivity {
    // يتم تعريف object من نوع binding لربط ملف ال xml بملف ال java
    private ActivityLoginBinding binding;
    // يتم تعريف object من نوع FirebaseFirestore للتعامل مع data base firestore
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // نقوم بعمل init  للمتغيرات

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        db = FirebaseFirestore.getInstance();

        binding.btnLogin.setOnClickListener(v -> validationData());
    }


    private void validationData() {
// نقوم بوضع مجموعه من ال validation لتجنب اخطاء المستخدم
        String id = binding.etID.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (id.isEmpty()) {
            SHOW_ALERT(this, getString(R.string.please_enter_your_id));
            return;
        }
        if (password.isEmpty()) {
            SHOW_ALERT(this, getString(R.string.please_enter_your_password));
            return;
        }
// هذا الكود خاص باخفاء الكيبورد عن الضغط علي زر Login
        hideKeyboard(this);
        if (!password.isEmpty() && !id.isEmpty()) {
            checkTimeValidation(id, password);
        }


    }

    private void checkTimeValidation(String id, String password) {

        // run progress
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();


        db.collection("time_validation")
                .document("1")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        boolean isOnline = task.getResult().getBoolean("isOnline").booleanValue();
                        progressDialog.dismiss();
                        if (isOnline) {
                            userLogin(id, password);
                        } else {
                            Toast.makeText(this, "time out ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    private void userLogin(String id, String password) {

        // run progress
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();


        db.collection("student")
                .whereEqualTo("id", id)
                .whereEqualTo("password", password).addSnapshotListener((value, error) -> {
            if (error == null) {
                assert value != null;
                if (!value.isEmpty()) {
                    for (DocumentChange documentChange : value.getDocumentChanges()) {

                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                            boolean isDisable = documentChange.getDocument().getBoolean("disable");
                            progressDialog.dismiss();
                            if (isDisable) {
                                Toast.makeText(this, R.string.account_has_been_disable, Toast.LENGTH_SHORT).show();
                            } else {

                                if (documentChange.getDocument().getString("password").equals(id)) {

                                    Intent intent = new Intent(this, AddUserDataActivity.class);
                                    intent.putExtra("id", documentChange.getDocument().getId());
                                    startActivity(intent);

                                    finish();
                                } else {

                                    SharedPrefManager.getInstance().saveUser(this, documentChange.getDocument().toObject(StudentModel.class));
                                    goToMain();
                                }


                            }


                        }

                    }
                } else {
                    progressDialog.dismiss();
                   Toast.makeText(this, "User name or password incorrect", Toast.LENGTH_SHORT).show();

                }
            } else {
                progressDialog.dismiss();
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void goToMain() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (SharedPrefManager.getInstance().isLogin(this)) goToMain();
    }


}

