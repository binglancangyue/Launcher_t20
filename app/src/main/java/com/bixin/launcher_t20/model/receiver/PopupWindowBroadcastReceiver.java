package com.bixin.launcher_t20.model.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.bixin.launcher_t20.model.listener.OnSettingsStatusListener;

/**
 * @author Altair
 * @date :2019.10.24 下午 02:33
 * @description: 设置PopupWindow 弹出广播
 */
public class PopupWindowBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "PopupWindowBroadcast";
    private OnSettingsStatusListener mListener;

    public PopupWindowBroadcastReceiver(OnSettingsStatusListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        Log.d(TAG, "onReceive:action " + action);
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            int mWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (mWifiState) {
                case WifiManager.WIFI_STATE_ENABLED://已打开
                    Log.d(TAG, "onReceive: wifi open");
                    sendToActivity(1, true);
                    break;
                case WifiManager.WIFI_STATE_DISABLED://已关闭
                    Log.d(TAG, "onReceive: wifi close");
                    sendToActivity(1, false);
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    //打开中
                    break;
            }
        }
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
            if (BluetoothAdapter.STATE_ON == blueState) {
                Log.d(TAG, "onReceive: bt open");
                sendToActivity(2, true);
            }
            if (BluetoothAdapter.STATE_OFF == blueState) {
                Log.d(TAG, "onReceive: bt close");
                sendToActivity(2, false);
            }
        }
    }

    private void sendToActivity(int type, boolean isOpen) {
        if (mListener != null) {
            if (isOpen) {
                mListener.openStatus(type);
            } else {
                mListener.closeStatus(type);
            }
        }
    }

}
