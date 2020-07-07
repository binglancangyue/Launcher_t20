package com.bixin.launcher_t20.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.bixin.launcher_t20.R;
import com.bixin.launcher_t20.adapter.RecyclerGridViewAdapter;
import com.bixin.launcher_t20.model.bean.AppInfo;
import com.bixin.launcher_t20.model.listener.OnAppUpdateListener;
import com.bixin.launcher_t20.model.listener.OnRecyclerViewItemListener;
import com.bixin.launcher_t20.model.tools.InterfaceCallBackManagement;
import com.bixin.launcher_t20.model.tools.StartActivityTool;

import java.util.ArrayList;

/**
 * @author Altair
 * @date :2019.10.28 上午 10:03
 * @description: AppList页面
 */
public class AppListActivity extends Activity implements OnRecyclerViewItemListener, OnAppUpdateListener {
    private final static String TAG = "AppListActivity";
    private LauncherApplication myApplication;
    private ArrayList<AppInfo> appInfoArrayList = new ArrayList<>();
    private Context mContext;
    private StartActivityTool mActivityTools;
    private RecyclerGridViewAdapter mRecyclerGridViewAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        init();
        initData();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {
        mContext = this;
        myApplication = (LauncherApplication) getApplication();
//        mActivityTools = new StartActivityTool(mContext);
        mActivityTools = new StartActivityTool();
        InterfaceCallBackManagement.getInstance().setOnAppUpdateListener(this);
    }

    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.rcv_app);
        GridLayoutManager manager = new GridLayoutManager(this, 5);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);

        mRecyclerGridViewAdapter = new RecyclerGridViewAdapter(this, appInfoArrayList);
        recyclerView.setAdapter(mRecyclerGridViewAdapter);
        mRecyclerGridViewAdapter.setOnRecyclerViewItemListener(this);
    }


    private void initData() {
        getAppList();
    }

    public void getAppList() {
        appInfoArrayList.clear();
//        if (myApplication.getShowAppList().size() <= 3) {
        myApplication.initAppList();
//        }
        appInfoArrayList = myApplication.getShowAppList();
    }

    @Override
    public void onItemClickListener(int position, String packageName) {
        mActivityTools.launchAppByPackageName(packageName);
    }

    @Override
    public void updateAppList() {
        Log.d(TAG, "updateAppList: ");
        if (mRecyclerGridViewAdapter != null) {
            getAppList();
            mRecyclerGridViewAdapter.setAppInfoArrayList(appInfoArrayList);
            mRecyclerGridViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContext = null;
    }
}
