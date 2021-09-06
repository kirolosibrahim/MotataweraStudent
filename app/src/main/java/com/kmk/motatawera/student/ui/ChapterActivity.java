package com.kmk.motatawera.student.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kmk.motatawera.student.R;
import com.kmk.motatawera.student.adapter.ChapterAdapter;
import com.kmk.motatawera.student.databinding.ActivityChapterBinding;
import com.kmk.motatawera.student.model.ChapterModel;

import java.util.ArrayList;
import java.util.List;


public class ChapterActivity extends AppCompatActivity {
    private ActivityChapterBinding binding;
    private ChapterAdapter adapter;
    private List<ChapterModel> list;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chapter);
        binding.setLifecycleOwner(this);

        firestore = FirebaseFirestore.getInstance();

        String subject_name = getIntent().getStringExtra("subject_name");
        binding.Subjectname.setText(subject_name);

        binding.recyclerview.setHasFixedSize(true);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new ChapterAdapter(list, this);

        getList();




    }

    private void getList() {
        String subject_id = getIntent().getStringExtra("subject_id");
        binding.progressbar.setVisibility(View.VISIBLE);
        firestore.collection("subject")
                .document(subject_id)
                .collection("chapter")
                .addSnapshotListener((value, error) -> {
                    if (error == null) {
                        binding.progressbar.setVisibility(View.GONE);
                        if (value == null) {

                            Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();

                        } else {

                            if (value.getDocuments().isEmpty()){
                               alertDialog();
                            }else {
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

                                    binding.recyclerview.setAdapter(adapter);
                                    binding.progressbar.setVisibility(View.GONE);
                                }
                            }


                        }

                    } else {
                        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                });


    }

    private void onDocumentAdded(DocumentChange change) {
        try {
            ChapterModel model = change.getDocument().toObject(ChapterModel.class);
            list.add(model);
            adapter.notifyItemInserted(list.size() - 1);
            adapter.getItemCount();




        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void onDocumentModified(DocumentChange change) {
        try {
            ChapterModel model = change.getDocument().toObject(ChapterModel.class);
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

    private void alertDialog() {

        new AlertDialog.Builder(this)
                .setMessage(R.string.no_data_added_till_now_check_soon)

                .setNegativeButton(R.string.yes, (dialog, which) -> {
                    finish();

                }).create().show();


    }
}