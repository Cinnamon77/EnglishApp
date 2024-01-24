package com.example.myaudio.utils;

import android.os.Environment;

import java.io.File;


public class SDCardUtils {
    private SDCardUtils(){

    }
    private static SDCardUtils sdCardUtils;
    public static SDCardUtils getInstance(){
        if (sdCardUtils == null) {
            synchronized (SDCardUtils.class){
                if (sdCardUtils==null) {
                    sdCardUtils = new SDCardUtils();
                }
            }
        }
        return sdCardUtils;

    }
//    判断当前手机是否有SD卡
    public boolean isHaveSDCard(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

//    创建项目的公共目录
    public File createAppPublicDir() {
        if (isHaveSDCard()) {
            File sdDir = Environment.getExternalStorageDirectory();
            File appDir = new File(sdDir,IFileInter.APP_DIR);
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            Contants.PATH_APP_DIR = appDir.getAbsolutePath();
            return appDir;

        }
        return null;
    }
    /*创建项目分支目录*/
    public File createAppFetchDir(String dir){
        File publicDir = createAppPublicDir();
        if (publicDir != null) {
            File fetchDir = new File(publicDir,dir);
            if (!fetchDir.exists()) {
                fetchDir.mkdirs();
            }
            return fetchDir;
        }
        return null;
    }

}
