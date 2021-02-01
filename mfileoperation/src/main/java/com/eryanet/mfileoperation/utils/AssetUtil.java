package com.eryanet.mfileoperation.utils;

import android.content.Context;

import com.eryanet.common.utils.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AssetUtil {

    /**
     * 将assets下的文件放到sd指定目录下
     *
     * @param assetsPath assets下的路径
     * @param sdCardPath sd卡的路径
     */
    public static void putAssetsToSDCard(Context mContext, String assetsPath, String sdCardPath) {
        Logger.debug("copyAssetsDirToSDCard() called with:  assetsDirName = [" + assetsPath + "], sdCardPath = [" + sdCardPath + "]");
        InputStream inputStream = null;
        FileOutputStream fos = null;
        try {
            String mString[] = mContext.getAssets().list(assetsPath);
            if (mString.length == 0) { // 说明assetsPath为空,或者assetsPath是一个文件
                inputStream = mContext.getAssets().open(assetsPath); // 读取流
                byte[] mByte = new byte[1024];
                int bt = 0;
                File file = new File(sdCardPath + File.separator
                        + assetsPath.substring(assetsPath.lastIndexOf('/')));
                if (!file.exists()) {
                    file.createNewFile(); // 创建文件
                } else {
                    return;//已经存在直接退出
                }
                fos = new FileOutputStream(file); // 写入流
                while ((bt = inputStream.read(mByte)) != -1) { // assets为文件,从文件中读取流
                    fos.write(mByte, 0, bt);// 写入流到文件中
                }
                fos.flush();// 刷新缓冲区
            } else { // 当mString长度大于0,说明其为文件夹
                sdCardPath = sdCardPath + File.separator + assetsPath;
                File file = new File(sdCardPath);
                if (!file.exists())
                    file.mkdirs(); // 在sd下创建目录
                for (String stringFile : mString) { // 进行递归
                    putAssetsToSDCard(mContext, assetsPath + File.separator
                            + stringFile, sdCardPath);
                }
            }
        } catch (Exception e) {
            Logger.error("putAssetsToSDCard Error: " + e.toString());
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(inputStream);
            IOUtil.closeQuietly(fos);
        }
    }

}
