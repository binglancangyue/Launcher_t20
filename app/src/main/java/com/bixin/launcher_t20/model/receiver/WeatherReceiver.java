package com.bixin.launcher_t20.model.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bixin.launcher_t20.model.tools.CustomValue;
import com.bixin.launcher_t20.model.tools.CallBackManagement;

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
    }
}
