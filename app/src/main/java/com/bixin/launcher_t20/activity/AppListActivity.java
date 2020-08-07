package com.bixin.launcher_t20.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.bixin.launcher_t20.model.receiver.APPReceiver;
import com.bixin.launcher_t20.model.tools.InterfaceCallBackManagement;
import com.bixin.launcher_t20.model.tools.StartActivityTool;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.RxActivity;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Altair
 * @date :2019.10.28 上午 10:03
 * @description: AppList页面
 */
public class AppListActivity extends RxActivity implements OnRecyclerViewItemListener, OnAppUpdateListener {
    private final static String TAG = "AppListActivity";
    private LauncherApplication myApplication;
    private ArrayList<AppInfo> appInfoArrayList = new ArrayList<>();
    private Context mContext;
    private StartActivityTool mActivityTools;
    private RecyclerGridViewAdapter mRecyclerGridViewAdapter;
    private CompositeDisposable compositeDisposable;
    private RecyclerView recyclerView;
    private APPReceiver mUpdateAppReceiver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        init();
        initView();
//        initData();
        initAppInfo();
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
        mUpdateAppReceiver = new APPReceiver();
        compositeDisposable = new CompositeDisposable();
        myApplication = (LauncherApplication) getApplication();
//        mActivityTools = new StartActivityTool(mContext);
        mActivityTools = new StartActivityTool();
        InterfaceCallBackManagement.getInstance().setOnAppUpdateListener(this);
        registerAppReceiver();
    }

    private void initView() {
        recyclerView = findViewById(R.id.rcv_app);
        GridLayoutManager manager = new GridLayoutManager(this, 5);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
    }


//    private void initData() {
//        getAppList();
//    }

    private void initAppInfo() {
        compositeDisposable.add(Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                getAppList(emitter);
                emitter.onNext(true);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(aBoolean -> {
                    mRecyclerGridViewAdapter = new RecyclerGridViewAdapter(this, appInfoArrayList);
                    recyclerView.setAdapter(mRecyclerGridViewAdapter);
                    mRecyclerGridViewAdapter.setOnRecyclerViewItemListener(this);
                }));
    }

    public void getAppList(ObservableEmitter<Boolean> emitter) {
        appInfoArrayList.clear();
//        if (myApplication.getShowAppList().size() <= 3) {
        myApplication.initAppList();
//        }
        appInfoArrayList = myApplication.getShowAppList();
        emitter.onNext(true);
    }

    @Override
    public void onItemClickListener(int position, String packageName) {
        mActivityTools.launchAppByPackageName(packageName);
    }

    @Override
    public void updateAppList() {
        Log.d(TAG, "updateAppList: ");
        if (mRecyclerGridViewAdapter != null) {
            compositeDisposable.add(Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                    getAppList(emitter);
                    emitter.onNext(true);
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .compose(bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribe(aBoolean -> {
                        mRecyclerGridViewAdapter.setAppInfoArrayList(appInfoArrayList);
                        mRecyclerGridViewAdapter.notifyDataSetChanged();
                    }));

        }
    }

    private void registerAppReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
//        filter.addAction(Customer.ACTION_TXZ_CUSTOM_COMMAND);
        filter.addDataScheme("package");
        registerReceiver(mUpdateAppReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mUpdateAppReceiver != null) {
            unregisterReceiver(mUpdateAppReceiver);
        }
        mContext = null;
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
            compositeDisposable.clear();
            compositeDisposable = null;
        }
    }
}
