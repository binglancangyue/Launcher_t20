package com.bixin.launcher_t20.model.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bixin.launcher_t20.model.bean.TXZOperation;
import com.bixin.launcher_t20.model.listener.OnTXZBroadcastListener;
import com.bixin.launcher_t20.model.tools.CustomValue;

import static android.support.constraint.Constraints.TAG;
import static com.bixin.launcher_t20.model.tools.CustomValue.ACTION_TXZ_SEND;

public class TXZBroadcastReceiver extends BroadcastReceiver {
    private OnTXZBroadcastListener onTXZListener;

    public TXZBroadcastReceiver() {

    }

    public TXZBroadcastReceiver(OnTXZBroadcastListener onTXZListener) {
        this.onTXZListener = onTXZListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "onReceive:action " + action);
        if (action != null && action.equals(ACTION_TXZ_SEND)) {
            String actionString = intent.getStringExtra("action");
            switch (actionString) {
                case "light.up":
                    sendToActivity(1, 20);
                    break;
                case "light.down":
                    sendToActivity(1, -20);
                    break;
                case "light.max":
                    sendToActivity(1, 100);
                    break;
                case "light.min":
                    sendToActivity(1, 0);
                    break;
                case "volume.up":
                    sendToActivity(2, 3);
                    break;
                case "volume.down":
                    sendToActivity(2, -3);
                    break;
                case "volume.max":
                    sendToActivity(2, 15);
                    break;
                case "volume.min":
                    sendToActivity(2, 1);
                    break;
                case "volume.mute":
                    boolean isMute = intent.getBooleanExtra("mute", true);
                    Log.d(TAG, "onReceive: " + isMute);
                    if (isMute) {
                        sendToActivity(2, 0);
                    }
                    break;
                case "wifi.open":
                    sendToActivity(3, 1);
                    break;
                case "wifi.close":
                    sendToActivity(3, 0);
                    break;
                case "screen.close":
                    sendToActivity(4, 0);
                    break;
                case "screen.open":
                    sendToActivity(4, 1);
                    break;
                case "go.home":
                    sendToActivity(5, 0);
                case "bluetooth.open":
                    sendToActivity(6, 1);
                    break;
                case "bluetooth.close":
                    sendToActivity(6, 0);
                    break;
                case "radio.open":
                    sendToActivity(7, 1);
                    break;
                case "radio.close":
                    sendToActivity(7, 0);
                    break;
            }
        }
        if (action.equals(CustomValue.ACTION_OPEN_TXZ_VIEW)) {
            sendToActivity(8);
        }
        if (action.equals(CustomValue.ACTION_QUICK_SETTINGS_VIEW)) {
            sendToActivity(9);
        }
        if (action.equals(CustomValue.ACTION_TXZ_INIT)) {
            sendToActivity(-1);
        }
    }

    private void sendToActivity(int type, int value) {
        TXZOperation txzOperation = new TXZOperation();
        switch (type) {
            case 1:
                txzOperation.setLight(value);
                break;
            case 2:
                txzOperation.setVolume(value);
                break;
            case 3:
                if (value == 1) {
                    txzOperation.setEnableWifi(true);
                } else {
                    txzOperation.setEnableWifi(false);
                }
                break;
            case 4:
                if (value == 1) {
                    txzOperation.setScreenOpen(true);
                } else {
                    txzOperation.setScreenOpen(false);
                }
                break;
            case 6:
                if (value == 1) {
                    txzOperation.setEnableBluetooth(true);
                } else {
                    txzOperation.setEnableBluetooth(false);
                }
                break;
            case 7:
                if (value == 1) {
                    txzOperation.setEnableFM(true);
                } else {
                    txzOperation.setEnableFM(false);
                }
                break;
            default:
                sendToActivity(-1);
                break;
        }
        if (onTXZListener != null) {
            onTXZListener.notifyActivity(type, txzOperation);
        }
    }

    private void sendToActivity(int type) {
        if (onTXZListener != null) {
            onTXZListener.notifyActivity(type, null);
        }
    }

}
