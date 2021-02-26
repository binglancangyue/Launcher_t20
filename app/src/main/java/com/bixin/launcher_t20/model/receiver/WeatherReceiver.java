package com.bixin.launcher_t20.model.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.bixin.launcher_t20.activity.LauncherApp;
import com.bixin.launcher_t20.model.tools.CallBackManagement;
import com.bixin.launcher_t20.model.tools.CustomValue;

/**
 * @author Altair
 * @date :2020.04.02 下午 03:10
 * @description:
 */
public class WeatherReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        Log.d("WeatherReceiver", "onReceive:action " + action);
        if (action.equals(CustomValue.ACTION_UPDATE_WEATHER)) {
            String weather = intent.getStringExtra("weatherInfo");
            CallBackManagement.getInstance().updateWeather(weather);
        }
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            NetworkInfo info =
                    intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (info == null) {
                ConnectivityManager connectivityManager =
                        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                info = connectivityManager.getActiveNetworkInfo();

            }
            if (info == null) {
                return;
            }
            if (!info.isConnected()) {
                return;
            }
            if (LauncherApp.getInstance().isFirstLaunch()) {
                LauncherApp.getInstance().setFirstLaunch(false);
                CallBackManagement.getInstance().updateWeather("update");
            }
        }
        if (action.equals(CustomValue.ACTION_GAODE_SEND)) {
            int keyCode = intent.getIntExtra("KEY_TYPE", 0);
            if (keyCode == 10001) {
                Log.d("WeatherReceiver", "ACTION_GAODE_SEND: ");
                Log.d("WeatherReceiver", "onReceive:NEW_ICON " + intent.getIntExtra("NEW_ICON", -1));
                Log.d("WeatherReceiver", "onReceive:ICON " + intent.getIntExtra("ICON", -1));
            }
        }
    }
}
