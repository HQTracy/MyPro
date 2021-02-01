package com.eryanet.mfileoperation.manager;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;

import com.eryanet.common.utils.Logger;
import com.eryanet.mfileoperation.utils.AssetUtil;
import com.eryanet.mfileoperation.utils.FileUtil;
import com.eryanet.mfileoperation.utils.ZipUtil;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.List;

public class UpgradeManager1 {

    private Context mContext;
    private Handler mHandler;

    private static final String AUTH_ERYA = "erya";

    private static final String NATIVE_VERSION = "version";
    private static final String NATIVE_ASSVERSION = "assversion";
    private static final String mFolderName = "eryalink_upgrade";
    private static final String mFileSubPath = File.separator + "AAAEryanet";
    private static final String mBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + mFileSubPath;
    private static final String ABS_LINKCORE_FOLDER = mBasePath + File.separator + mFolderName;
    private static final String ABS_LINKCORE_FOLDER_lib = ABS_LINKCORE_FOLDER + File.separator + "lib";
    private static final String ABS_LINKCORE_FOLDER_bin = ABS_LINKCORE_FOLDER + File.separator + "bin";
    private static final String ABS_NATIVE_VERSION = ABS_LINKCORE_FOLDER + File.separator + NATIVE_VERSION;
    private static final String ABS_NATIVE_ASSVERSION = ABS_LINKCORE_FOLDER + File.separator + NATIVE_ASSVERSION;
    private static final String ABS_COMPLETE_PATH = ABS_LINKCORE_FOLDER + File.separator + "completed";

    private int retryTimes = 0;


    public UpgradeManager1(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
    }


    //异常重试拷贝升级
    private void retryCheck() throws Exception {
        if (retryTimes == 3) {
            return;
        }
        retryTimes++;
        //清空数据
        FileUtil.deleteFile(ABS_LINKCORE_FOLDER);
        SystemClock.sleep(500);
        checkUpgradeLinkcore();
    }

    /**
     * 解压后的文件和文件夹是否存在
     *
     * @return true - 存在
     * false - 不存在
     */
    private boolean isFileOrFolderExist() {
        boolean isvFileExist = FileUtil.isFileExist(ABS_NATIVE_VERSION);
        Logger.debug("ABS_LINKCORE_FOLDER: " + ABS_NATIVE_VERSION + " isExist > " + isvFileExist);
        boolean isassvFileExist = FileUtil.isFileExist(ABS_NATIVE_ASSVERSION);
        Logger.debug("ABS_NATIVE_ASSVERSION: " + ABS_NATIVE_ASSVERSION + " isExist > " + isassvFileExist);
        boolean isFileFull = false;
        Logger.debug("ABS_LINKCORE_FOLDER_bin: " + ABS_LINKCORE_FOLDER_bin);
        Logger.debug("bin size: " + FileUtil.getFileNameList(ABS_LINKCORE_FOLDER_bin)
        + "lib size: " + FileUtil.getFileNameList(ABS_LINKCORE_FOLDER_lib));
        if (FileUtil.getFileNameList(ABS_LINKCORE_FOLDER_bin).size() == 6 &&
                FileUtil.getFileNameList(ABS_LINKCORE_FOLDER_lib).size() == 10) {
            isFileFull = true;
        }
        Logger.debug("isFileFull: " + isFileFull);
        return isvFileExist && isassvFileExist && isFileFull;
    }

    //检测升级
    public boolean checkUpgradeLinkcore() throws Exception {
        File destPath = new File(mBasePath);
        boolean isDirsExist;
        if (destPath.exists()) {
            isDirsExist = true;
        } else {
            isDirsExist = FileUtil.makeDirs(new File(mBasePath));
        }
        Logger.info("makeDirs isDirsExist: " + isDirsExist);
        if (isDirsExist) {
            if (FileUtil.isFolderExist(ABS_LINKCORE_FOLDER)) {
                Logger.info("isFolderExist with Linkcore");
                if (isFileOrFolderExist() && !isNeedUpdate()) {
                    Logger.info("Currently the latest version");
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(101);
                    }
                } else {
                    Logger.info("update Linkcore");
                    retryCheck();
                }
            } else {
                AssetUtil.putAssetsToSDCard(mContext, mFolderName, mBasePath);
                Logger.info("putAssetsToSDCard");
                if (isFileOrFolderExist()) {
                    Logger.info("isFileOrFolderExist true");
                    FileUtil.createFile(ABS_COMPLETE_PATH);
                    notifyLinkcoreUpgrade();
                } else {
                    Logger.info("isFileOrFolderExist true");
                    retryCheck();
                }
            }
        }
        return false;
    }

    /**
     * 检查版本是否存在更新
     *
     * @return true - 存在更新
     * false - 已最新版本
     */
    private boolean isNeedUpdate() {
        List<String> readStr = null;
        try {
            readStr = FileUtil.readFileToList(ABS_NATIVE_VERSION);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String nativeVersion = null;
        if (readStr != null && !readStr.isEmpty()) {
            nativeVersion = readStr.get(0);
            Logger.debug("app version is: " + getTheLatestVersion() + " || native version is: " + nativeVersion);
        }
        return !getTheLatestVersion().equalsIgnoreCase(nativeVersion);
    }


    private byte[] string2Bytes(String str) {
        byte[] srtbyte = null;
        try {
            srtbyte = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return srtbyte;
    }

    //通知linkcore进行升级拷贝
    private boolean notifyLinkcoreUpgrade() {
        boolean isWriteOk;
        try {
            Logger.debug("10477 start");
            Socket socket = new Socket("127.0.0.1", 10477);
            Logger.debug("10477 end");

            OutputStream os = socket.getOutputStream();
            BufferedOutputStream bufos = new BufferedOutputStream(os);
            bufos.write(string2Bytes(AUTH_ERYA), 0, string2Bytes(AUTH_ERYA).length);
            bufos.flush();

            bufos.write(string2Bytes(ABS_LINKCORE_FOLDER), 0, string2Bytes(ABS_LINKCORE_FOLDER).length);
            bufos.flush();
            Logger.debug("send2path：" + ABS_LINKCORE_FOLDER);

            bufos.close();
            os.close();
            socket.close();
            isWriteOk = true;
        } catch (IOException e) {
            isWriteOk = false;
            Logger.error("path：" + ABS_LINKCORE_FOLDER + "||" + e.getMessage());
        }
        Logger.debug("Linkcore File notify result " + isWriteOk);

        if (isWriteOk && mHandler != null) {
            mHandler.sendEmptyMessageDelayed(101, 6 * 1000);
        }

        return isWriteOk;
    }


    /**
     * 获取本地linkcore 版本
     */
    public String getTheLatestVersion() {
        String value = "";
        String line = null;
        BufferedReader buffer = null;
        try {
            buffer = new BufferedReader(new InputStreamReader(mContext.getAssets().open(ABS_NATIVE_VERSION), "utf-8"));
            while ((line = buffer.readLine()) != null) {
                value = line;
            }
        } catch (Exception e) {
            Logger.error("getUpgradeConfData Error: " + e.toString());
            e.printStackTrace();
        } finally {
            try {
                buffer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return value;
    }

}
