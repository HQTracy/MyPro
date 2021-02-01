package com.eryanet.mfileoperation.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.eryanet.common.utils.Logger;
import com.eryanet.mfileoperation.manager.UpgradeManager;
import com.eryanet.mfileoperation.manager.UpgradeManager1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpgradeService extends Service {

    public final IBinder mBinder = new MyBinder();
    private UpgradeManager1 upgradeManager;
    private ExecutorService executorService;

    public class MyBinder extends Binder {
        public UpgradeService getService() {
            return UpgradeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }


    private void init() {
        executorService = Executors.newSingleThreadExecutor();
    }

    public void check(Handler handler) {
        upgradeManager = new UpgradeManager1(getApplicationContext(), handler);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    upgradeManager.checkUpgradeLinkcore();
                } catch (Exception e) {
                    Logger.error("checkUpgradeLinkcore Exception: " + e.toString());
                    e.printStackTrace();
                }
            }
        });
    }


}
