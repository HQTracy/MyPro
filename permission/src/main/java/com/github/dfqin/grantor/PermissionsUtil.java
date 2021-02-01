//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.github.dfqin.grantor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.PermissionChecker;
import android.util.Log;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PermissionsUtil {
    public static final String TAG = "PermissionGrantor";
    private static Map<String, PermissionListener> listenerMap = new ConcurrentHashMap<>();

    public PermissionsUtil() {
    }

    public static void requestPermission(Activity activity, PermissionListener listener, String... permission) {
        requestPermission(activity, listener, permission, true, (PermissionsUtil.TipInfo)null);
    }

    public static void requestPermission(@NonNull Activity activity, @NonNull PermissionListener listener, @NonNull String[] permission, boolean showTip, @Nullable PermissionsUtil.TipInfo tip) {
        if (listener == null) {
            Log.e("PermissionGrantor", "listener is null");
        } else if (VERSION.SDK_INT < 23) {
            if (hasPermission(activity, permission)) {
                listener.permissionGranted(permission);
            } else {
                listener.permissionDenied(permission);
            }

            Log.e("PermissionGrantor", "API level : " + VERSION.SDK_INT + "不需要申请动态权限!");
        } else {
            String key = String.valueOf(System.currentTimeMillis());
            listenerMap.put(key, listener);
            Intent intent = new Intent(activity, PermissionActivity.class);
            intent.putExtra("permission", permission);
            intent.putExtra("key", key);
            intent.putExtra("showTip", showTip);
            intent.putExtra("tip", tip);
            activity.startActivity(intent);
        }
    }

    public static boolean hasPermission(@NonNull Context context, @NonNull String... permissions) {
        String[] var2 = permissions;
        int var3 = permissions.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String per = var2[var4];
            int result = PermissionChecker.checkSelfPermission(context, per);
            if (result != 0) {
                return false;
            }
        }

        return true;
    }

    public static boolean isGranted(@NonNull int... grantResult) {
        int[] var1 = grantResult;
        int var2 = grantResult.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            int result = var1[var3];
            if (result != 0) {
                return false;
            }
        }

        return true;
    }

    public static void gotoSetting(@NonNull Context context) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }

    static PermissionListener fetchListener(String key) {
        return (PermissionListener)listenerMap.remove(key);
    }

    public static class TipInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        String title;
        String content;
        String cancel;
        String ensure;

        public TipInfo(@Nullable String title, @Nullable String content, @Nullable String cancel, @Nullable String ensure) {
            this.title = title;
            this.content = content;
            this.cancel = cancel;
            this.ensure = ensure;
        }
    }
}
