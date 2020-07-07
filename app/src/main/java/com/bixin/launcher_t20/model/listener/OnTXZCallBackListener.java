package com.bixin.launcher_t20.model.listener;

/**
 * @author Altair
 * @date :2019.11.05 下午 06:00
 * @description:
 */
public interface OnTXZCallBackListener {
    void openAPP(String packageName);
    void closeApp(String packageName);
}
