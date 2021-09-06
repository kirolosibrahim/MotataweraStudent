package com.kmk.motatawera.student.ui.auth;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kmk.motatawera.student.R;
import com.kmk.motatawera.student.databinding.ActivityUpdateProfileBinding;
import com.kmk.motatawera.student.model.StudentModel;
import com.kmk.motatawera.student.storage.SharedPrefManager;
import com.kmk.motatawera.student.util.CheckInternetConn;

import java.util.HashMap;
import java.util.Map;

import static com.kmk.motatawera.student.util.Hide_Keyboard.hideKeyboard;
import static com.kmk.motatawera.student.util.ShowAlert.SHOW_ALERT;

public class UpdateProfileActivity extends AppCompatActivity {

    private ActivityUpdateProfileBinding binding;

    private Uri imageUri = null;
    private static String ImageURL = null;

    private FirebaseFirestore firestore;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_profile);

        //setup toolbar
      //  setSupportActionBar(binding.toolbarUpdateProfile);
       // binding.toolbarUpdateProfile.setNavigationOnClickListener(v -> finish());

        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        StudentModel model = SharedPrefManager.getInstance().getUser(this);

        String name = model.getName();
        String image = model.getPhoto();
        String phone = model.getPhone();

        if (name != null) binding.studentName.setText(name);
        if (phone != null) binding.studentPhone.setText(phone);
        if (image != null)
            Glide.with(this).load(image).placeholder(R.drawable.user_placeholder).into(binding.studentPhoto);

        binding.selectPhoto.setOnClickListener(v -> {
            if (new CheckInternetConn(this).isConnection()) checkPermission();
            else SHOW_ALERT(this, getString(R.string.no_internt));

        });

        binding.btnSaveUpdateProfile.setOnClickListener(v -> {
            if (new CheckInternetConn(this).isConnection()) checkValidation();
            else SHOW_ALERT(this, getString(R.string.no_internt));
        });
    }

    private void checkValidation() {
        String name = binding.studentName.getText().toString().trim();
        String phone = binding.studentPhone.getText().toString().trim();

        if (name.isEmpty()) {
            binding.studentName.requestFocus();
            SHOW_ALERT(this, getString(R.string.name_is_requried));
            return;
        }
        if (phone.isEmpty()) {
            binding.studentPhone.requestFocus();
            SHOW_ALERT(this, getString(R.string.phone_is_requied));
            return;
        }
        if (phone.length() < 11) {
            binding.studentPhone.requestFocus();
            SHOW_ALERT(this, getString(R.string.invalid_number));
            return;
        }

        hideKeyboard(this);

        if (imageUri != null) uploadImage();
        else saveChange();

    }

    private void uploadImage() {

        // chick if user image is null or not
        if (imageUri != null) {

            String userID = SharedPrefManager.getInstance().getUser(this).getId();

            // mack progress bar dialog
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(getString(R.string.please_wait));
            progressDialog.setCancelable(false);
            progressDialog.show();

            // mack collection in fireStorage
            final StorageReference ref = storageReference.child("profile_image_user").child(userID + ".jpg");

            // get image user and give to imageUserPath
            ref.putFile(imageUri).addOnProgressListener(taskSnapshot -> {

                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.setMessage(getString(R.string.upload) + (int) progress + "%");

            }).continueWithTask(task -> {
                if (!task.isSuccessful()) {

                    throw task.getException();

                }
                return ref.getDownloadUrl();

            }).addOnCompleteListener(task -> {

                if (task.isSuccessful()) {

                    progressDialog.dismiss();
                    Uri downloadUri = task.getResult();

                    assert downloadUri != null;
                    ImageURL = downloadUri.toString();
                    progressDialog.dismiss();
                    saveChange();

                } else {

                    progressDialog.dismiss();
                    Toast.makeText(UpdateProfileActivity.this, " Error in addOnCompleteListener " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }

    }

    private void saveChange() {

        // mack progress bar dialog
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();

        String userID = SharedPrefManager.getInstance().getUser(this).getId();

        Map<String, Object> map = new HashMap<>();
        map.put("name", binding.studentName.getText().toString());
        map.put("phone", binding.studentPhone.getText().toString().trim());
        String url;
        if (ImageURL != null) url = ImageURL;
        else url = SharedPrefManager.getInstance().getUser(this).getPhoto();
        map.put("photo", url);

        firestore.collection("student")
                .document(userID)
                .update(map)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        SharedPrefManager.getInstance().updateProfile(
                                this,
                                binding.studentName.getText().toString().trim(),
                                binding.studentPhone.getText().toString().trim(),
                                url
                        );
                        progressDialog.dismiss();
                        new AlertDialog.Builder(this)
                                .setMessage(R.string.profile_update_successfully)
                                .setCancelable(false)
                                .setPositiveButton(R.string.ok, (dialog, which) -> {

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

    private void checkPermission() {

        //use permission to READ_EXTERNAL_STORAGE For Device >= Marshmallow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(UpdateProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // to ask user to reade external storage
                ActivityCompat.requestPermissions(UpdateProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            } else {
                OpenGalleryImagePicker();
            }

            //implement code for device < Marshmallow
        } else {

            OpenGalleryImagePicker();
        }
    }

    private void OpenGalleryImagePicker() {
        // start picker to get image for cropping and then use the image in cropping activity
//        CropImage.activity()
//                .setGuidelines(CropImageView.Guidelines.ON) // to show option camera, gallery, derive
//                .setAllowFlipping(false) // to set width equal height
//                .setAspectRatio(CropImageView.MEASURED_SIZE_MASK, CropImageView.MEASURED_SIZE_MASK) // to set min width and height "parameter is min of image"
//                .start(this); // to start cropping activity
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//
//            if (resultCode == RESULT_OK) {
//
//                imageUri = result.getUri();
//                binding.studentPhoto.setImageURI(imageUri);
//
//                // uploadImage();
//
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//
//                Exception error = result.getError();
//                Toast.makeText(UpdateProfileActivity.this, "Error : " + error, Toast.LENGTH_LONG).show();
//
//            }
//        }
    }
}