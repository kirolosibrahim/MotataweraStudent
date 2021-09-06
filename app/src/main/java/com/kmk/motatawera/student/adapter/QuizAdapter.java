package com.kmk.motatawera.student.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kmk.motatawera.student.R;
import com.kmk.motatawera.student.databinding.QuizlistLayoutBinding;
import com.kmk.motatawera.student.model.QuizModel;
import com.kmk.motatawera.student.storage.SharedPrefManager;
import com.kmk.motatawera.student.ui.StartQuizActivity;

import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ViewHolder> {

    private List<QuizModel> quizModels;
    private Context context;
    private String branch;
    private QuizLisner quizLisner;
    private boolean isActiveBtn;

    public QuizAdapter(List<QuizModel> quizModels, Context context, QuizLisner quizLisner) {
        this.quizModels = quizModels;
        this.context = context;
        this.quizLisner = quizLisner;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        QuizlistLayoutBinding quizlistLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.quizlist_layout, parent, false);
        return new ViewHolder(quizlistLayoutBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuizModel model = quizModels.get(position);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String id = SharedPrefManager.getInstance().getUser(context).getId();

        quizLisner.isLoading(true);

        if (model.getQuizActive()) {
            ActiveBtn(false, holder.quizlistLayoutBinding);
            quizLisner.isLoading(true);
            db.collection("quiz")
                    .document(model.getId())
                    .collection("score")
                    .whereEqualTo("quizFinished", true)
                    .whereEqualTo("student_id", id)
                    .whereEqualTo("quiz_id", model.getId())
                    .addSnapshotListener((value, error) -> {
                        if (value == null) {
                            ActiveBtn(true, holder.quizlistLayoutBinding);
                            setData(holder.quizlistLayoutBinding, model);
                            Toast.makeText(context, R.string.no_score_found, Toast.LENGTH_SHORT).show();
                        }
                        if (value != null) {
                            quizLisner.isLoading(true);
                            for (DocumentChange documentChange : value.getDocumentChanges()) {
                                String s = documentChange.getDocument().get("correct").toString();
                                String y = documentChange.getDocument().get("questions_number").toString();
                                holder.quizlistLayoutBinding.txtResult.setText(context.getString(R.string.ruselt) + "\n" + s + " / " + y);
                                ActiveBtn(false, holder.quizlistLayoutBinding);
                                setData(holder.quizlistLayoutBinding, model);
                            }

                            quizLisner.isLoading(false);
                        }

                    });
            ActiveBtn(true, holder.quizlistLayoutBinding);
        }

        if (!model.getQuizActive()) {
            ActiveBtn(false, holder.quizlistLayoutBinding);
            setData(holder.quizlistLayoutBinding, model);
        }

        setData(holder.quizlistLayoutBinding, model);
        holder.quizlistLayoutBinding.btnStartquiz.setOnClickListener(v -> {
            Intent intent = new Intent(context, StartQuizActivity.class);
            intent.putExtra("id", quizModels.get(position).getId());
            intent.putExtra("titel", quizModels.get(position).getTitle());
            context.startActivity(intent);
        });
    }


    private void ActiveBtn(boolean isActive, QuizlistLayoutBinding quizlistLayoutBinding) {
        if (isActive) {
            quizlistLayoutBinding.btnStartquiz.setClickable(true);
            quizlistLayoutBinding.btnStartquiz.setEnabled(true);
            quizlistLayoutBinding.btnStartquiz.setBackgroundResource(R.drawable.bg_botton_red);
        } else {
            quizlistLayoutBinding.btnStartquiz.setEnabled(false);
            quizlistLayoutBinding.btnStartquiz.setClickable(false);
            quizlistLayoutBinding.btnStartquiz.setBackgroundResource(R.drawable.bg_botton_gray);
        }

    }

    private void setData(QuizlistLayoutBinding quizlistLayoutBinding, QuizModel model) {

        if (model.getSubject_branch() == 1)
            branch = "Haram";
        else
            branch = "Qtamia";



        quizlistLayoutBinding.txtQuiztitle.setText(model.getTitle());
        quizlistLayoutBinding.txtSubject.setText(model.getSubject_name());
    }

    @Override
    public int getItemCount() {
        return quizModels.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        QuizlistLayoutBinding quizlistLayoutBinding;

        public ViewHolder(@NonNull QuizlistLayoutBinding quizlistLayoutBinding) {
            super(quizlistLayoutBinding.getRoot());
            this.quizlistLayoutBinding = quizlistLayoutBinding;
        }


    }

    public void setList(List<QuizModel> quizModels) {
        this.quizModels = quizModels;
        notifyDataSetChanged();
    }

    public interface QuizLisner {
        void isLoading(boolean l);

        void OnScoreLisner(int position, List<QuizModel> quizModels, Context context, QuizlistLayoutBinding quizlistLayoutBinding, boolean isActiveBtn);

    }


}
