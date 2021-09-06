package com.kmk.motatawera.student.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kmk.motatawera.student.R;
import com.kmk.motatawera.student.adapter.QuizAdapter;
import com.kmk.motatawera.student.databinding.FragmentQuizBinding;
import com.kmk.motatawera.student.databinding.QuizlistLayoutBinding;
import com.kmk.motatawera.student.model.QuizModel;
import com.kmk.motatawera.student.model.SubjectModel;
import com.kmk.motatawera.student.storage.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;


public class QuizFragment extends Fragment implements QuizAdapter.QuizLisner {
    FragmentQuizBinding binding;
    RecyclerView recyclerView;
    FirebaseFirestore db;
    List<QuizModel> quizList;

    ProgressDialog progressDialog;
    QuizAdapter quizListAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_quiz, container, false);
        recyclerView = binding.rvQuizlist;


        return binding.getRoot();


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        quizList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        //  binding.progressBar.setVisibility(View.VISIBLE);
//         progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setMessage("رجاء الإنتظار..");
//        progressDialog.setCancelable(false);
//        progressDialog.show();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        quizListAdapter = new QuizAdapter(quizList, getActivity(), this);
        recyclerView.setAdapter(quizListAdapter);


        getQuizList();


    }

    private void getQuizList() {

        int getuserdep = SharedPrefManager.getInstance().getUser(getActivity()).getDepartment();
        int getuserbranch = SharedPrefManager.getInstance().getUser(getActivity()).getBranch();
        int getusergrad = SharedPrefManager.getInstance().getUser(getActivity()).getGrad();


        db.collection("subject")
                .whereEqualTo("grad", getusergrad)
                .whereEqualTo("department", getuserdep)
                .whereEqualTo("branch", getuserbranch)
                .addSnapshotListener((value1, error1) -> {
                    if (error1 == null) {
                        if (value1 == null) {
                            Toast.makeText(getActivity(), error1.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            for (DocumentChange documentChange2 : value1.getDocumentChanges()) {
                                SubjectModel model = documentChange2.getDocument().toObject(SubjectModel.class);
                                db.collection("quiz")
                                        .whereEqualTo("subject_id", model.getId())
                                        .whereEqualTo("quizFinished", false)
                                        .addSnapshotListener((value, error) -> {
                                            if (error == null) {
                                                if (value == null) {
                                                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                                } else {
                                                    for (DocumentChange documentChange : value.getDocumentChanges()) {

                                                        switch (documentChange.getType()) {
                                                            case ADDED:
                                                                QuizModel quizModel = documentChange.getDocument().toObject(QuizModel.class);
                                                                quizList.add(quizModel);
                                                                quizListAdapter.notifyDataSetChanged();
                                                                break;

                                                            case REMOVED:
                                                                // remove
                                                                quizList.remove(documentChange.getOldIndex());
                                                                quizListAdapter.notifyItemRemoved(documentChange.getOldIndex());
                                                                break;
                                                            case MODIFIED:
                                                                QuizModel changedModel = documentChange.getDocument().toObject(QuizModel.class);
                                                                if (documentChange.getOldIndex() == documentChange.getNewIndex()) {
                                                                    // Item changed and still in the same position
                                                                    quizList.set(documentChange.getOldIndex(), changedModel);
                                                                    quizListAdapter.notifyItemChanged(documentChange.getOldIndex());
                                                                    quizListAdapter.notifyDataSetChanged();
                                                                } else {
                                                                    // Item changed and changed position
                                                                    quizList.remove(documentChange.getOldIndex());
                                                                    quizList.add(documentChange.getNewIndex(), changedModel);
                                                                    quizListAdapter.notifyItemMoved(documentChange.getOldIndex(), documentChange.getNewIndex());
                                                                    quizListAdapter.notifyDataSetChanged();
                                                                }
                                                                quizListAdapter.setList(quizList);
                                                        }
                                                    }
                                                }
                                            } else {
                                                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        }
                    }
                });


    }

    @Override
    public void isLoading(boolean l) {
        if (l) {
            recyclerView.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void OnScoreLisner(int position, List<QuizModel> quizModels, Context context, QuizlistLayoutBinding quizlistLayoutBinding, boolean isActiveBtn) {
//        db = FirebaseFirestore.getInstance();
//        String getuserid = SharedPrefManager.getInstance().getUser(context).getId();
//        if ((quizModels.get(position).getId() == null)) {
//            Toast.makeText(context, "No Quiz Found", Toast.LENGTH_SHORT).show();
//        } else {
//
//            if (isActiveBtn) {
//                db.collection("quiz")
//                        .document(quizModels.get(position).getId())
//                        .collection("score")
//                        .whereEqualTo("quizFinished", true)
//                        .whereEqualTo("student_id", getuserid)
//                        .get()
//                        .addOnCompleteListener(task -> {
//                            if (task.getResult().isEmpty()) {
//                                quizlistLayoutBinding.btnStartquiz.setEnabled(true);
//                                quizlistLayoutBinding.btnStartquiz.setBackgroundResource(R.drawable.bg_botton_red);
//                                progressDialog.dismiss();
//                            } else {
//                                db.collection("quiz").document(quizModels.get(position).getId())
//                                        .collection("score")
//                                        .addSnapshotListener((value, error) -> {
//                                            if (error == null) {
//                                                if (value == null) {
//
//                                                    Toast.makeText(context, "No Score found", Toast.LENGTH_SHORT).show();
//                                                } else {
//                                                    for (DocumentChange documentChange : value.getDocumentChanges()) {
//
//                                                        quizlistLayoutBinding.btnStartquiz.setEnabled(false);
//                                                        quizlistLayoutBinding.btnStartquiz.setClickable(false);
//
//                                                        String s = documentChange.getDocument().get("correct").toString();
//                                                        String y = documentChange.getDocument().get("questions_number").toString();
//
//
//                                                        quizlistLayoutBinding.txtResult.setText(getActivity().getString(R.string.ruselt) + "\n" + s + " / " + y);
//                                                        progressDialog.dismiss();
//                                                    }
//
//
//                                                }
//                                            } else {
//                                                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
//                                            }
//
//                                        });
//
//
//                                //   Toast.makeText(context, "else 2", Toast.LENGTH_SHORT).show();
//                                quizlistLayoutBinding.btnStartquiz.setEnabled(false);
//                                quizlistLayoutBinding.btnStartquiz.setBackgroundResource(R.drawable.bg_botton_gray);
//                            }
//
//
//                        });
//            }
//
//        }
    }
}