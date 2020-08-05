package com.bixin.launcher_t20.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bixin.launcher_t20.R;
import com.bixin.launcher_t20.model.bean.WeatherBean;
import com.bixin.launcher_t20.model.listener.OnLocationListener;
import com.bixin.launcher_t20.model.listener.OnSettingDisplayListener;
import com.bixin.launcher_t20.model.listener.OnSettingPopupWindowListener;
import com.bixin.launcher_t20.model.receiver.APPReceiver;
import com.bixin.launcher_t20.model.receiver.AlarmReceiver;
import com.bixin.launcher_t20.model.receiver.PopupWindowBroadcastReceiver;
import com.bixin.launcher_t20.model.receiver.TXZBroadcastReceiver;
import com.bixin.launcher_t20.model.receiver.WeatherReceiver;
import com.bixin.launcher_t20.model.tools.CustomValue;
import com.bixin.launcher_t20.model.tools.InterfaceCallBackManagement;
import com.bixin.launcher_t20.model.tools.ScreenControl;
import com.bixin.launcher_t20.model.tools.SettingPopupWindow;
import com.bixin.launcher_t20.model.tools.SharedPreferencesTool;
import com.bixin.launcher_t20.model.tools.StartActivityTool;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.RxActivity;

import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.bixin.launcher_t20.model.tools.CustomValue.ACTION_OPEN_TXZ_VIEW;


public class LauncherHomeActivity extends RxActivity implements View.OnClickListener,
        OnSettingDisplayListener, OnSettingPopupWindowListener, OnLocationListener {
    private static final String TAG = "HomeActivity";
    private Context mContext;
    private LauncherApplication myApplication;
    private CompositeDisposable compositeDisposable;
    public InnerHandler mHandler;
    private StartActivityTool activityTools;
    private SettingPopupWindow mPopupWindow;
    private SharedPreferencesTool mPreferencesTools;
    private TXZBroadcastReceiver mTxzReceiver;
    private PopupWindowBroadcastReceiver mPopupWindowBroadcastReceiver;
    private ScreenControl mScreenControl;
    private APPReceiver mUpdateAppReceiver;
    private WeatherReceiver mWeatherReceiver;
    private ImageView ivWeatherIcon;
    private TextView tvWeather;
    private TextView tvCurrentCity;
    private WeatherBean weatherBean;
    private AlarmManager manager;
    private PendingIntent pi;
    private AlarmReceiver alarmReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_layout_weather);
        init();
        initView();
        getFileData();
        mHandler.sendEmptyMessageDelayed(2, 2000);
        mHandler.sendEmptyMessageDelayed(3, 4000);
//        initUpdateWeatherAlarm();
    }

    private void initUpdateWeatherAlarm() {
        long triggerAtTime = System.currentTimeMillis() + 60000;
        alarmReceiver = new AlarmReceiver();
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, AlarmReceiver.class);
        pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime, 10000, pi);
    }

/*    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            //do something.
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }*/

    private void init() {
        mContext = this;
        myApplication = (LauncherApplication) getApplication();
        compositeDisposable = new CompositeDisposable();
        mHandler = new InnerHandler(this);
        activityTools = new StartActivityTool();
//        mPopupWindow = new SettingPopupWindow(this);
//        mPopupWindow.setOnSettingPopupWindowListener(this);
//        mTxzReceiver = new TXZBroadcastReceiver(this);
        InterfaceCallBackManagement.getInstance().setOnLocationListener(this);
        mPreferencesTools = new SharedPreferencesTool();
        mUpdateAppReceiver = new APPReceiver();
        mWeatherReceiver = new WeatherReceiver();
    }

    private void initView() {
        ImageView ivNavigation = findViewById(R.id.iv_navigation);
        ImageView ivRecorder = findViewById(R.id.iv_recorder);
        ImageView ivCloudService = findViewById(R.id.iv_cloud_service);
        ImageView ivFM = findViewById(R.id.iv_fm);
        ImageView ivBluetooth = findViewById(R.id.iv_bluetooth);
        ImageView ivMusic = findViewById(R.id.iv_music);
        ImageView ivPlayback = findViewById(R.id.iv_playback);
        ImageView ivAPP = findViewById(R.id.iv_app);
        ivWeatherIcon = findViewById(R.id.iv_weather);
        tvWeather = findViewById(R.id.tv_weather);
        tvCurrentCity = findViewById(R.id.tv_city);

        ivNavigation.setOnClickListener(this);
        ivRecorder.setOnClickListener(this);
        ivCloudService.setOnClickListener(this);
        ivFM.setOnClickListener(this);
        ivBluetooth.setOnClickListener(this);
        ivMusic.setOnClickListener(this);
        ivPlayback.setOnClickListener(this);
        ivAPP.setOnClickListener(this);
    }

    private void registerWeatherReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CustomValue.ACTION_UPDATE_WEATHER);
        registerReceiver(mWeatherReceiver, filter);
    }

    private void registerAppReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
//        filter.addAction(Customer.ACTION_TXZ_CUSTOM_COMMAND);
        filter.addDataScheme("package");
        registerReceiver(mUpdateAppReceiver, filter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_navigation: // 高德导航
                activityTools.launchAppByPackageName(CustomValue.PACKAGE_NAME_AUTONAVI);
                break;
            case R.id.iv_recorder: // 记录仪
                activityTools.launchAppByPackageName(CustomValue.PACKAGE_NAME_DVR);
                break;
            case R.id.iv_cloud_service: // 云服务
                break;
            case R.id.iv_fm: // FM
//                Intent intent=new Intent(this,SettingsWindowActivity.class);
//                startActivity(intent);
                activityTools.launchAppByPackageName(CustomValue.PACKAGE_NAME_FM);
                break;
            case R.id.iv_bluetooth: // 蓝牙
//                activityTools.launchAppByPackageName("com.cywl.bt.activity");
                activityTools.jumpByAction(Settings.ACTION_BLUETOOTH_SETTINGS);
                break;
            case R.id.iv_music: // 音乐
                activityTools.launchAppByPackageName(CustomValue.PACKAGE_NAME_KWMUSIC);
                break;
            case R.id.iv_playback: // 视频回放
                activityTools.launchAppByPackageName(CustomValue.PACKAGE_NAME_ViDEO_PLAY_BACK);
                break;
            default: // 应用
                activityTools.jumpToActivity(this, AppListActivity.class);
                break;
        }
    }

    @Override
    public void showSettingPopupWindow() {
        mPopupWindow.showPopupWindow();
    }

//    @Override
//    public void openAPP(String packageName) {
//        if (packageName == null) {
//            activityTools.jumpToActivity(this, AppListActivity.class);
//        } else {
//            activityTools.launchAppByPackageName(packageName);
//
//        }
//    }
//
//    @Override
//    public void closeApp(String packageName) {
//        Log.d(TAG, "closeApp:killPackageName ");
////        activityTools.stopApps(packageName);
//        activityTools.killApp(packageName);
//    }

//    /**
//     * 同行者广播回调
//     *
//     * @param type         类型
//     * @param txzOperation 实体类
//     */
//    @Override
//    public void notifyActivity(int type, TXZOperation txzOperation) {
//        switch (type) {
//            case 1:
//                int light = txzOperation.getLight();
//                mPopupWindow.txzUpdateBrightnessOrVolume(0, light);
//                break;
//            case 2:
//                int volume = txzOperation.getVolume();
//                mPopupWindow.txzUpdateBrightnessOrVolume(1, volume);
//                break;
//            case 3:
//                boolean enableWifi = txzOperation.isEnableWifi();
//                mPopupWindow.txzUpdateWifiSwitch(enableWifi);
//                break;
//            case 4:
//                boolean screenOn = txzOperation.isScreenOpen();
//                if (screenOn) {
//                    mScreenControl.checkScreenOn();
//                } else {
//                    mScreenControl.checkScreenOff();
//                }
//                break;
//            case 5:
//                activityTools.goHome();
//                break;
//            case 6:
//                boolean enableBluetooth = txzOperation.isEnableBluetooth();
//                mPopupWindow.txzUpdateSwitch(4, enableBluetooth);
//                break;
//            case 8:
//                activityTools.openTXZView();
//                break;
//            case 9:
//                mPopupWindow.showPopupWindow();
//                break;
//            default:
//                new TXZVoiceControl(LauncherHomeActivity.this);
//                break;
//        }
//    }

    @Override
    public void sendMessageToActivity(int what) {
        Log.d(TAG, "sendMessageToActivity: ");
    }

    @Override
    public void gpsSpeedChanged() {

    }

    @Override
    public void updateWeather(String weatherInfo) {
        Log.d(TAG, "updateWeather: ");
        weatherBean = JSONObject.parseObject(weatherInfo, WeatherBean.class);
        mHandler.sendEmptyMessage(4);
    }

    private void updateWeather() {
        tvCurrentCity.setText(weatherBean.getCityName());
        String weather = weatherBean.getWeather() + "  " + weatherBean.getCurrentTemperature() + "°";
        tvWeather.setText(weather);
    }

    public static class InnerHandler extends Handler {
        private final WeakReference<LauncherHomeActivity> activityWeakReference;
        private LauncherHomeActivity activity;

        private InnerHandler(LauncherHomeActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            activity = activityWeakReference.get();
            if (msg.what == 2) {
                activity.registerWeatherReceiver();
                activity.registerAppReceiver();
                activity.activityTools.startVoiceRecognitionService();
            }
            if (msg.what == 3) {
                activity.startDVR();
            }
            if (msg.what == 4) {
                activity.updateWeather();
            }
        }
    }

    private void initAppInfo() {
        compositeDisposable.add(Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                myApplication.initAppList();
//                TXZConfigManager.getInstance().initialize(mContext, HomeActivity.this);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(aBoolean -> {

                }));
    }

    private void createPopWindow() {
        compositeDisposable.add(Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                mPopupWindow.createPopWindow();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(aBoolean -> {

                }));
    }

//    private void initTXZVoiceListener() {
//        compositeDisposable.add(Observable.create(new ObservableOnSubscribe<Boolean>() {
//            @Override
//            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
//                new TXZVoiceControl(LauncherHomeActivity.this);
//            }
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .compose(bindUntilEvent(ActivityEvent.DESTROY))
//                .subscribe(aBoolean -> {
//
//                }));
//    }


    @SuppressLint("NewApi")
    private void getFileData() {
        getWindow().getDecorView().post(() -> mHandler.post(() -> {
//            registerTXZReceiver();
            initAppInfo();
//            createPopWindow();
//            mScreenControl = new ScreenControl();
//            mScreenControl.init();
//            mScreenControl.checkAndTurnOnDeviceManager(this);

//            RequestPermissionsTool requestPermissionsTools = new RequestPermissionsTool();
//            requestPermissionsTools.requestPermissions(HomeActivity.this,
//                    new String[]{KILL_BACKGROUND_PROCESSES});

        }));
    }


    private void registerTXZReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CustomValue.ACTION_TXZ_RECV);
//        intentFilter.addAction(CustomValue.ACTION_TXZ_SEND);
        intentFilter.addAction(ACTION_OPEN_TXZ_VIEW);
        intentFilter.addAction(CustomValue.ACTION_TXZ_INIT);
        registerReceiver(mTxzReceiver, intentFilter);
    }

    private void startDVR() {
        Intent intent = new Intent();
        String packageName = "com.bx.carDVR";
        String className = "com.bx.carDVR.DVRService";
        intent.setComponent(new ComponentName(packageName, className));
        try {
            mContext.startService(intent);
        } catch (Exception e) {
            Log.e(TAG, "startDVR: e " + e.getMessage());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
            compositeDisposable.clear();
        }
        if (mPopupWindow != null) {
            if (mPopupWindowBroadcastReceiver != null) {
                mPopupWindow.unregisterSettingsPopReceiver();
            }
            mPopupWindow.clearHandle();
            mPopupWindow = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mTxzReceiver != null) {
            unregisterReceiver(mTxzReceiver);
        }
        if (mUpdateAppReceiver != null) {
            unregisterReceiver(mUpdateAppReceiver);
        }
        if (mWeatherReceiver != null) {
            unregisterReceiver(mWeatherReceiver);
        }
    }

}
