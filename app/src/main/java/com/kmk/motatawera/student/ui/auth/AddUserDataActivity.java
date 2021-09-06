package com.kmk.motatawera.student.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kmk.motatawera.student.R;
import com.kmk.motatawera.student.databinding.ActivityAddUserDataBinding;
import com.kmk.motatawera.student.model.StudentModel;

import java.util.HashMap;
import java.util.Map;

public class AddUserDataActivity extends AppCompatActivity {

    private ActivityAddUserDataBinding binding;
    private String phone, email, name, password, confirm_password;

    private FirebaseFirestore firestore;
    private String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_user_data);
        firestore = FirebaseFirestore.getInstance();
        id = getIntent().getStringExtra("id");

        binding.btnSave.setOnClickListener(v -> {

            validationData();

        });
    }

    private void validationData() {
        email = binding.email.getText().toString().trim();
        phone = binding.phone.getText().toString().trim();
        name = binding.name.getText().toString().trim();
        password = binding.newPassword.getText().toString().trim();
        confirm_password = binding.confirmNewPassword.getText().toString().trim();


        if (email.isEmpty()) {
            binding.email.requestFocus();
            showAlert(getString(R.string.email_is_required));
            return;

        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.email.requestFocus();
            showAlert(getString(R.string.invaild_emaill_address_email_must_be_yourname_company_com));
            return;
        }
        if (!password.equals(confirm_password)) {
            binding.newPassword.requestFocus();

            showAlert(getString(R.string.password_not_equal_confirm_password));
            return;
        }
        if (password.length() < 6) {
            binding.newPassword.requestFocus();

            showAlert(getString(R.string.password_must));
            return;
        }

        if (name.isEmpty()) {
            binding.name.requestFocus();
            showAlert(getString(R.string.name_is_requried));
            return;
        }
        if (phone.isEmpty()) {
            binding.phone.requestFocus();
            showAlert(getString(R.string.phone_is_requied));
            return;
        }
        if (phone.length() < 11) {
            binding.phone.requestFocus();
            showAlert(getString(R.string.invalid_phone_required));
            return;
        }
        savewithfirebase();
    }

    private void savewithfirebase() {


        DocumentReference mm = firestore.collection("student").document(id);
        Map<String, Object> user = new HashMap<>();

        user.put("password", password);
        user.put("email", email);
        user.put("name", name);
        user.put("phone", phone);

        mm.update(user).addOnCompleteListener(task -> {

            Toast.makeText(this, R.string.data_added, Toast.LENGTH_SHORT).show();
            goToLogin();

        });


    }

    private void goToLogin() {
        finish();
        startActivity(new Intent(AddUserDataActivity.this, LoginActivity.class));

    }


    void showAlert(String msg) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.error)
                .setMessage(msg)
                .setPositiveButton(R.string.ok, null)
                .create().show();
    }

}