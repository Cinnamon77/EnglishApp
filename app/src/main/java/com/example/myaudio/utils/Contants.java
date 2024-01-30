package com.example.myaudio.utils;

import com.example.myaudio.bean.AudioBean;

import java.util.List;

public class Contants {

    /*存放文件的目录*/
    public static String PATH_APP_DIR;
    public static String PATH_FETCH_DIR_RECORD;
    private static List<AudioBean> sAudioList;

    public static void setsAudioList(List<AudioBean>audioList){
        if (audioList!=null) {
            Contants.sAudioList = audioList;
        }
    }

    public static List<AudioBean> getsAudioList() {
        return sAudioList;
    }
}
