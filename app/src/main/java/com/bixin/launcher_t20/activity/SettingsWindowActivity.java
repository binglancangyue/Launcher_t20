package com.bixin.launcher_t20.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bixin.launcher_t20.R;
import com.bixin.launcher_t20.model.bean.TXZOperation;
import com.bixin.launcher_t20.model.listener.OnSettingPopupWindowListener;
import com.bixin.launcher_t20.model.listener.OnSettingsStatusListener;
import com.bixin.launcher_t20.model.listener.OnTXZBroadcastListener;
import com.bixin.launcher_t20.model.receiver.PopupWindowBroadcastReceiver;
import com.bixin.launcher_t20.model.tools.RequestPermissionsTool;
import com.bixin.launcher_t20.model.tools.SettingsFunctionTool;
import com.bixin.launcher_t20.model.tools.SharedPreferencesTool;
import com.bixin.launcher_t20.model.tools.WifiTool;

import java.lang.ref.WeakReference;

import static android.content.ContentValues.TAG;
import static com.bixin.launcher_t20.model.tools.CustomValue.HANDLE_POP_UPDATE_BRIGHTNESS;
import static com.bixin.launcher_t20.model.tools.CustomValue.HANDLE_POP_UPDATE_BTN;
import static com.bixin.launcher_t20.model.tools.CustomValue.HANDLE_POP_UPDATE_BTN_BY_LISTENER;
import static com.bixin.launcher_t20.model.tools.CustomValue.HANDLE_POP_UPDATE_BTN_TXZ;
import static com.bixin.launcher_t20.model.tools.CustomValue.HANDLE_POP_UPDATE_DATA;
import static com.bixin.launcher_t20.model.tools.CustomValue.HANDLE_POP_UPDATE_SEEK_BAR;
import static com.bixin.launcher_t20.model.tools.CustomValue.HANDLE_POP_WIFI_BTN;

/**
 * @author Altair
 * @date :2020.01.07 下午 02:16
 * @description:
 */
public class SettingsWindowActivity extends Activity implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener, OnTXZBroadcastListener, OnSettingsStatusListener {
    private OnSettingPopupWindowListener mListener;
    private Context mContext;
    private LinearLayout llBtnWireless;
    private LinearLayout llBtnScreenControl;
    private LinearLayout llBtnOther;
    private LinearLayout llWireless;
    private LinearLayout llScreenControl;
    private LinearLayout llOther;
    private RadioButton rbCloseScreen;
    private RadioButton rbBright;
    private RadioButton rbScreenSaver;
    private RadioButton rbTime1;
    private RadioButton rbTime5;
    private RadioButton rbTime30;
    private RadioButton rbDay;
    private RadioButton rbNight;

    private LinearLayout llBtnWifi;
    private TextView tvWifi;
    private TextView tvWifiStatus;
    private LinearLayout llBtnHotspot;
    private ImageView ivHotspot;
    private TextView tvHotspotStatus;
    private LinearLayout llBtnGps;
    private ImageView ivGps;
    private TextView tvGpsStatus;
    private LinearLayout llBtnMobileData;
    private ImageView ivMobileData;
    private TextView tvMobileDataStatus;
    private TextView tv4GStatus;
    private LinearLayout llBtnBlueTooth;
    private ImageView ivBlueTooth;
    private TextView tvBlueTooth;

    private SeekBar seekBarBrightness;
    private TextView tvBrightnessValue;
    private ImageView ivFmSwitch;
    private SeekBar seekBarVolume;
    private TextView tvVolumeValue;
    private InnerHandler mHandler;
    private SettingsFunctionTool mSettingsUtils;
    private WifiTool wifiUtils;
    private RequestPermissionsTool requestPermissionsTool;
    private SharedPreferencesTool mSharedPreferencesTool;
    private PopupWindowBroadcastReceiver mPopupWindowBroadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        setContentView(R.layout.popup_window_setting);
        mHandler = new InnerHandler(this);
//        mSettingsUtils = new SettingsFunctionTool(mContext);
        mSettingsUtils = new SettingsFunctionTool();
//        wifiUtils = new WifiTool(mContext);
        wifiUtils = new WifiTool();
//        requestPermissionsTool = new RequestPermissionsTool();
//        mSharedPreferencesTool = new SharedPreferencesTool(mContext);
        mSharedPreferencesTool = new SharedPreferencesTool();
        mPopupWindowBroadcastReceiver = new PopupWindowBroadcastReceiver(this);
        setWindowSize();
        createPopWindow();
    }

    private void setWindowSize() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        //获取手机屏幕的高度
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int widthPixels = (int) (metric.widthPixels * 0.75f);
        int heightPixels = (int) (metric.heightPixels * 0.6f);
        lp.width = widthPixels;
        lp.height = heightPixels;
        window.setAttributes(lp);
    }


    @SuppressLint("InflateParams")
    public void createPopWindow() {
        initPopupWindow();
        setPopupWindowListener();
        setData();
    }

    private void setData() {
        int brightness = mSettingsUtils.getScreenBrightnessPercentageValue();
        int volume = mSettingsUtils.getCurrentVolume();
        seekBarBrightness.setProgress(brightness);
        tvBrightnessValue.setText(String.valueOf(brightness));
        seekBarVolume.setProgress(volume);
        tvVolumeValue.setText(String.valueOf(volume));
        if (wifiUtils.isWifiEnable()) {
            WifiSwitchOpen();
        } else {
            WifiSwitchClose();
        }

        if (mSettingsUtils.isBlueToothEnable()) {
            llBtnBlueTooth.setSelected(false);
        } else {
            llBtnBlueTooth.setSelected(true);
        }
        Log.d(TAG, "setData:isBlueToothEnable " + mSettingsUtils.isBlueToothEnable());
        updateSettingButton(4);
        initScreenOutTime();
        Log.d(TAG, "setData: " + mSettingsUtils.isGpsOpen());

    }

    public void setOnSettingPopupWindowListener(OnSettingPopupWindowListener listener) {
        this.mListener = listener;
    }

    public void showPopupWindow() {
        mHandler.sendEmptyMessage(HANDLE_POP_UPDATE_DATA);
    }

    private void initPopupWindow() {
        llBtnWireless = findViewById(R.id.ll_left_wifi);
        llBtnScreenControl = findViewById(R.id.ll_left_screen_control);
        llBtnOther = findViewById(R.id.ll_left_other);

        llWireless = findViewById(R.id.ll_wireless_data);
        llScreenControl = findViewById(R.id.ll_brightness);
        llOther = findViewById(R.id.ll_other);

        // Wireless data
        llBtnWifi = findViewById(R.id.ll_btn_wifi);
        tvWifi = findViewById(R.id.tv_wifi);
        tvWifiStatus = findViewById(R.id.tv_wifi_status);
        llBtnHotspot = findViewById(R.id.ll_btn_hotspot);
        ivHotspot = findViewById(R.id.iv_hotspot);
        tvHotspotStatus = findViewById(R.id.tv_hotspot_status);
        llBtnGps = findViewById(R.id.ll_btn_gps);
        ivGps = findViewById(R.id.iv_gps);
        tvGpsStatus = findViewById(R.id.tv_gps_status);
        llBtnMobileData = findViewById(R.id.ll_btn_mobile_data);
        ivMobileData = findViewById(R.id.iv_mobile_data);
        tvMobileDataStatus = findViewById(R.id.tv_mobile_data_status);
        tv4GStatus = findViewById(R.id.tv_4g_status);
        llBtnBlueTooth = findViewById(R.id.ll_btn_bluetooth);
        ivBlueTooth = findViewById(R.id.iv_bluetooth);
        tvBlueTooth = findViewById(R.id.tv_bluetooth_status);

        llBtnWifi.setOnClickListener(this);
        llBtnHotspot.setOnClickListener(this);
        llBtnGps.setOnClickListener(this);
        llBtnMobileData.setOnClickListener(this);
        llBtnBlueTooth.setOnClickListener(this);


        //Screen control
        rbCloseScreen = findViewById(R.id.rb_close_screen);
        rbBright = findViewById(R.id.rb_bright);
        rbScreenSaver = findViewById(R.id.rb_screen_saver);

        rbTime1 = findViewById(R.id.rb_time_1);
        rbTime5 = findViewById(R.id.rb_time_5);
        rbTime30 = findViewById(R.id.rb_time_30);

        rbDay = findViewById(R.id.rb_screen_control_day);
        rbNight = findViewById(R.id.rb_screen_control_night);


        rbCloseScreen.setOnClickListener(this);
        rbBright.setOnClickListener(this);
        rbScreenSaver.setOnClickListener(this);
        rbTime1.setOnClickListener(this);
        rbTime5.setOnClickListener(this);
        rbTime30.setOnClickListener(this);
        rbDay.setOnClickListener(this);
        rbNight.setOnClickListener(this);

        seekBarBrightness = findViewById(R.id.sb_brightness);
        tvBrightnessValue = findViewById(R.id.tv_brightness_value);

        // Other
        ivFmSwitch = findViewById(R.id.iv_switch_fm);
        seekBarVolume = findViewById(R.id.sb_volume);
        tvVolumeValue = findViewById(R.id.tv_volume_value);

/*
        //获取手机屏幕的高度
        DisplayMetrics metric = new DisplayMetrics();
        mHomeActivity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int widthPixels = (int) (metric.widthPixels * 0.75f);
        int heightPixels = (int) (metric.heightPixels * 0.6f);

        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setWidth(widthPixels);
        popupWindow.setHeight(heightPixels);

        popupWindow.setBackgroundDrawable(new ColorDrawable(-000000));
        popupWindow.setAnimationStyle(R.style.SettingsTranslateAnim);*/

        clearLeftButton();
        llBtnWireless.setSelected(true);
        llWireless.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_left_wifi:
                setLeftBtnSelected(llBtnWireless, llWireless);
                break;
            case R.id.ll_left_screen_control:
                setLeftBtnSelected(llBtnScreenControl, llScreenControl);
                break;
            case R.id.ll_left_other:
                setLeftBtnSelected(llBtnOther, llOther);
                break;
            case R.id.rb_close_screen:
                clearFunctionRBCheck();
                rbCloseScreen.setChecked(true);
                break;
            case R.id.rb_screen_saver:
                clearFunctionRBCheck();
                rbScreenSaver.setChecked(true);
                break;
            case R.id.rb_bright:
                clearFunctionRBCheck();
                setScreenControlRbCheck(rbBright);
                mSettingsUtils.setTime(Integer.MAX_VALUE);
                break;
            case R.id.rb_time_1:
                mSettingsUtils.setTime(60 * 1000);
                setScreenControlRbCheck(rbTime1);
                break;
            case R.id.rb_time_5:
                mSettingsUtils.setTime(300 * 1000);
                setScreenControlRbCheck(rbTime5);
                break;
            case R.id.rb_time_30:
                mSettingsUtils.setTime(1800 * 1000);
                setScreenControlRbCheck(rbTime30);
                break;
            case R.id.rb_screen_control_day:
                setDayOrNightBtnCheck(rbDay);
//                mSharedPreferencesTool.saveBoolean("btn_day", true);
//                mSharedPreferencesTool.saveBoolean("btn_night", false);
                sendMessageToHomeActivity(HANDLE_POP_UPDATE_BRIGHTNESS, 100);
                break;
            case R.id.rb_screen_control_night:
                setDayOrNightBtnCheck(rbNight);
//                mSharedPreferencesTool.saveBoolean("btn_night", true);
//                mSharedPreferencesTool.saveBoolean("btn_day", false);
                sendMessageToHomeActivity(HANDLE_POP_UPDATE_BRIGHTNESS, 40);
                break;
            case R.id.ll_btn_wifi:
//                sendMessageToHomeActivity(HANDLE_POP_WIFI_BTN);
                wifiOpenOrClose(!llBtnWifi.isSelected());
                break;
            case R.id.ll_btn_hotspot:
                sendMessageToHomeActivity(HANDLE_POP_UPDATE_BTN, 1);
                break;
            case R.id.ll_btn_gps:
                sendMessageToHomeActivity(HANDLE_POP_UPDATE_BTN, 2);
                break;
            case R.id.ll_btn_mobile_data:
                sendMessageToHomeActivity(HANDLE_POP_UPDATE_BTN, 3);
                break;
            case R.id.ll_btn_bluetooth:
                mSettingsUtils.openOrCloseBT(!llBtnBlueTooth.isSelected());
//                sendMessageToHomeActivity(HANDLE_POP_UPDATE_BTN, 4);
                break;
            case R.id.iv_switch_fm:
                if (ivFmSwitch.isSelected()) {
                    ivFmSwitch.setSelected(false);
                } else {
                    ivFmSwitch.setSelected(true);
                }
        }
    }

    private void setLeftBtnSelected(LinearLayout leftBtn, LinearLayout layout) {
        clearLeftButton();
        leftBtn.setSelected(true);
        layout.setVisibility(View.VISIBLE);
    }

    private void setScreenControlRbCheck(RadioButton btn) {
        clearTimeRBCheck();
        btn.setChecked(true);
        if (rbBright.isChecked()) {
            clearTimeRBCheck();
        }
    }

    private void initScreenOutTime() {
        int timeType = mSettingsUtils.getScreenOutTime();
        Log.d(TAG, "initScreenOutTime: " + timeType);
        RadioButton radioButton;
        clearFunctionRBCheck();
        if (timeType == 1) {
            radioButton = rbTime1;
        } else if (timeType == 2) {
            radioButton = rbTime5;
        } else if (timeType == 3) {
            radioButton = rbTime30;
        } else {
            radioButton = rbBright;
            rbBright.setChecked(true);
        }
        setScreenControlRbCheck(radioButton);
    }

    private void setDayOrNightBtnCheck(RadioButton btn) {
        clearDayOrNightRBCheck();
        btn.setChecked(true);

    }

    private void sendMessage(int what) {
        if (mListener != null) {
            mListener.sendMessageToActivity(what);
        }
    }

    private void sendMessageToHomeActivity(int messageCode) {
        mHandler.sendEmptyMessage(messageCode);
    }

    public void sendMessageToHomeActivity(int num, int type) {
        Message message = Message.obtain();
        message.what = num;
        message.arg1 = type;
        mHandler.sendMessage(message);
    }

    private void sendMessageToHomeActivity(int num, int type, int enable) {
        Message message = Message.obtain();
        message.what = num;
        message.arg1 = type;
        message.arg2 = enable;
        mHandler.sendMessage(message);
    }

    private void clearLeftButton() {
        llBtnWireless.setSelected(false);
        llBtnScreenControl.setSelected(false);
        llBtnOther.setSelected(false);
        llWireless.setVisibility(View.GONE);
        llScreenControl.setVisibility(View.GONE);
        llOther.setVisibility(View.GONE);
    }

    private void clearDayOrNightRBCheck() {
        rbDay.setChecked(false);
        rbNight.setChecked(false);
    }

    private void clearFunctionRBCheck() {
        rbCloseScreen.setChecked(false);
        rbBright.setChecked(false);
        rbScreenSaver.setChecked(false);
    }

    private void clearTimeRBCheck() {
        rbTime1.setChecked(false);
        rbTime5.setChecked(false);
        rbTime30.setChecked(false);
    }

    private void setPopupWindowListener() {
        llBtnWireless.setOnClickListener(this);
        llBtnScreenControl.setOnClickListener(this);
        llBtnOther.setOnClickListener(this);
        seekBarBrightness.setOnSeekBarChangeListener(this);
        seekBarVolume.setOnSeekBarChangeListener(this);
        ivFmSwitch.setOnClickListener(this);

    }

    private void updateWifiBtn() {
        if (llBtnWifi.isSelected()) {
            WifiSwitchClose();
        } else {
            WifiSwitchOpen();
        }
    }

    private void WifiSwitchClose() {
        llBtnWifi.setSelected(false);
        wifiUtils.closeWifi();
        tvWifi.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
        tvWifiStatus.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
        tvWifiStatus.setText(R.string.setting_closed);
    }

    private void WifiSwitchOpen() {
        if (!wifiUtils.isWifiEnable()) {
            wifiUtils.openWifi();
        }
        llBtnWifi.setSelected(true);
        tvWifi.setTextColor(mContext.getResources().getColor(R.color.colorBlue));
        tvWifiStatus.setTextColor(mContext.getResources().getColor(R.color.colorBlue));
        tvWifiStatus.setText(R.string.setting_opened);
    }

    private void wifiOpenOrClose(boolean isOpen) {
        if (isOpen) {
            if (!wifiUtils.isWifiEnable()) {
                wifiUtils.openWifi();
            }
        } else {
            wifiUtils.closeWifi();
        }
    }

    private void updateWifiBtnStatus(boolean isOpen) {
        if (isOpen) {
            llBtnWifi.setSelected(true);
            tvWifi.setTextColor(mContext.getResources().getColor(R.color.colorBlue));
            tvWifiStatus.setTextColor(mContext.getResources().getColor(R.color.colorBlue));
            tvWifiStatus.setText(R.string.setting_opened);
        } else {
            llBtnWifi.setSelected(false);
            tvWifi.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
            tvWifiStatus.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
            tvWifiStatus.setText(R.string.setting_closed);
        }
    }

    private void settingsSwitchOff(LinearLayout llButton, ImageView ivIcon, TextView tvStatus) {
        llButton.setSelected(false);
        ivIcon.setSelected(false);
        tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
        tvStatus.setText(R.string.setting_closed);
    }

    private void settingsSwitchOn(LinearLayout llButton, ImageView ivIcon, TextView tvStatus) {
        llButton.setSelected(true);
        ivIcon.setSelected(true);
        tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorBlue));
        tvStatus.setText(R.string.setting_opened);
    }

    private void update4GBtn(boolean isOpen) {
        if (isOpen) {
            tv4GStatus.setText(R.string.mobile_data_status_connected);
            tv4GStatus.setTextColor(mContext.getResources().getColor(R.color.colorBlue));
        } else {
            tv4GStatus.setText(R.string.mobile_data_status_unconnected);
            tv4GStatus.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
        }
    }

    /**
     * 按钮选中与selected相反
     *
     * @param id
     */
    private void updateSettingButton(int id) {
        LinearLayout llButton;
        ImageView ivIcon;
        TextView tvStatus;
        switch (id) {
            case 1:
                llButton = llBtnHotspot;
                ivIcon = ivHotspot;
                tvStatus = tvHotspotStatus;
                break;
            case 2:
                llButton = llBtnGps;
                ivIcon = ivGps;
                tvStatus = tvGpsStatus;
                mSettingsUtils.openGPS(!llButton.isSelected());
                break;
            case 3:
                llButton = llBtnMobileData;
                ivIcon = ivMobileData;
                tvStatus = tvMobileDataStatus;
                break;
            default:
                llButton = llBtnBlueTooth;
                ivIcon = ivBlueTooth;
                tvStatus = tvBlueTooth;
                mSettingsUtils.openOrCloseBT(!llBtnBlueTooth.isSelected());
                break;
        }
        boolean isSelector = llButton.isSelected();
        if (isSelector) {
            settingsSwitchOff(llButton, ivIcon, tvStatus);
        } else {
            settingsSwitchOn(llButton, ivIcon, tvStatus);
        }

        if (id == 3) {
            update4GBtn(!isSelector);
        }
    }

    /**
     * 根据同行者语音命令开启和关闭
     *
     * @param btnType 区分按钮
     * @param enable  是否选中
     */
    private void updateSettingButtonByTXZ(int btnType, int enable) {
        LinearLayout llButton;
        ImageView ivIcon;
        TextView tvStatus;
        boolean switchOpen;
        if (enable == 1) {
            switchOpen = true;
        } else {
            switchOpen = false;
        }
        switch (btnType) {
            case 1:
                llButton = llBtnHotspot;
                ivIcon = ivHotspot;
                tvStatus = tvHotspotStatus;
                break;
            case 2:
                llButton = llBtnGps;
                ivIcon = ivGps;
                tvStatus = tvGpsStatus;
                mSettingsUtils.openGPS(switchOpen);
                break;
            case 3:
                llButton = llBtnMobileData;
                ivIcon = ivMobileData;
                tvStatus = tvMobileDataStatus;
                update4GBtn(switchOpen);
                break;
            default:
                llButton = llBtnBlueTooth;
                ivIcon = ivBlueTooth;
                tvStatus = tvBlueTooth;
                mSettingsUtils.openOrCloseBT(switchOpen);
                break;
        }
        if (switchOpen) {
            settingsSwitchOn(llButton, ivIcon, tvStatus);
        } else {
            settingsSwitchOff(llButton, ivIcon, tvStatus);
        }
    }


    /**
     * 更新拖动后的seekBar值
     *
     * @param type 区分seekBar
     */
    private void updateSeekBarProgress(int type) {
        if (type == 0) {
            int progress = seekBarBrightness.getProgress();
            mSettingsUtils.progressChangeToBrightness(progress);
            tvBrightnessValue.setText(String.valueOf(progress));
        } else {
            int volume = seekBarVolume.getProgress();
            mSettingsUtils.setVolume(volume);
            tvVolumeValue.setText(String.valueOf(volume));
        }
    }

    /**
     * @param value 屏幕亮度值
     */
    private void updateBrightness(int value) {
        String progressValue = String.valueOf(value);
        tvBrightnessValue.setText(progressValue);
        seekBarBrightness.setProgress(value);
        mSettingsUtils.progressChangeToBrightness(value);
    }

    public void clearHandle() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId() == R.id.sb_brightness) {
            sendMessageToHomeActivity(HANDLE_POP_UPDATE_SEEK_BAR, 0);
        } else {
            sendMessageToHomeActivity(HANDLE_POP_UPDATE_SEEK_BAR, 1);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar.getId() == R.id.sb_brightness) {
            clearDayOrNightRBCheck();
        }
    }

    @Override
    public void openStatus(int type) {
        Log.d(TAG, "openStatus: " + type);
        Message message = Message.obtain();
        message.what = 7;
        if (type == 1) {
            message.arg1 = 2;
        }
        if (type == 2) {
            message.arg1 = 4;
        }
        message.obj = true;
        mHandler.sendMessage(message);
    }

    @Override
    public void closeStatus(int type) {
        Log.d(TAG, "openStatus: " + type);
        Message message = Message.obtain();
        message.what = 7;
        if (type == 1) {
            message.arg1 = 2;
        }
        if (type == 2) {
            message.arg1 = 4;
        }
        message.obj = false;
        mHandler.sendMessage(message);
    }

    private void updateBtnByListener(int btnType, boolean isOpen) {
        LinearLayout llButton;
        ImageView ivIcon;
        TextView tvStatus;

        switch (btnType) {
            case 1:
                llButton = llBtnHotspot;
                ivIcon = ivHotspot;
                tvStatus = tvHotspotStatus;
                break;
            case 2:
                llButton = llBtnGps;
                ivIcon = ivGps;
                tvStatus = tvGpsStatus;
                break;
            case 3:
                llButton = llBtnMobileData;
                ivIcon = ivMobileData;
                tvStatus = tvMobileDataStatus;
                break;
            default:
                llButton = llBtnBlueTooth;
                ivIcon = ivBlueTooth;
                tvStatus = tvBlueTooth;
                break;
        }
        if (isOpen) {
            settingsSwitchOn(llButton, ivIcon, tvStatus);
        } else {
            settingsSwitchOff(llButton, ivIcon, tvStatus);
        }
    }

    private static class InnerHandler extends Handler {
        private final WeakReference<SettingsWindowActivity> activityWeakReference;

        private InnerHandler(SettingsWindowActivity popupWindow) {
            this.activityWeakReference = new WeakReference<>(popupWindow);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SettingsWindowActivity mPopupWindow = activityWeakReference.get();
            switch (msg.what) {
                case HANDLE_POP_WIFI_BTN://wifi按钮
                    mPopupWindow.updateWifiBtn();
                    break;
                case HANDLE_POP_UPDATE_BTN:// 更新按钮状态
                    mPopupWindow.updateSettingButton(msg.arg1);
                    Log.d(TAG, "handleMessage: " + mPopupWindow.mSettingsUtils.isGpsOpen());
                    break;
                case HANDLE_POP_UPDATE_BRIGHTNESS:// 更新屏幕亮度seekBar Progress
                    mPopupWindow.updateBrightness(msg.arg1);
                    break;
                case HANDLE_POP_UPDATE_SEEK_BAR:// 更新seekBar Progress
                    mPopupWindow.updateSeekBarProgress(msg.arg1);
                    break;
                case HANDLE_POP_UPDATE_DATA:
                    mPopupWindow.setData();
                    break;
                case HANDLE_POP_UPDATE_BTN_TXZ:
                    mPopupWindow.updateSettingButtonByTXZ(msg.arg1, msg.arg2);
                    break;
                case HANDLE_POP_UPDATE_BTN_BY_LISTENER:
                    if (msg.arg1 == 4) {
                        mPopupWindow.updateBtnByListener(msg.arg1, (Boolean) msg.obj);
                    }
                    if (msg.arg1 == 2) {
                        mPopupWindow.updateWifiBtnStatus((Boolean) msg.obj);
                    }

                    break;
            }
        }
    }


    /**
     * 同行者广播回调
     *
     * @param type         类型
     * @param txzOperation 实体类
     */
    @Override
    public void notifyActivity(int type, TXZOperation txzOperation) {
        switch (type) {
            case 1:
                int light = txzOperation.getLight();
                txzUpdateBrightnessOrVolume(0, light);
                break;
            case 2:
                int volume = txzOperation.getVolume();
                txzUpdateBrightnessOrVolume(1, volume);
                break;
            case 3:
                boolean enableWifi = txzOperation.isEnableWifi();
                txzUpdateWifiSwitch(enableWifi);
                break;
            case 4:
                boolean screenOn = txzOperation.isScreenOpen();
                if (screenOn) {

                } else {

                }
                break;
            case 5:
//                activityTools.goHome();
                break;
            case 6:
                boolean enableBluetooth = txzOperation.isEnableBluetooth();
                txzUpdateSwitch(4, enableBluetooth);
                break;
        }
    }

    public void txzUpdateBrightnessOrVolume(final int type, int value) {
        final int maxValue;
        SeekBar mSeekBar;
        if (type == 0) {
            maxValue = 100;
            mSeekBar = seekBarBrightness;
        } else {
            maxValue = 15;
            mSeekBar = seekBarVolume;
        }
        if (value != 0 && value != maxValue) {
            value = mSeekBar.getProgress() + value;
            if (value < 0) {
                value = 0;
            }
            if (value > maxValue) {
                value = maxValue;
            }
        }
        mSeekBar.setProgress(value);
        sendMessageToHomeActivity(HANDLE_POP_UPDATE_SEEK_BAR, type);
    }

    public void txzUpdateWifiSwitch(boolean enable) {
        llBtnWifi.setSelected(enable);
        sendMessageToHomeActivity(HANDLE_POP_WIFI_BTN);
    }

    public void txzUpdateSwitch(int btnType, boolean enable) {
        if (enable) {
            sendMessageToHomeActivity(HANDLE_POP_UPDATE_BTN_TXZ, btnType, 1);
        } else {
            sendMessageToHomeActivity(HANDLE_POP_UPDATE_BTN_TXZ, btnType, 0);
        }
    }

    private void registerSettingsReceiver() {
        Log.d(TAG, "registerSettingsReceiver: ");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mPopupWindowBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerSettingsReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPopupWindowBroadcastReceiver != null) {
            unregisterReceiver(mPopupWindowBroadcastReceiver);
        }
    }

}
