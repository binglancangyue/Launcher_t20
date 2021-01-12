package com.bixin.launcher_t20.model.tools;

import android.annotation.SuppressLint;
import android.widget.Toast;

import com.bixin.launcher_t20.activity.LauncherApp;

public class ToastTool {
    private static Toast toast;

    @SuppressLint("ShowToast")
    public static void showToast(String text) {
        if (toast == null) {
            toast = Toast.makeText(LauncherApp.getInstance(), text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    @SuppressLint("ShowToast")
    public static void showToast(int text) {

        if (toast == null) {
            toast = Toast.makeText(LauncherApp.getInstance(), text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }
}
