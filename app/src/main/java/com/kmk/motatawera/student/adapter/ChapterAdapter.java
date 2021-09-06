package com.kmk.motatawera.student.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.kmk.motatawera.student.R;

import com.kmk.motatawera.student.databinding.FormatChapterBinding;
import com.kmk.motatawera.student.model.ChapterModel;
import com.kmk.motatawera.student.ui.PdfActivity;
import com.kmk.motatawera.student.ui.VideoActivity;

import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ViewHolder> {

    private List<ChapterModel> list;

    private Context context;

    public ChapterAdapter(List<ChapterModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FormatChapterBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.format_chapter, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChapterModel model = list.get(position);

        holder.binding.chapterItemName.setText(model.getName());

        if (model.getPdf_url().isEmpty()) {
            holder.binding.chapterItemPDF.setText(R.string.no_pdf_avilable);

        } else {
            holder.binding.chapterItemPDF.setText(R.string.pdf_available);

        }
        if (model.getVideo_url().isEmpty()) {
            holder.binding.chapterItemVideo.setText(R.string.no_video_vailable);

        } else {
            holder.binding.chapterItemVideo.setText(R.string.video_available);

        }
        holder.binding.layoutBtn.setOnClickListener(v -> {

            if (!model.getPdf_url().isEmpty() && !model.getVideo_url().isEmpty()) {
                showAlertPDFVideo(position);
            } else if (model.getPdf_url().isEmpty() && model.getVideo_url().isEmpty()) {

                Toast.makeText(context, R.string.no_data_found_check_soon_, Toast.LENGTH_LONG).show();
            } else if (model.getPdf_url().isEmpty()) {
                showAlertVideo(position);
            } else if (model.getVideo_url().isEmpty()) {
                showAlertPDF(position);
            }
        });


    }

    private void checklistempty(List<ChapterModel> list) {
        if (list.isEmpty()||list.size()==0){
            new androidx.appcompat.app.AlertDialog.Builder(context)
                    .setMessage(R.string.no_data_added_till_now_check_soon)

                    .setNegativeButton(R.string.yes, (dialog, which) -> {
                        ((Activity)context).finish();

                    }).create().show();



        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<ChapterModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        FormatChapterBinding binding;

        public ViewHolder(@NonNull FormatChapterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

    private void showAlertPDFVideo(int position ) {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setCancelable(false);

        View view = LayoutInflater.from(context).inflate(R.layout.layout_subject, null);

        dialog.setView(view);

        view.findViewById(R.id.img_close).setOnClickListener(v -> dialog.dismiss());

        RadioButton radioPDF = view.findViewById(R.id.radio_pdf);
        RadioButton radioVideo = view.findViewById(R.id.radio_video);


        view.findViewById(R.id.show).setOnClickListener(v -> {
            if (radioPDF.isChecked()) {
                Intent intent = new Intent(context, PdfActivity.class);
                intent.putExtra("PDF_url", list.get(position).getPdf_url());
                context.startActivity(intent);
                dialog.dismiss();
            } else if (radioVideo.isChecked()) {

                Intent intent = new Intent(context, VideoActivity.class);
                intent.putExtra("video_url", list.get(position).getVideo_url());
                context.startActivity(intent);
                dialog.dismiss();
            }

        });

        dialog.show();
    }

    private void showAlertPDF( int position) {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setCancelable(false);

        View view = LayoutInflater.from(context).inflate(R.layout.layout_pdf_dialog, null);

        dialog.setView(view);

        view.findViewById(R.id.img_close).setOnClickListener(v -> dialog.dismiss());

        RadioButton radioPDF = view.findViewById(R.id.radio_pdf);


        view.findViewById(R.id.show).setOnClickListener(v -> {
            if (radioPDF.isChecked()) {
                Intent intent = new Intent(context, PdfActivity.class);
                intent.putExtra("PDF_url", list.get(position).getPdf_url());
                context.startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showAlertVideo( int position) {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setCancelable(false);

        View view = LayoutInflater.from(context).inflate(R.layout.layout_video_dialog, null);

        dialog.setView(view);

        view.findViewById(R.id.img_close).setOnClickListener(v -> dialog.dismiss());


        RadioButton radioVideo = view.findViewById(R.id.radio_video);


        view.findViewById(R.id.show).setOnClickListener(v -> {
            if (radioVideo.isChecked()) {

                Intent intent = new Intent(context, VideoActivity.class);
                intent.putExtra("video_url", list.get(position).getVideo_url());
                context.startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}

