package com.kmk.motatawera.student.ui;

import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.kmk.motatawera.student.R;
import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.controls.DocumentActivity;

public class PdfActivity extends AppCompatActivity {

    private String PDF_Uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        PDF_Uri = getIntent().getStringExtra("PDF_url");
//
        ViewerConfig config = new ViewerConfig.Builder().openUrlCachePath(this.getCacheDir().getAbsolutePath()).build();
      final Uri fileLink = Uri.parse(PDF_Uri);
        DocumentActivity.openDocument(this, fileLink, config);

    }
}