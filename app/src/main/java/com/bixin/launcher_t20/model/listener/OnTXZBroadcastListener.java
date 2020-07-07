package com.bixin.launcher_t20.model.listener;

import com.bixin.launcher_t20.model.bean.TXZOperation;

/**
 * @author Altair
 * @date :2019.11.05 下午 04:05
 * @description:
 */
public interface OnTXZBroadcastListener {
    void notifyActivity(int type, TXZOperation txzOperation);
}
