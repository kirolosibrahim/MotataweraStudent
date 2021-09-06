package com.kmk.motatawera.student.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kmk.motatawera.student.R;
import com.kmk.motatawera.student.storage.SharedPrefManager;

public class ScheduleFragment extends Fragment {


    public ScheduleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ImageView imageView = view.findViewById(R.id.image_schedule);

        int department = SharedPrefManager.getInstance().getUser(getActivity()).getDepartment();
        int branch = SharedPrefManager.getInstance().getUser(getActivity()).getBranch();
        int grad = SharedPrefManager.getInstance().getUser(getActivity()).getGrad();

        if (branch == 1 && department == 1 && grad == 1) {
            imageView.setImageResource(R.mipmap.mis_h_1_n);
        } else if (branch == 1 && department == 1 && grad == 2) {
            imageView.setImageResource(R.mipmap.mis_h_2_n);
        } else if (branch == 1 && department == 1 && grad == 3) {
            imageView.setImageResource(R.mipmap.mis_h_3_n);
        } else if (branch == 1 && department == 1 && grad == 4) {
            imageView.setImageResource(R.mipmap.mis_h_4_n);


        } else if (branch == 1 && department == 2 && grad == 1) {
            imageView.setImageResource(R.mipmap.mis_h_1_t);

        } else if (branch == 1 && department == 2 && grad == 2) {
            imageView.setImageResource(R.mipmap.mis_h_2_t);

        } else if (branch == 1 && department == 2 && grad == 3) {
            imageView.setImageResource(R.mipmap.mis_h_3_t);

        } else if (branch == 1 && department == 2 && grad == 4) {
            imageView.setImageResource(R.mipmap.mis_h_4_t);


        } else if (branch == 2 && department == 1 && grad == 1) {
            imageView.setImageResource(R.mipmap.mis_q_1_n);

        } else if (branch == 2 && department == 1 && grad == 2) {
            imageView.setImageResource(R.mipmap.mis_q_2_n);

        } else if (branch == 2 && department == 1 && grad == 3) {
            imageView.setImageResource(R.mipmap.mis_q_3_n);


        } else if (branch == 2 && department == 1 && grad == 4) {
            imageView.setImageResource(R.mipmap.mis_q_4_n);


        } else if (branch == 2 && department == 2 && grad == 1) {
            imageView.setImageResource(R.mipmap.mis_q_1_t);

        } else if (branch == 2 && department == 2 && grad == 2) {
            imageView.setImageResource(R.mipmap.mis_q_2_t);

        } else if (branch == 2 && department == 2 && grad == 3) {
            imageView.setImageResource(R.mipmap.mis_q_3_t);

        } else if (branch == 2 && department == 2 && grad == 4) {
            imageView.setImageResource(R.mipmap.mis_q_4_t);

        }
    }
}