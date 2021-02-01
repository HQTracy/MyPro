//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.github.dfqin.grantor;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.text.TextUtils;

import com.github.dfqin.grantor.PermissionsUtil.TipInfo;

import java.io.Serializable;

public class PermissionActivity extends Activity {
    private static final int PERMISSION_REQUEST_CODE = 64;
    private boolean isRequireCheck;
    private String[] permission;
    private String key;
    private boolean showTip;
    private TipInfo tipInfo;
    private final String defaultTitle = "帮助";
    private final String defaultContent = "当前应用缺少必要权限。\n \n 请点击 \"设置\"-\"权限\"-打开所需权限。";
    private final String defaultCancel = "取消";
    private final String defaultEnsure = "设置";

    public PermissionActivity() {
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getIntent() != null && this.getIntent().hasExtra("permission")) {
            this.isRequireCheck = true;
            this.permission = this.getIntent().getStringArrayExtra("permission");
            this.key = this.getIntent().getStringExtra("key");
            this.showTip = this.getIntent().getBooleanExtra("showTip", true);
            Serializable ser = this.getIntent().getSerializableExtra("tip");
            if (ser == null) {
                this.tipInfo = new TipInfo("帮助", "当前应用缺少必要权限。\n \n 请点击 \"设置\"-\"权限\"-打开所需权限。", "取消", "设置");
            } else {
                this.tipInfo = (TipInfo) ser;
            }

        } else {
            this.finish();
        }
    }

    protected void onResume() {
        super.onResume();
        if (this.isRequireCheck) {
            if (PermissionsUtil.hasPermission(this, this.permission)) {
                this.permissionsGranted();
            } else {
                this.requestPermissions(this.permission);
                this.isRequireCheck = false;
            }
        } else {
            this.isRequireCheck = true;
        }

    }

    private void requestPermissions(String[] permission) {
        ActivityCompat.requestPermissions(this, permission, 64);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 64 && PermissionsUtil.isGranted(grantResults) && PermissionsUtil.hasPermission(this, permissions)) {
            this.permissionsGranted();
        } else if (this.showTip) {
            this.showMissingPermissionDialog();
        } else {
            this.permissionsDenied();
        }

    }

    Builder builder;

    private void showMissingPermissionDialog() {
        if (builder == null) {
            builder = new Builder(this);
            builder.setTitle(TextUtils.isEmpty(this.tipInfo.title) ? "帮助" : this.tipInfo.title);
            builder.setMessage(TextUtils.isEmpty(this.tipInfo.content) ? "当前应用缺少必要权限。\n \n 请点击 \"设置\"-\"权限\"-打开所需权限。" : this.tipInfo.content);
            builder.setNegativeButton(TextUtils.isEmpty(this.tipInfo.cancel) ? "取消" : this.tipInfo.cancel, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    PermissionActivity.this.permissionsDenied();
                }
            });
            builder.setPositiveButton(TextUtils.isEmpty(this.tipInfo.ensure) ? "设置" : this.tipInfo.ensure, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    PermissionsUtil.gotoSetting(PermissionActivity.this);
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
    }


    private void permissionsDenied() {
        PermissionListener listener = PermissionsUtil.fetchListener(this.key);
        if (listener != null) {
            listener.permissionDenied(this.permission);
        }

        this.finish();
    }

    private void permissionsGranted() {
        PermissionListener listener = PermissionsUtil.fetchListener(this.key);
        if (listener != null) {
            listener.permissionGranted(this.permission);
        }

        this.finish();
    }

    protected void onDestroy() {
        PermissionsUtil.fetchListener(this.key);
        super.onDestroy();
    }
}
