package com.bixin.launcher_t20.model.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.bixin.launcher_t20.activity.MyApplication;

import static com.bixin.launcher_t20.model.tools.CustomValue.SP_NAME;

/**
 * @author Altair
 * @date :2019.10.28 上午 10:03
 * @description: SharedPreferences 工具
 */
public class SharedPreferencesTool {
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private Context mContext;

    @SuppressLint("CommitPrefEdits")
    public SharedPreferencesTool() {
        this.mContext = MyApplication.getApplication();
        this.mPreferences = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        this.mEditor = mPreferences.edit();
    }

    public SharedPreferences getSharePreferences() {
        return mPreferences;
    }

    public void saveString(String name, String value) {
        mEditor.putString(name, value);
        mEditor.apply();
    }

    public void saveInt(String name, int value) {
        mEditor.putInt(name, value);
        mEditor.apply();
    }

    public void saveBoolean(String name, boolean value) {
        mEditor.putBoolean(name, value);
        mEditor.apply();
    }
}
