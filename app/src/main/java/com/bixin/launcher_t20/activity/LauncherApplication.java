package com.bixin.launcher_t20.activity;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.provider.Settings;
import android.util.Log;

import com.bixin.launcher_t20.R;
import com.bixin.launcher_t20.model.bean.AppInfo;
import com.bixin.launcher_t20.model.tools.CustomValue;
import com.bixin.launcher_t20.model.tools.TXZVoiceAddCommand;
import com.txznet.sdk.TXZConfigManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LauncherApplication extends Application implements TXZConfigManager.InitListener,
        TXZConfigManager.ActiveListener {
    private static LauncherApplication myApplication = null;
    public ArrayList<AppInfo> mAppList = new ArrayList<>();
    private TXZConfigManager.InitParam mInitParam;


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        myApplication = this;
        init();
    }

    public static LauncherApplication getInstance() {
        return myApplication;
    }

    public void init() {
        new Thread(() -> {
//            setDefaultIME();
//            initTXZ();
        }).start();
    }

    private void setDefaultIME() {
        ContentResolver resolver = getContentResolver();
        Settings.Secure.putString(resolver, Settings.Secure.DEFAULT_INPUT_METHOD,
                "com.sohu.inputmethod.sogou/.SogouIME");
    }

    /**
     * 获得所有安装的APP
     */
    public void initAppList() {
        if (mAppList.size() > 0) {
            mAppList.clear();
        }
        PackageManager pm = getPackageManager();
        Intent main = new Intent(Intent.ACTION_MAIN, null);
        main.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> apps = pm.queryIntentActivities(main, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(pm));
        for (ResolveInfo resolveInfo : apps) {
            AppInfo info = new AppInfo();
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (ignoreApp(activityInfo.packageName)) {
                info.setAppName(resolveInfo.loadLabel(pm).toString());
                info.setPkgName(activityInfo.packageName);
                info.setFlags(activityInfo.flags);
                info.setAppIcon(activityInfo.loadIcon(pm));
                info.setAppIntent(pm.getLaunchIntentForPackage(activityInfo.packageName));
                mAppList.add(info);
            }
            Log.d("MyApplication",
                    "AppList apps info: " + activityInfo.packageName);
        }
    }

    public ArrayList<AppInfo> getShowAppList() {
        if (mAppList.size() <= 0) {
            initAppList();
        }
        return mAppList;
    }

    /**
     * 过滤显示
     *
     * @param pkgName 包名
     * @return true or false
     */
    private boolean ignoreApp(String pkgName) {
        if (pkgName.equals(CustomValue.PACKAGE_NAME_AUTONAVI)
                || pkgName.equals(CustomValue.PACKAGE_NAME_KWMUSIC)
                || pkgName.equals(CustomValue.PACKAGE_NAME_SETTINGS)
                || pkgName.equals(CustomValue.PACKAGE_NAME_E_DOG)
                || pkgName.equals(CustomValue.PACKAGE_NAME_DSJ)
                || pkgName.equals(CustomValue.PACKAGE_NAME_FILE_MANAGER)
                || pkgName.equals(CustomValue.PACKAGE_NAME_FM)
                || pkgName.equals(CustomValue.PACKAGE_NAME_GPS)
                || pkgName.equals(CustomValue.PACKAGE_NAME_MAP_TOOL)) {
            return true;
        }
        return false;
    }

    private void initTXZ() {
        //  获取接入分配的appId和appToken
        String appId = this.getResources().getString(
                R.string.txz_sdk_init_app_id);
        String appToken = this.getResources().getString(
                R.string.txz_sdk_init_app_token);
        //  设置初始化参数
        mInitParam = new TXZConfigManager.InitParam(appId, appToken);
        //  可以设置自己的客户ID，同行者后台协助计数/鉴权
        // mInitParam.setAppCustomId("ABCDEFG");
        //  可以设置自己的硬件唯一标识码
        // mInitParam.setUUID("0123456789");

        //  设置识别和tts引擎类型
        mInitParam.setAsrType(TXZConfigManager.AsrEngineType.ASR_YUNZHISHENG).setTtsType(
                TXZConfigManager.TtsEngineType.TTS_YUNZHISHENG);
        //  设置唤醒词
        String[] wakeupKeywords = this.getResources().getStringArray(
                R.array.txz_sdk_init_wakeup_keywords);
        mInitParam.setWakeupKeywordsNew(wakeupKeywords);
        // 19.7.24  跑模拟器时显示语音按钮
        // 掩藏语音按钮
//        mInitParam.setFloatToolType(FLOAT_NONE);

        //  可以按需要设置自己的对话模式
        // mInitParam.setAsrMode(AsrMode.ASR_MODE_CHAT);
        //  设置识别模式，默认自动模式即可
        // mInitParam.setAsrServiceMode(AsrServiceMode.ASR_SVR_MODE_AUTO);
        //  设置是否允许启用服务号
        // mInitParam.setEnableServiceContact(true);
        //  设置开启回音消除模式
        mInitParam.setFilterNoiseType(1);
        //  其他设置

        //  初始化在这里
        TXZConfigManager.getInstance().initialize(this, mInitParam, this, this);
//        TXZConfigManager.getInstance().initialize(this, this);
    }

    private void sendLocalBroadcast() {
        Intent intent = new Intent(CustomValue.ACTION_TXZ_INIT);
        sendBroadcast(intent);
    }

    @Override
    public void onSuccess() {
        Log.d("MyApplication", "txz init : onSuccess");
        sendLocalBroadcast();
    }

    @Override
    public void onError(int i, String s) {
        Log.d("MyApplication", "onError: ");
    }

    @Override
    public void onFirstActived() {
        new TXZVoiceAddCommand();
        Log.d("MyApplication", "onFirstActived: ");
    }
}
