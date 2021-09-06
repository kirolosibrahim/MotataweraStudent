package com.kmk.motatawera.student.ui.splash;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.kmk.motatawera.student.R;
import com.kmk.motatawera.student.ui.auth.LoginActivity;

import java.util.Locale;

import static com.kmk.motatawera.student.app.Const.SHARED_PREF_IS_DARK;
import static com.kmk.motatawera.student.app.Const.SHARED_PREF_LANGUAGE;
import static com.kmk.motatawera.student.app.Const.SHARED_PREF_THEME;
import static com.kmk.motatawera.student.app.Const.SHARED_PREF_lANG;


public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSaveTheme();
        getLanguage();

        new Handler().postDelayed(() -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }, 3000);

    }

    void getSaveTheme() {
        if (getSharedPreferences(SHARED_PREF_THEME, MODE_PRIVATE).getBoolean(SHARED_PREF_IS_DARK, false))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    void getLanguage() {

        String currentLangDevice = Locale.getDefault().getDisplayLanguage();
        String lang = getSharedPreferences(SHARED_PREF_LANGUAGE, MODE_PRIVATE).getString(SHARED_PREF_lANG, currentLangDevice);

        assert lang != null;
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

    }

}

