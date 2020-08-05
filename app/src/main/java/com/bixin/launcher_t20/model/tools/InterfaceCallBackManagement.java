package com.bixin.launcher_t20.model.tools;


import com.bixin.launcher_t20.model.listener.OnAppUpdateListener;
import com.bixin.launcher_t20.model.listener.OnLocationListener;

/**
 * @author Altair
 * @date :2020.04.01 上午 10:23
 * @description:
 */
public class InterfaceCallBackManagement {
    private OnLocationListener mOnLocationListener;
    private OnAppUpdateListener mOnAppUpdateListener;

    public static InterfaceCallBackManagement getInstance() {
        return SingleHolder.management;
    }

    public static class SingleHolder {
        static InterfaceCallBackManagement management = new InterfaceCallBackManagement();
    }

    public void setOnAppUpdateListener(OnAppUpdateListener onAppUpdateListener) {
        this.mOnAppUpdateListener = onAppUpdateListener;
    }

    public void setOnLocationListener(OnLocationListener listener) {
        this.mOnLocationListener = listener;
    }

    public void gpsSpeedChange() {
        if (mOnLocationListener == null) {
            return;
        }
        mOnLocationListener.gpsSpeedChanged();
    }

    public void updateAppList() {
        if (mOnAppUpdateListener != null) {
            mOnAppUpdateListener.updateAppList();
        }
    }

    public void updateWeather(String weatherInfo) {
        if (mOnLocationListener != null) {
            mOnLocationListener.updateWeather(weatherInfo);
        }
    }

}
