package com.bixin.launcher_t20.model.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

import com.bixin.launcher_t20.R;
import com.bixin.launcher_t20.activity.LauncherApplication;
import com.txznet.sdk.TXZAsrManager;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

/**
 * @author Altair
 * @date :2019.10.21 下午 02:18
 * @description:
 */
public class StartActivityTool {
    private Context mContext;

    public StartActivityTool() {
        this.mContext = LauncherApplication.getInstance();
    }

    /**
     * 根据action跳转
     *
     * @param action action
     */
    public void jumpByAction(String action) {
        Intent intent = new Intent(action);
        mContext.startActivity(intent);
    }

    public void jumpToActivity(Activity activity, Class aClass) {
        Intent intent = new Intent(activity, aClass);
        mContext.startActivity(intent);
    }

    /**
     * 根据包名启动应用
     *
     * @param packageName clicked app
     */
    public void launchAppByPackageName(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            Log.i(TAG, "package name is null!");
            return;
        }

        Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent == null) {
            ToastTool.showToast(R.string.app_not_install);
        } else {
            mContext.startActivity(launchIntent);
        }
    }


    /**
     * 根据包名启动应用
     *
     * @param packageName clicked app
     */
    public void launchAppByPackageName(String packageName, boolean isTrue) {
        if (TextUtils.isEmpty(packageName)) {
            Log.i(TAG, "package name is null!");
            return;
        }

        Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent == null) {
            ToastTool.showToast(R.string.app_not_install);
        } else {
            launchIntent.putExtra("front", isTrue);
            mContext.startActivity(launchIntent);
        }
    }

    /**
     * 返回桌面
     */
    public void goHome() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);// "android.intent.action.MAIN"
        intent.addCategory(Intent.CATEGORY_HOME); //"android.intent.category.HOME"
        mContext.startActivity(intent);
    }

    public void openTXZView() {
        Intent it = new Intent("com.txznet.txz.config.change");
        it.putExtra("type", "screen");
        it.putExtra("x", 0);
        it.putExtra("y", 0);
//        it.putExtra("width", 1024);
//        it.putExtra("height", 517);
        mContext.sendBroadcast(it);
//        TXZAsrManager.getInstance().triggerRecordButton();
    }

    public boolean killPackageName(String packageName) {
        OutputStream out = null;
        try {
            ActivityManager am =
                    (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
            am.killBackgroundProcesses(packageName);

            Process process = Runtime.getRuntime().exec("su");
            String cmd = "am force-stop " + packageName + " \n";
            out = process.getOutputStream();
            out.write(cmd.getBytes());
            out.flush();
        } catch (IOException e) {
            Log.e(TAG, " e: " + e.getMessage());
            return false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * 根据包名强制关闭一个应用，不管前台应用还是后台进程，需要share systemuid
     * 需要权限 FORCE_STOP_PACKAGES
     *
     * @param packageName app package name
     */
    public void stopApps(String packageName) {
        try {
            ActivityManager am =
                    (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
            Method forceStopPackage = am.getClass().getDeclaredMethod("forceStopPackage",
                    String.class);
            forceStopPackage.setAccessible(true);
            forceStopPackage.invoke(am, packageName);
            System.out.println("TimerV force stop package " + packageName + " successful");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("TimerV force stop package " + packageName + " error!");
        }
    }

    public void killApp(String packageName) {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        assert am != null;
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : processes) {
            String processName = info.processName;
            Log.d(TAG, "killApp: " + processName + " size: " + processes.size());
            if (processName.equals(packageName)) {
                am.killBackgroundProcesses(processName);
                android.os.Process.killProcess(info.pid);
            }
        }
    }

    @SuppressLint("NewApi")
    public void startVoiceRecognitionService() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String packageName = "com.bixin.speechrecognitiontool";
        String className = "com.bixin.speechrecognitiontool.SpeechRecognitionService";
        intent.setClassName(packageName, className);
        mContext.startForegroundService(intent);
    }

}
