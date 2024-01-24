package com.example.myaudio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.myaudio.audio.AudioListActivity;
import com.example.myaudio.databinding.ActivityMainBinding;
import com.example.myaudio.utils.Contants;
import com.example.myaudio.utils.IFileInter;
import com.example.myaudio.utils.PermissionUtils;
import com.example.myaudio.utils.SDCardUtils;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    int time =3;//倒计时时间
    String []permissions = {Manifest.permission.RECORD_AUDIO,
    Manifest.permission.READ_EXTERNAL_STORAGE};

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                time--;
                if (time == 0) {
                    startActivity(new Intent(MainActivity.this, AudioListActivity.class));
                    finish();
                }else{
                    binding.mainTv.setText(time+"");
                    handler.sendEmptyMessageDelayed(1,1000);
                }

            }
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.mainTv.setText(time+"");
        PermissionUtils.getInstance().onRequestPermission(this,permissions,listener);
    }
    PermissionUtils.OnPermissionCallbackListener listener = new PermissionUtils.OnPermissionCallbackListener() {
        @Override
        public void onGrandted() {
//            判断是否有应用文件夹，如果没有创建应用文件夹
              createAppDir();
//            倒计时进入播放录音页面
            handler.sendEmptyMessageDelayed(1,1000);
        }

        @Override
        public void onDenied(List<String> deniedPermissions) {
            PermissionUtils.getInstance()
                    .showDialogTipUserGotoAppSetting(MainActivity.this);
        }
    };

    private void createAppDir() {
       File recoderDir = SDCardUtils.getInstance().createAppFetchDir(IFileInter.FETCH_DIR_AUDIO);
        Contants.PATH_FETCH_DIR_RECORD = recoderDir.getAbsolutePath();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.getInstance().onRequestPermissionResult(this,requestCode,permissions,grantResults);
    }
}