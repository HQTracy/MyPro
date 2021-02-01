package com.eryanet.mfileoperation.manager;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;

import com.eryanet.common.utils.Logger;
import com.eryanet.mfileoperation.utils.AssetUtil;
import com.eryanet.mfileoperation.utils.FileUtil;
import com.eryanet.mfileoperation.utils.MD5Util;
import com.eryanet.mfileoperation.utils.ZipUtil;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.List;

public class UpgradeManager {

    private Context mContext;
    private Handler mHandler;

    private static final String AUTH_ERYA = "erya";
    private static final String KEY_MD5 = "md5";
    private static final String KEY_VERSION = "version";

    private static final String UPGRADE_CONFNAME = "Linkcore/upgrade.conf";
    private static final String LINKCORE_ZIPNAME = "eryalink_upgrade.zip";
    private static final String mFolderName = "Linkcore";
    private static final String mFileSubPath = File.separator + "AAAEryanet";
    private static final String mBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + mFileSubPath;
    private static final String mFileAbsolutePath = mBasePath + File.separator + mFolderName;
    private static final String ABS_LINKCORE_FOLDER = mFileAbsolutePath + File.separator + "eryalink_upgrade";
    private static final String ABS_UPGRADE_CONF = mBasePath + File.separator + UPGRADE_CONFNAME;
    private static final String ABS_ZIP_PATH = mFileAbsolutePath + File.separator + LINKCORE_ZIPNAME;
    private static final String ABS_COMPLETE_PATH = mFileAbsolutePath + File.separator + "eryalink_upgrade" + File.separator + "completed";

    private int retryTimes = 0;


    public UpgradeManager(Context context, Handler handler) {
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
        FileUtil.deleteFile(mFileAbsolutePath);
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
        boolean isFileExist = FileUtil.isFolderExist(ABS_LINKCORE_FOLDER);
        Logger.debug("ABS_LINKCORE_FOLDER: " + ABS_LINKCORE_FOLDER + " isExist > " + isFileExist);
        boolean isFolderExist = FileUtil.isFileExist(ABS_UPGRADE_CONF);
        Logger.debug("ABS_UPGRADE_CONF: " + ABS_UPGRADE_CONF + " isExist > " + isFolderExist);
        return isFileExist && isFolderExist;
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
            if (FileUtil.isFolderExist(mFileAbsolutePath)) {
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
                if (checkZipMd5()) {
                    Logger.info("checkZipMd5 ok");
                    ZipUtil.UnZipFolder(ABS_ZIP_PATH, mFileAbsolutePath);
                    boolean unzipComplete = FileUtil.createFile(ABS_COMPLETE_PATH);
                    Logger.debug("UnzipFolder result: " + unzipComplete);
                    FileUtil.deleteFile(ABS_ZIP_PATH);
                    notifyLinkcoreUpgrade();
                } else {
                    Logger.error("checkZipMd5 fail");
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
            readStr = FileUtil.readFileToList(ABS_UPGRADE_CONF);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String nativeVersion = null;
        for (int i = 0; i < readStr.size(); i++) {
            if (readStr.get(i).contains("version:")) {
                nativeVersion = readStr.get(i).split(":")[1];
                break;
            }
        }
        Logger.debug("app version is: " + getNativeUpgradeConfData(KEY_VERSION) + " || native version is: " + nativeVersion);
        return !getNativeUpgradeConfData(KEY_VERSION).equalsIgnoreCase(nativeVersion);
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

            bufos.write(string2Bytes(mFileAbsolutePath), 0, string2Bytes(mFileAbsolutePath).length);
            bufos.flush();
            Logger.debug("send2path：" + mFileAbsolutePath);

            bufos.close();
            os.close();
            socket.close();
            isWriteOk = true;
        } catch (IOException e) {
            isWriteOk = false;
            Logger.error("path：" + mFileAbsolutePath + "||" + e.getMessage());
        }
        Logger.debug("Linkcore File notify result " + isWriteOk);

        if (isWriteOk && mHandler != null) {
            mHandler.sendEmptyMessageDelayed(101, 6 * 1000);
        }

        return isWriteOk;
    }


    /**
     * 初始化目录
     */
//    public static void init(Context context) {
    // 优先保存到SD卡中
//        if (Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED)) {
//            mBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + mFileSubPath;
//        } else {// 如果SD卡不存在，就保存到本应用的目录下
//            mBasePath = context.getFilesDir().getAbsolutePath() + mFileSubPath;
//        }

//        Logger.debug("mBasePath: " + mBasePath);
//        File fileDir = new File(mBasePath);
//        if (!fileDir.exists()) {
//            Logger.info("mkdirs destPath");
//            fileDir.mkdirs();
//        }
//    }


    /**
     * 校验zip文件 md5
     *
     * @return true - 完整
     * false - 不完整
     */
    private boolean checkZipMd5() {
        String md5sum = "";
        try {
            md5sum = MD5Util.md5(new File(ABS_ZIP_PATH));
            Logger.debug("app md5: " + getNativeUpgradeConfData(KEY_MD5) + " || native md5sum: " + md5sum);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (md5sum.equalsIgnoreCase(getNativeUpgradeConfData(KEY_MD5))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取配置数据
     *
     * @param keyName md5 or version
     * @return value
     */
    public String getNativeUpgradeConfData(String keyName) {
        String value = "";
        String line = null;
        BufferedReader buffer = null;
        try {
            buffer = new BufferedReader(new InputStreamReader(mContext.getAssets().open(UPGRADE_CONFNAME), "utf-8"));
            while ((line = buffer.readLine()) != null) {
                if (keyName.equalsIgnoreCase(line.split(":")[0])) {
                    value = line.split(":")[1];
                    break;
                }
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
