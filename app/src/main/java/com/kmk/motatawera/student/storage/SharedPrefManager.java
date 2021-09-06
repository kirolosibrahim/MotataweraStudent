package com.kmk.motatawera.student.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.kmk.motatawera.student.model.StudentModel;

import static com.kmk.motatawera.student.app.Const.SHARED_PREF_STUDENT;


public class SharedPrefManager {

    private static SharedPrefManager mInstance;

    public static synchronized SharedPrefManager getInstance() {
        if (mInstance == null)
            mInstance = new SharedPrefManager();
        return mInstance;
    }

    public void saveUser(Context context, StudentModel userModel) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF_STUDENT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("id", userModel.getId());
        editor.putInt("branch", userModel.getBranch());
        editor.putInt("department", userModel.getDepartment());
        editor.putInt("grad", userModel.getGrad());
        editor.putString("name", userModel.getName());
        editor.putString("phone", userModel.getPhone());
        editor.putString("photo", userModel.getPhoto());
        editor.putString("email", userModel.getEmail());
        editor.putInt("gender", userModel.getGender());
        editor.putString("password", userModel.getPassword());
        editor.putBoolean("isDisabled", userModel.isDisable());
        editor.apply();
    }

    public void updateProfile(Context context, String name, String phone, String image) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF_STUDENT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("name", name);
        editor.putString("phone", phone);
        editor.putString("photo", image);
        editor.apply();
    }

    public void updatePassword(Context context, String newPassword) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF_STUDENT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("password", newPassword);
        editor.apply();
    }


    public boolean isLogin(Context context) {
        return context.getSharedPreferences(SHARED_PREF_STUDENT, Context.MODE_PRIVATE).getString("id", null) != null;
    }

    public StudentModel getUser(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF_STUDENT, Context.MODE_PRIVATE);
        return new StudentModel(
                preferences.getString("id", null),
                preferences.getString("name", null),
                preferences.getString("phone", null),
                preferences.getString("photo", null),
                preferences.getString("email", null),
                preferences.getString("password", null),
                preferences.getInt("branch", -1),
                preferences.getInt("department", -1),
                preferences.getInt("grad", -1),
                preferences.getInt("gender", -1),
                preferences.getBoolean("isDisabled", true)
        );
    }

    public void logOut(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF_STUDENT, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

}