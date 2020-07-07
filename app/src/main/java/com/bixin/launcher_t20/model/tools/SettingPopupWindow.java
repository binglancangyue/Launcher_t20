package com.bixin.launcher_t20.model.tools;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bixin.launcher_t20.R;
import com.bixin.launcher_t20.activity.LauncherHomeActivity;
import com.bixin.launcher_t20.model.listener.OnSettingPopupWindowListener;
import com.bixin.launcher_t20.model.listener.OnSettingsStatusListener;
import com.bixin.launcher_t20.model.receiver.PopupWindowBroadcastReceiver;

import java.lang.ref.WeakReference;

import static android.content.ContentValues.TAG;
import static com.bixin.launcher_t20.model.tools.CustomValue.HANDLE_POP_UPDATE_BTN_BY_LISTENER;

/**
 * @author Altair
 * @date :2019.10.24 上午 09:46
 * @description: 设置PopupWindow
 */
public class SettingPopupWindow implements View.OnClickListener, SeekBar.OnSeekBarChangeListener,
        OnSettingsStatusListener {
    private OnSettingPopupWindowListener mListener;
    private Context mContext;
    private LauncherHomeActivity mHomeActivity;
    private PopupWindow popupWindow;
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


    private View parentView;
    private WindowManager.LayoutParams lp;
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


    public SettingPopupWindow(Context context) {
        this.mContext = context;
        this.mHomeActivity = (LauncherHomeActivity) context;
        mHandler = new InnerHandler(this);
//        mSettingsUtils = new SettingsFunctionTool(mContext);
        mSettingsUtils = new SettingsFunctionTool();
//        wifiUtils = new WifiTool(mContext);
        wifiUtils = new WifiTool();
        requestPermissionsTool = new RequestPermissionsTool();
//        mSharedPreferencesTool = new SharedPreferencesTool(mContext);
        mSharedPreferencesTool = new SharedPreferencesTool();
        mPopupWindowBroadcastReceiver = new PopupWindowBroadcastReceiver(this);
    }

    private void registerSettingsReceiver() {
        Log.d(TAG, "registerSettingsReceiver: ");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(mPopupWindowBroadcastReceiver, intentFilter);
    }

    @SuppressLint("InflateParams")
    public void createPopWindow() {
        if (popupWindow == null) {
            parentView = LayoutInflater.from(mContext).inflate(R.layout.activity_home, null);
            lp = mHomeActivity.getWindow().getAttributes();
            initPopupWindow();
            setPopupWindowListener(lp);
            setViewData();
            registerSettingsReceiver();
        }
    }

    private void setViewData() {
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
        Log.d(TAG, "setViewData:isBlueToothEnable " + mSettingsUtils.isBlueToothEnable());
        updateSettingButton(4);
        initScreenOutTime();
        Log.d(TAG, "setViewData: " + mSettingsUtils.isGpsOpen());

    }

    public void setOnSettingPopupWindowListener(OnSettingPopupWindowListener listener) {
        this.mListener = listener;
    }

    public void showPopupWindow() {
//        createPopWindow();
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            mHandler.sendEmptyMessage(CustomValue.HANDLE_POP_UPDATE_DATA);
//            setViewData();
            lp.alpha = 0.4f;
            mHomeActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            mHomeActivity.getWindow().setAttributes(lp);
            popupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        }
    }


    private void initPopupWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.popup_window_setting, null, false);
        llBtnWireless = view.findViewById(R.id.ll_left_wifi);
        llBtnScreenControl = view.findViewById(R.id.ll_left_screen_control);
        llBtnOther = view.findViewById(R.id.ll_left_other);

        llWireless = view.findViewById(R.id.ll_wireless_data);
        llScreenControl = view.findViewById(R.id.ll_brightness);
        llOther = view.findViewById(R.id.ll_other);

        // Wireless data
        llBtnWifi = view.findViewById(R.id.ll_btn_wifi);
        tvWifi = view.findViewById(R.id.tv_wifi);
        tvWifiStatus = view.findViewById(R.id.tv_wifi_status);
        llBtnHotspot = view.findViewById(R.id.ll_btn_hotspot);
        ivHotspot = view.findViewById(R.id.iv_hotspot);
        tvHotspotStatus = view.findViewById(R.id.tv_hotspot_status);
        llBtnGps = view.findViewById(R.id.ll_btn_gps);
        ivGps = view.findViewById(R.id.iv_gps);
        tvGpsStatus = view.findViewById(R.id.tv_gps_status);
        llBtnMobileData = view.findViewById(R.id.ll_btn_mobile_data);
        ivMobileData = view.findViewById(R.id.iv_mobile_data);
        tvMobileDataStatus = view.findViewById(R.id.tv_mobile_data_status);
        tv4GStatus = view.findViewById(R.id.tv_4g_status);
        llBtnBlueTooth = view.findViewById(R.id.ll_btn_bluetooth);
        ivBlueTooth = view.findViewById(R.id.iv_bluetooth);
        tvBlueTooth = view.findViewById(R.id.tv_bluetooth_status);

        llBtnWifi.setOnClickListener(this);
        llBtnHotspot.setOnClickListener(this);
        llBtnGps.setOnClickListener(this);
        llBtnMobileData.setOnClickListener(this);
        llBtnBlueTooth.setOnClickListener(this);


        //Screen control
        rbCloseScreen = view.findViewById(R.id.rb_close_screen);
        rbBright = view.findViewById(R.id.rb_bright);
        rbScreenSaver = view.findViewById(R.id.rb_screen_saver);

        rbTime1 = view.findViewById(R.id.rb_time_1);
        rbTime5 = view.findViewById(R.id.rb_time_5);
        rbTime30 = view.findViewById(R.id.rb_time_30);

        rbDay = view.findViewById(R.id.rb_screen_control_day);
        rbNight = view.findViewById(R.id.rb_screen_control_night);


        rbCloseScreen.setOnClickListener(this);
        rbBright.setOnClickListener(this);
        rbScreenSaver.setOnClickListener(this);
        rbTime1.setOnClickListener(this);
        rbTime5.setOnClickListener(this);
        rbTime30.setOnClickListener(this);
        rbDay.setOnClickListener(this);
        rbNight.setOnClickListener(this);

        seekBarBrightness = view.findViewById(R.id.sb_brightness);
        tvBrightnessValue = view.findViewById(R.id.tv_brightness_value);

        // Other
        ivFmSwitch = view.findViewById(R.id.iv_switch_fm);
        seekBarVolume = view.findViewById(R.id.sb_volume);
        tvVolumeValue = view.findViewById(R.id.tv_volume_value);


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
        popupWindow.setAnimationStyle(R.style.SettingsTranslateAnim);

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
                sendMessageToHomeActivity(CustomValue.HANDLE_POP_UPDATE_BRIGHTNESS, 100);
                break;
            case R.id.rb_screen_control_night:
                setDayOrNightBtnCheck(rbNight);
//                mSharedPreferencesTool.saveBoolean("btn_night", true);
//                mSharedPreferencesTool.saveBoolean("btn_day", false);
                sendMessageToHomeActivity(CustomValue.HANDLE_POP_UPDATE_BRIGHTNESS, 40);
                break;
            case R.id.ll_btn_wifi:
//                sendMessageToHomeActivity(CustomValue.HANDLE_POP_WIFI_BTN);
                wifiOpenOrClose(!llBtnWifi.isSelected());
                break;
            case R.id.ll_btn_hotspot:
                sendMessageToHomeActivity(CustomValue.HANDLE_POP_UPDATE_BTN, 1);
                break;
            case R.id.ll_btn_gps:
                sendMessageToHomeActivity(CustomValue.HANDLE_POP_UPDATE_BTN, 2);
                break;
            case R.id.ll_btn_mobile_data:
                sendMessageToHomeActivity(CustomValue.HANDLE_POP_UPDATE_BTN, 3);
                break;
            case R.id.ll_btn_bluetooth:
//                sendMessageToHomeActivity(CustomValue.HANDLE_POP_UPDATE_BTN, 4);
                mSettingsUtils.openOrCloseBT(!llBtnBlueTooth.isSelected());
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

    private void sendMessageToHomeActivity(int num, int type) {
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

    private void setPopupWindowListener(WindowManager.LayoutParams lp) {
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lp.alpha = 1.0f;
                mHomeActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                mHomeActivity.getWindow().setAttributes(lp);
            }
        });
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

    private void wifiOpenOrClose(boolean isOpen) {
        if (isOpen) {
            if (!wifiUtils.isWifiEnable()) {
                wifiUtils.openWifi();
            }
        } else {
            wifiUtils.closeWifi();
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
            sendMessageToHomeActivity(CustomValue.HANDLE_POP_UPDATE_SEEK_BAR, 0);
        } else {
            sendMessageToHomeActivity(CustomValue.HANDLE_POP_UPDATE_SEEK_BAR, 1);
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
        private final WeakReference<SettingPopupWindow> activityWeakReference;

        private InnerHandler(SettingPopupWindow popupWindow) {
            this.activityWeakReference = new WeakReference<>(popupWindow);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SettingPopupWindow mPopupWindow = activityWeakReference.get();
            switch (msg.what) {
                case CustomValue.HANDLE_POP_WIFI_BTN://wifi按钮
                    mPopupWindow.updateWifiBtn();
                    break;
                case CustomValue.HANDLE_POP_UPDATE_BTN:// 更新按钮状态
                    mPopupWindow.updateSettingButton(msg.arg1);
                    Log.d(TAG, "handleMessage: " + mPopupWindow.mSettingsUtils.isGpsOpen());
                    break;
                case CustomValue.HANDLE_POP_UPDATE_BRIGHTNESS:// 更新屏幕亮度seekBar Progress
                    mPopupWindow.updateBrightness(msg.arg1);
                    break;
                case CustomValue.HANDLE_POP_UPDATE_SEEK_BAR:// 更新seekBar Progress
                    mPopupWindow.updateSeekBarProgress(msg.arg1);
                    break;
                case CustomValue.HANDLE_POP_UPDATE_DATA:
                    mPopupWindow.setViewData();
                    break;
                case CustomValue.HANDLE_POP_UPDATE_BTN_TXZ:
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
        sendMessageToHomeActivity(CustomValue.HANDLE_POP_UPDATE_SEEK_BAR, type);
    }

    public void txzUpdateWifiSwitch(boolean enable) {
        llBtnWifi.setSelected(enable);
        sendMessageToHomeActivity(CustomValue.HANDLE_POP_WIFI_BTN);
    }

    public void txzUpdateSwitch(int btnType, boolean enable) {
        if (enable) {
            sendMessageToHomeActivity(CustomValue.HANDLE_POP_UPDATE_BTN_TXZ, btnType, 1);
        } else {
            sendMessageToHomeActivity(CustomValue.HANDLE_POP_UPDATE_BTN_TXZ, btnType, 0);
        }
    }

    public void unregisterSettingsPopReceiver() {
        mContext.unregisterReceiver(mPopupWindowBroadcastReceiver);
    }

}
