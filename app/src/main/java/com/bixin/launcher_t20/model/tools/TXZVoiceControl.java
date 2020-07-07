package com.bixin.launcher_t20.model.tools;

import android.util.Log;

import com.bixin.launcher_t20.model.listener.OnTXZCallBackListener;
import com.txznet.sdk.TXZAsrManager;
import com.txznet.sdk.TXZConfigManager;
import com.txznet.sdk.TXZTtsManager;


/**
 * @author Altair
 * @date :2020.01.10 上午 11:26
 * @description: 同行者本地指令监听
 */
public class TXZVoiceControl {
    private OnTXZCallBackListener mListener;

    public TXZVoiceControl(OnTXZCallBackListener listener) {
        this.mListener = listener;
        if (TXZConfigManager.getInstance().isInitedSuccess()) {
            Log.d("OnTXZCallBackListener", "isInitedSuccess: ");
            customCommandListener();
        } else {
            Log.d("OnTXZCallBackListener", ": !isInitedSuccess ");
        }
    }

    public void setListener(OnTXZCallBackListener listener) {
        this.mListener = listener;
    }

    /**
     * 同行者语音监听
     */
    private void customCommandListener() {
        TXZAsrManager.getInstance().addCommandListener(new TXZAsrManager.CommandListener() {
            @Override
            public void onCommand(String cmd, String data) {
                switch (data) {
                case "CMD_OPEN_APP_MGT":
                    openApp(null);
                    toSpeakText("APP管理已打开");
                    break;
                case "CMD_OPEN_FILE_MGT":
                    openApp(CustomValue.PACKAGE_NAME_FILE_MANAGER);
                    toSpeakText("文件管理已打开");
                    break;
                case "CMD_OPEN_CAMERA_MGT":
                    openApp(CustomValue.PACKAGE_NAME_DVR);
                    toSpeakText("记录仪已打开");
                    break;
                case "CMD_OPEN_V_PLAYBACK":
                    openApp(CustomValue.PACKAGE_NAME_ViDEO_PLAY_BACK);
                    toSpeakText("视频回放已打开");
                    break;
//                    case "CMD_OPEN_V_BLUETOOTH":
//                        openApp(CustomValue.PACKAGE_NAME_BLUETOOTH);
//                        toSpeakText("蓝牙已打开");
//                        break;
                case "CMD_OPEN_TV_HOME":
                    openApp(CustomValue.PACKAGE_NAME_TV_HOME);
                    break;
                case "CMD_OPEN_FM":
                    openApp(CustomValue.PACKAGE_NAME_FM);
                    break;
                case "CMD_OPEN_XIMALAYA":
                    openApp(CustomValue.PACKAGE_NAME_XIMALAYA);
                    break;
                case "CMD_CLOSE_TV_HOME":
                    closeApp(CustomValue.PACKAGE_NAME_TV_HOME);
                    break;
                default:
                    break;
            }
        }
        });
    }

    private void toSpeakText(String s) {
        TXZTtsManager.getInstance().speakText(s);
    }

    private void openApp(String packageName) {
        if (mListener != null) {
            mListener.openAPP(packageName);
        }
    }

    private void closeApp(String packageName) {
        if (mListener != null) {
            mListener.closeApp(packageName);
        }
    }

}
