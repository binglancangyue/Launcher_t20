package com.bixin.launcher_t20.model.tools;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.bixin.launcher_t20.activity.LauncherApplication;

import static android.support.constraint.Constraints.TAG;

public class WifiTool {
    private Context mContext;
    private WifiManager mWifiManager;

    public WifiTool() {
        mContext = LauncherApplication.getInstance();
        mWifiManager =
                (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * wifi是否打开
     *
     * @return open or close
     */
    public boolean isWifiEnable() {
        boolean isEnable = false;
        if (mWifiManager != null) {
            Log.d(TAG, "isWifiEnable: " + mWifiManager.isWifiEnabled());
            if (mWifiManager.isWifiEnabled()) {
                isEnable = true;
            }
        }
        return isEnable;
    }

    /**
     * 打开WiFi
     */
    public void openWifi() {
        if (mWifiManager != null && !isWifiEnable()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 关闭WiFi
     */
    public void closeWifi() {
        if (mWifiManager != null && isWifiEnable()) {
            mWifiManager.disconnect();
            mWifiManager.setWifiEnabled(false);
        }
    }

}

