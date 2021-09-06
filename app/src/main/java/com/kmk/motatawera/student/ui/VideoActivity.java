package com.kmk.motatawera.student.ui;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.kmk.motatawera.student.R;

public class VideoActivity extends AppCompatActivity {
private String videoUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        videoUrl = getIntent().getStringExtra("video_url");

        // create an object of media controller
        MediaController mediaController = new MediaController(this);

        Uri uri = Uri.parse(videoUrl);
        VideoView simpleVideoView = (VideoView) findViewById(R.id.VideoView); // initiate a video view
        simpleVideoView.setVideoURI(uri);
        // set media controller object for a video view
        simpleVideoView.setMediaController(mediaController);


        simpleVideoView.start();

    }
}