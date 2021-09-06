package com.kmk.motatawera.student.util;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.kmk.motatawera.student.R;

public class ShowAlert {

    public static void SHOW_ALERT(Context context, String msg) {
        if (context != null)
            new AlertDialog.Builder(context)
                    .setMessage(msg)
                    .setPositiveButton("ok", null)
                    .create()
                    .show();
    }
}
