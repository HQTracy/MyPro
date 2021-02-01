package com.eryanet.mfileoperation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.eryanet.common.utils.Logger;
import com.eryanet.mfileoperation.service.UpgradeService;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    //动态申请权限
    protected String[] needPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    Handler mHandler;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Logger.debug("msg what: " + msg.what);
            }
        };

        initPremisson();
//        UpgradeManager.init(MainActivity.this);
        findViewById(R.id.copytosdcard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bindUpgradeService();
//                Intent service = new Intent(MainActivity.this, UpgradeService.class);
//                startService(service);
//                Logger.debug("hahahha  ---- ");
//                UpgradeManager.putAssetsToSDCard(MainActivity.this, UpgradeManager.mFolderName, UpgradeManager.mBasePath);
//                Logger.debug("hahahha  ---- end");
            }
        });

        findViewById(R.id.unzip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                boolean isExist = FileUtil.isFileExist(UpgradeManager.mBasePath + File.separator + UpgradeManager.UPGRADE_CONFNAME);
//                Logger.debug("path: " + UpgradeManager.mBasePath + File.separator + UpgradeManager.UPGRADE_CONFNAME);
//                Logger.debug("isExist: " + isExist);
//                boolean isExist = FileUtil.isFolderExist(UpgradeManager.mFileAbsolutePath);
//                Logger.debug("path：" + getApplicationContext().getFilesDir().getAbsolutePath());
//                try {
//                    String str = FileUtil.readFile(UpgradeManager.mFileSubPath + File.separator + UpgradeManager.UPGRADE_CONFNAME);
//                    Logger.debug("readFile str: " + str);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                FileUtil.deleteFile(UpgradeManager.mFileAbsolutePath);
                /*try {
                    Logger.info("unzip  ---- ");
                    ZipUtil.UnZipFolder(UpgradeManager.mZipAbsolutePath, UpgradeManager.mFileAbsolutePath);
                    Logger.info("unzip  ---- end");
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
        });

        findViewById(R.id.check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*try {
                    String s = UpgradeManager.fromAssets(MainActivity.this, "Linkcore/upgrade.conf");
                    String md5sum = MD5Util.md5(new File(UpgradeManager.mZipAbsolutePath));
                    Logger.debug("s: " + s + " md5sum: " + md5sum);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
        });

    }

    //申请系统权限
    private void initPremisson() {
        if (PermissionsUtil.hasPermission(this, needPermissions)) {
            Logger.debug("requestPermission hasPermission");
        } else {
            PermissionsUtil.requestPermission(this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permissions) {
                    Logger.info("requestPermission permissionGranted");
                }

                @Override
                public void permissionDenied(@NonNull String[] permissions) {
                    Logger.error("requestPermission permissionDenied");
                }
            }, needPermissions);
        }
    }

    private UpgradeService mService = null;

    //升级service 统一管理（包括dialog）
    private void bindUpgradeService() {
        bindService(new Intent(this, UpgradeService.class), upgradeConn, Service.BIND_AUTO_CREATE);
    }

    private ServiceConnection upgradeConn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (mService != null)
                mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Logger.debug("recv upgradeConn onServiceConnected");
            UpgradeService.MyBinder binder = (UpgradeService.MyBinder) service;
            mService = binder.getService();
            checkUpgrade();
        }
    };

    private void checkUpgrade() {
        if (mService != null) {
            mService.check(mHandler);
        }
    }

}
