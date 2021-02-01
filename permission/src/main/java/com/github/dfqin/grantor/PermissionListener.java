//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.github.dfqin.grantor;


import androidx.annotation.NonNull;

public interface PermissionListener {
    void permissionGranted(@NonNull String[] var1);

    void permissionDenied(@NonNull String[] var1);
}
