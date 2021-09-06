package com.kmk.motatawera.student.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kmk.motatawera.student.R;
import com.kmk.motatawera.student.adapter.SubjectAdapter;
import com.kmk.motatawera.student.databinding.FragmentSubjectBinding;
import com.kmk.motatawera.student.model.StudentModel;
import com.kmk.motatawera.student.model.SubjectModel;
import com.kmk.motatawera.student.storage.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class SubjectFragment extends Fragment {

    private FragmentSubjectBinding binding;
    private SubjectAdapter adapter;
    private FirebaseFirestore db;
    private List<SubjectModel> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_subject, container, false);
        db = FirebaseFirestore.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.progressBarSubject.setVisibility(View.VISIBLE);
        list = new ArrayList<>();

        binding.recyclerViewSubject.setHasFixedSize(true);
        binding.recyclerViewSubject.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        adapter = new SubjectAdapter(getActivity(), list);
        getSubjectList();
    }

    private void getSubjectList() {
        StudentModel model = SharedPrefManager.getInstance().getUser(getActivity());
        db.collection("subject")
                .whereEqualTo("branch", model.getBranch())
                .whereEqualTo("department", model.getDepartment())
                .whereEqualTo("grad", model.getGrad())
                .addSnapshotListener((value, error) -> {
                    if (error == null) {
                        binding.progressBarSubject.setVisibility(View.GONE);
                        if (value == null) {
                            binding.progressBarSubject.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            for (DocumentChange documentChange : value.getDocumentChanges()) {
                                switch (documentChange.getType()) {
                                    case ADDED:
                                        onDocumentAdded(documentChange);
                                        break;
                                    case MODIFIED:
                                        onDocumentModified(documentChange);
                                        break;
                                    case REMOVED:
                                        onDocumentRemoved(documentChange);
                                        break;
                                }
                                binding.progressBarSubject.setVisibility(View.GONE);
                                binding.recyclerViewSubject.setAdapter(adapter);
                            }
                            binding.progressBarSubject.setVisibility(View.GONE);
                        }
                    } else {
                        binding.progressBarSubject.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                });

    }
    private void onDocumentAdded(DocumentChange change) {
        SubjectModel model = change.getDocument().toObject(SubjectModel.class);
        list.add(model);
        adapter.notifyItemInserted(list.size() - 1);
        adapter.getItemCount();

    }

    private void onDocumentModified(DocumentChange change) {
        try {
            SubjectModel model = change.getDocument().toObject(SubjectModel.class);
            if (change.getOldIndex() == change.getNewIndex()) {
                // Item changed but remained in same position
                list.set(change.getOldIndex(), model);
                adapter.notifyItemChanged(change.getOldIndex());
            } else {
                // Item changed and changed position
                list.remove(change.getOldIndex());
                list.add(change.getNewIndex(), model);
                adapter.notifyItemRangeChanged(0, list.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onDocumentRemoved(DocumentChange change) {
        try {
            list.remove(change.getOldIndex());
            adapter.notifyItemRemoved(change.getOldIndex());
            adapter.notifyItemRangeChanged(0, list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}