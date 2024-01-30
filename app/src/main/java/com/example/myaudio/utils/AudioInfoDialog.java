package com.example.myaudio.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.myaudio.bean.AudioBean;
import com.example.myaudio.databinding.DialogAudioinfoBinding;

import java.text.DecimalFormat;

public class AudioInfoDialog extends Dialog {
    private DialogAudioinfoBinding binding;

    public AudioInfoDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogAudioinfoBinding.inflate(getLayoutInflater());
        CardView root = binding.getRoot();
        setContentView(root);
        binding.tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();

            }
        });


    }

    /*
     * 设置对话框宽度和屏幕宽度一致
     * */
    public void setDialogWidth(){
//        获取当前屏幕窗口对象
        Window window = getWindow();
//        获取窗口的信息参数
        WindowManager.LayoutParams wlp = window.getAttributes();
//        获取屏幕的宽度
        Display display = window.getWindowManager().getDefaultDisplay();
        wlp.width = display.getWidth()-30;
        wlp.gravity = Gravity.BOTTOM;
//        设置窗口背景透明
        window.setBackgroundDrawableResource(android.R.color.transparent);
//        设置窗口参数
        window.setAttributes(wlp);

    }


    public void setFileInfo(AudioBean bean){
        binding.tvTime.setText(bean.getTime());
        binding.tvTitle.setText(bean.getTitle());
        binding.tvPath.setText(bean.getPath());
        String size = calFileSIze(bean.getFileLength());
        binding.tvSize.setText(size);

    }

    private String calFileSIze(long fileLength) {
        DecimalFormat format = new DecimalFormat("#.00");
        if (fileLength>=1024*1024) {
            return format.format(fileLength*1.0/(1024*1024))+"MB";

        }
        if (fileLength>=1024) {
            return format.format(fileLength*1.0/1024)+"KB";
        }
        if (fileLength<1024) {
            return fileLength+"B";
        }

        return "0KB";

    }
}
