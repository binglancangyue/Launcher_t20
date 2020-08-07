package com.bixin.launcher_t20.activity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bixin.launcher_t20.R;
import com.bixin.launcher_t20.model.bean.WeatherBean;
import com.bixin.launcher_t20.model.listener.OnLocationListener;
import com.bixin.launcher_t20.model.receiver.WeatherReceiver;
import com.bixin.launcher_t20.model.tools.CustomValue;
import com.bixin.launcher_t20.model.tools.InterfaceCallBackManagement;
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


public class LauncherHomeActivity extends RxActivity implements View.OnClickListener, OnLocationListener {
    private static final String TAG = "HomeActivity";
    private Context mContext;
    private LauncherApplication myApplication;
    private CompositeDisposable compositeDisposable;
    public InnerHandler mHandler;
    private StartActivityTool activityTools;
    private WeatherReceiver mWeatherReceiver;
    private ImageView ivWeatherIcon;
    private TextView tvWeather;
    private TextView tvCurrentCity;
    private WeatherBean weatherBean;
    private static final int MIN_CLICK_DELAY_TIME = 5000;
    private static long lastClickTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_layout_weather);
        init();
        initView();
        getFileData();
        mHandler.sendEmptyMessageDelayed(2, 6000);
        mHandler.sendEmptyMessageDelayed(3, 4000);
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
        InterfaceCallBackManagement.getInstance().setOnLocationListener(this);
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
        ConstraintLayout constraintLayout = findViewById(R.id.cl_weather);
        constraintLayout.setOnClickListener(this);

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
            case R.id.cl_weather:
                getWeather();
                break;
            default: // 应用
                activityTools.jumpToActivity(this, AppListActivity.class);
                break;
        }
    }

    @Override
    public void gpsSpeedChanged() {

    }

    @Override
    public void updateWeather(String weatherInfo) {
        Log.d(TAG, "updateWeather:weatherInfo " + weatherInfo);
        weatherBean = JSONObject.parseObject(weatherInfo, WeatherBean.class);
        if (weatherBean == null) {
            return;
        }
        if (mHandler != null) {
            mHandler.sendEmptyMessage(4);
        }
    }

    private void updateWeather() {
        String cityName = weatherBean.getCityName();
        String weather = weatherBean.getWeather();
        tvCurrentCity.setText(cityName);
        String weatherInfo = weather + "  " + weatherBean.getCurrentTemperature() + "°";
        tvWeather.setText(weatherInfo);
        ivWeatherIcon.setImageResource(activityTools.getWeatherIcon(weather));
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
                Intent intent = new Intent("com.bixin.action_start_service");
                activity.sendBroadcast(intent);
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

    private void getWeather() {
        if (isFastClick()) {
            Intent intent = new Intent(CustomValue.ACTION_GET_WEATHER);
            sendBroadcast(intent);
        }
    }

    public boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
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

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        if (mWeatherReceiver != null) {
            unregisterReceiver(mWeatherReceiver);
        }
    }

}
