package com.bixin.launcher_t20.model.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * @author Altair
 * @date :2019.11.23 上午 11:32
 * @description:
 */
public class ScreenOffAdminReceiver extends DeviceAdminReceiver {
    private void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        showToast(context,
                "设备管理器激活成功");
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        showToast(context,
                "设备管理器激活失败");
    }
}
