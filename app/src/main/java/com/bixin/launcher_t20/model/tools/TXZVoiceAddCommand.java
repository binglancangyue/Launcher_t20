package com.bixin.launcher_t20.model.tools;

import com.txznet.sdk.TXZAsrManager;
import com.txznet.sdk.TXZConfigManager;

/**
 * @author Altair
 * @date :2020.01.10 上午 11:26
 * @description: 同行者本地指令注册
 */
public class TXZVoiceAddCommand {

    public TXZVoiceAddCommand() {
        if (TXZConfigManager.getInstance().isInitedSuccess()) {
            customRegistration();
        }
    }

    /**
     * 注册语音命令
     *
     * @param commands 命令
     * @param data     命令标识
     */
    private void registerCommand(String[] commands, String data) {
        TXZAsrManager.getInstance().regCommand(commands, data);
    }

    /**
     * 定制同行者语音命令
     */
    private void customRegistration() {
        //Open
        registerCommand(new String[]{"打开应用管理", "打开应用管理器", "打开应用列表"},
                "CMD_OPEN_APP_MGT");
        registerCommand(new String[]{"打开文件管理", "打开文件管理器"}, "CMD_OPEN_FILE_MGT");
        registerCommand(new String[]{"打开记录仪", "打开行车记录仪"}, "CMD_OPEN_CAMERA_MGT");
        registerCommand(new String[]{"打开视频回放", "打开视频"}, "CMD_OPEN_V_PLAYBACK");
//       registerCommand(new String[]{"打开蓝牙"}, "CMD_OPEN_V_Bluetooth");
        registerCommand(new String[]{"打开电视家", "打开电视机"}, "CMD_OPEN_TV_HOME");
        registerCommand(new String[]{"打开FM"}, "CMD_OPEN_FM");
        registerCommand(new String[]{"打开喜马拉雅"}, "CMD_OPEN_XIMALAYA");


        //Close
        registerCommand(new String[]{"关闭应用管理", "关闭应用管理器", "退出应用管理", "退出应用管理器"},
                "CMD_CLOSE_APP_MGT");
        registerCommand(new String[]{"退出电视家", "关闭电视家", "退出电视机", "关闭电视机"},
                "CMD_CLOSE_TV_HOME");
    }
}
