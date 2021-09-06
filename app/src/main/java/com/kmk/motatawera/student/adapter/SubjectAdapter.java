package com.kmk.motatawera.student.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kmk.motatawera.student.R;
import com.kmk.motatawera.student.model.SubjectModel;
import com.kmk.motatawera.student.ui.ChapterActivity;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {

    private Context context;
    private List<SubjectModel> list;

    public SubjectAdapter(Context context, List<SubjectModel> list) {
        this.context = context;
        this.list = list;
    }

    public void setList(List<SubjectModel> list) {
        this.list = list;
     notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.format_subject, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.subjectName.setText(list.get(position).getName());

        holder.subjectName.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChapterActivity.class);
            intent.putExtra("subject_id", list.get(position).getId());
            intent.putExtra("subject_name", list.get(position).getName());
            context.startActivity(intent);
            //showAlertSubject();
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView subjectName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectName = itemView.findViewById(R.id.subject_name_item);
        }
    }




}
