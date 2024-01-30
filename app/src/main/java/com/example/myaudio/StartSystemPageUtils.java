package com.example.myaudio;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

//跳转系统相关页面
public class StartSystemPageUtils {

//    跳转到系统中当前应用的设置页面

    public static void goToAppSetting(Activity context){
        Intent intent =new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri=Uri.fromParts("package",context.getPackageName(),null);
        intent.setData(uri);
        context.startActivity(intent);
    }

    /*
    * 跳转到手机Home页面
    * */
    public static void goToHomePage(Activity context){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(intent);
    }
}
