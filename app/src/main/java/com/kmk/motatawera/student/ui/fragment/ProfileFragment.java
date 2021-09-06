package com.kmk.motatawera.student.ui.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.kmk.motatawera.student.R;
import com.kmk.motatawera.student.databinding.FragmentProfileBinding;
import com.kmk.motatawera.student.model.StudentModel;
import com.kmk.motatawera.student.storage.SharedPrefManager;
import com.kmk.motatawera.student.ui.auth.ChangePassActivity;
import com.kmk.motatawera.student.ui.auth.UpdateProfileActivity;
import com.kmk.motatawera.student.ui.splash.SplashActivity;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.kmk.motatawera.student.app.Const.SHARED_PREF_IS_DARK;
import static com.kmk.motatawera.student.app.Const.SHARED_PREF_LANGUAGE;
import static com.kmk.motatawera.student.app.Const.SHARED_PREF_THEME;
import static com.kmk.motatawera.student.app.Const.SHARED_PREF_lANG;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private String getLang;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //getLanguage
        setDefaultLang();

        //get theme
        setDefaultTheme();

        //change lang
        binding.btnLang.setOnClickListener(v -> showAlertLanguage());

        //change theme
        binding.switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                savedTheme(true);

            } else {
                savedTheme(false);
            }
            refreshApp();
        });

        //change pass
        binding.changePass.setOnClickListener(v -> startActivity(new Intent(getActivity(), ChangePassActivity.class)));

        //update profile
        binding.updateProfile.setOnClickListener(v -> startActivity(new Intent(getActivity(), UpdateProfileActivity.class)));

        //logout
        binding.logout.setOnClickListener(v -> new AlertDialog.Builder(getActivity())
                .setMessage(R.string.sure_want_to_logout)
                .setPositiveButton(R.string.no, null)
                .setNegativeButton(R.string.yes, (dialog, which) -> {
                    SharedPrefManager.getInstance().logOut(getActivity());
                    refreshApp();
                }).create().show());
    }

    private void setDefaultTheme() {
        if (getActivity().getSharedPreferences(SHARED_PREF_THEME, MODE_PRIVATE).getBoolean(SHARED_PREF_IS_DARK, false))
            binding.switchTheme.setChecked(true);
        else binding.switchTheme.setChecked(false);
    }

    private void setDefaultLang() {
        String currentLang = Locale.getDefault().getDisplayLanguage();
        getLang = getActivity().getSharedPreferences(SHARED_PREF_LANGUAGE, MODE_PRIVATE).getString(SHARED_PREF_lANG, currentLang);
        assert getLang != null;
        if (getLang.equals("ar")) binding.languageName.setText("عربى");
        else binding.languageName.setText("English");
    }


    private void savedTheme(boolean b) {
        getActivity().getSharedPreferences(SHARED_PREF_THEME, MODE_PRIVATE)
                .edit()
                .putBoolean(SHARED_PREF_IS_DARK, b)
                .apply();

        if (b) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

    }

    private void showAlertLanguage() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.setCancelable(false);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_language, null);

        dialog.setView(view);

        view.findViewById(R.id.img_close).setOnClickListener(v -> dialog.dismiss());

        RadioButton radioEN = view.findViewById(R.id.radio_en);
        RadioButton radioAR = view.findViewById(R.id.radio_ar);

        if (getLang.equals("ar")) {
            radioAR.setChecked(true);
            radioEN.setChecked(false);
        } else {
            radioEN.setChecked(true);
            radioAR.setChecked(false);
        }

        view.findViewById(R.id.save_lang).setOnClickListener(v -> {
            if (radioEN.isChecked()) {
                saveLang("en");
            } else if (radioAR.isChecked()) {
                saveLang("ar");
            }
        });

        dialog.show();
    }

    void saveLang(String lang) {

        getActivity().getSharedPreferences(SHARED_PREF_LANGUAGE, MODE_PRIVATE)
                .edit()
                .putString(SHARED_PREF_lANG, lang)
                .apply();

        Locale l = new Locale(lang);
        Locale.setDefault(l);
        Configuration configuration = new Configuration();
        configuration.locale = l;
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

        refreshApp();
    }

    private void refreshApp() {
        Intent intent = new Intent(getActivity(), SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();

        StudentModel model = SharedPrefManager.getInstance().getUser(getActivity());

        if (model.getName() != null & model.getPhoto() != null & model.getId() != null) {
            binding.studentName.setText(model.getName());
            binding.studentId.setText(model.getId());
            Glide.with(getActivity())
                    .load(model.getPhoto())
                    .centerCrop()
                    .placeholder(R.drawable.user_placeholder)
                    .into(binding.studentPhoto);
        } else binding.studentName.setText(R.string.complete_your_profile);

    }
}