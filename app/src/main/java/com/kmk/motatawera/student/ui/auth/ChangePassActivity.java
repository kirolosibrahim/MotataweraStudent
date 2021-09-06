package com.kmk.motatawera.student.ui.auth;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.firestore.FirebaseFirestore;
import com.kmk.motatawera.student.R;
import com.kmk.motatawera.student.databinding.ActivityChangePassBinding;
import com.kmk.motatawera.student.storage.SharedPrefManager;
import com.kmk.motatawera.student.util.CheckInternetConn;

import java.util.HashMap;
import java.util.Map;

import static com.kmk.motatawera.student.util.Hide_Keyboard.hideKeyboard;
import static com.kmk.motatawera.student.util.ShowAlert.SHOW_ALERT;

public class ChangePassActivity extends AppCompatActivity {

    private ActivityChangePassBinding binding;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_pass);


        firestore = FirebaseFirestore.getInstance();

        binding.btnSave.setOnClickListener(v -> {
            if (new CheckInternetConn(this).isConnection()) {
                Toast.makeText(this, SharedPrefManager.getInstance().getUser(this).getPassword(), Toast.LENGTH_SHORT).show();
                checkValidation();
            } else SHOW_ALERT(this, getString(R.string.no_internt));
        });

    }

    private void checkValidation() {


        String oldPass = binding.oldPassword.getText().toString().trim();
        String newPass = binding.newPassword.getText().toString().trim();
        String cNewPass = binding.confirmNewPassword.getText().toString().trim();

        if (oldPass.isEmpty()) {
            binding.oldPassword.requestFocus();
            SHOW_ALERT(this, getString(R.string.please_enter_your_old_password));
            return;
        }
        if (newPass.isEmpty()) {
            binding.newPassword.requestFocus();
            SHOW_ALERT(this, getString(R.string.please_enter_your_password));
            return;
        }
        if (cNewPass.isEmpty()) {
            binding.confirmNewPassword.requestFocus();
            SHOW_ALERT(this, getString(R.string.please_enter_your_confirm_password));
            return;
        }

        if (newPass.length() < 6 & cNewPass.length() < 6) {
            SHOW_ALERT(this, getString(R.string.all_password_should_be_greater_than_6_char));
            return;
        }

        if (!newPass.equals(cNewPass)) {
            SHOW_ALERT(this, getString(R.string.new_password_should_be_equal_confirm_new_password));
            return;
        }

        if (!oldPass.equals(SharedPrefManager.getInstance().getUser(this).getPassword())) {
            SHOW_ALERT(this, getString(R.string.old_password_is_incorrect));
            return;
        }

        hideKeyboard(this);

        updatePassword();

    }

    private void updatePassword() {

        // mack progress bar dialog
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();

        Map<String, Object> map = new HashMap<>();
        map.put("password", binding.newPassword.getText().toString().trim());




        firestore.collection("student")
                .document(SharedPrefManager.getInstance().getUser(this).getId())
                .update(map)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        progressDialog.dismiss();

                        new AlertDialog.Builder(this)
                                .setMessage(R.string.password_update_successfully)
                                .setCancelable(false)
                                .setPositiveButton(R.string.ok, (dialog, which) -> {

                                    SharedPrefManager.getInstance().updatePassword(this, binding.newPassword.getText().toString().trim());

                                    finish();
                                })
                                .create()
                                .show();

                    } else {

                        SHOW_ALERT(this, task.getException().getMessage());
                        progressDialog.dismiss();

                    }

                });

    }
}