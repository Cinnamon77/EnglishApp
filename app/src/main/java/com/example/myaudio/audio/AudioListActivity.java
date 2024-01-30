package com.example.myaudio.audio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.PopupMenu;
import android.view.MenuItem;

import android.widget.PopupMenu;

import com.example.myaudio.R;
import com.example.myaudio.StartSystemPageUtils;
import com.example.myaudio.bean.AudioBean;
import com.example.myaudio.databinding.ActivityAudioListBinding;
import com.example.myaudio.recorder.RecorderActivity;
import com.example.myaudio.utils.AudioInfoDialog;
import com.example.myaudio.utils.AudioInfoUtils;
import com.example.myaudio.utils.Contants;
import com.example.myaudio.utils.DialogUtils;
import com.example.myaudio.utils.RenameDialog;


import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AudioListActivity extends AppCompatActivity {
    private ActivityAudioListBinding binding;
    private List<AudioBean>mDatas;
    private AudioListAdapter adapter;
    private AudioService audioService;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AudioService.AudioBinder audioBinder = (AudioService.AudioBinder) service;
            audioService = audioBinder.getService();
            audioService.setOnPlayChangeListener(playChangeListener);


        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    AudioService.OnPlayChangeListener playChangeListener = new AudioService.OnPlayChangeListener() {
        @Override
        public void playChange(int changPos) {
            adapter.notifyDataSetChanged();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAudioListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        绑定服务
        Intent intent = new Intent(this, AudioService.class);
        bindService(intent,connection,BIND_AUTO_CREATE);

//        为ListView设置数据源和适配器
        mDatas = new ArrayList<>();
        adapter = new AudioListAdapter(this,mDatas);
        binding.audioLv.setAdapter(adapter);
//        将音频对象集合保存到全局变量当中
        Contants.setsAudioList(mDatas);
//        加载数据
        loadDatas();
//        设置监听时间
        setEvents();

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        判断点击了返回按钮
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            StartSystemPageUtils.goToHomePage(this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /*
    * 设置监听
    * */

    private void setEvents() {
        adapter.setOnItemPlayClickListener(playClickListener);
        binding.audioLv.setOnItemLongClickListener(longClickListener);
        binding.audioIb.setOnClickListener(onClickListener);
    }
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //1.关闭音乐
                audioService.closeMusic();
                //2.跳转到录音页面
                startActivity(new Intent(AudioListActivity.this, RecorderActivity.class));
                //3.销毁当前Activity
                finish();

            }
        };

    //        点击每一个播放按钮会回调方法
    AudioListAdapter.OnItemPlayClickListener playClickListener = new AudioListAdapter.OnItemPlayClickListener() {
        @Override
        public void onItemPlayClick(AudioListAdapter adapter, View converView, View playView, int position) {
            for (int i = 0; i < mDatas.size(); i++) {
                if (i==position) {
                    continue;
                }
                AudioBean bean = mDatas.get(i);
                bean.setPlaying(false);

            }
//            获取当前播放状态
            boolean playing = mDatas.get(position).isPlaying();
            mDatas.get(position).setPlaying(!playing);
            adapter.notifyDataSetChanged();
            audioService.CutMusioOrPause(position);
        }
    };

    AdapterView.OnItemLongClickListener longClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            showPopMenu(view,position);
            audioService.closeMusic();
            return false;
        }


    };
    /*长按每一项item能够弹出menu窗口*/
    private void showPopMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(this, view, Gravity.RIGHT);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.audio_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_info) {
                    showFileInfoDialog(position);
                    // 处理 menu_info
                } else if (item.getItemId() == R.id.menu_del) {
                    deleteFileByPos(position);
                    // 处理 menu_del
                } else if (item.getItemId() == R.id.menu_rename) {
                    showRenameDialog(position);
                    // 处理 menu_rename
                } else {
                    throw new IllegalStateException("Unexpected value: " + item.getItemId());
                }
                return false;
            }

        });
        popupMenu.show();

    }

    /*
    * 显示文件详情对话框
    * */
    private void showFileInfoDialog(int position) {
        AudioBean bean = mDatas.get(position);
        AudioInfoDialog dialog = new AudioInfoDialog(this);
        dialog.show();
        dialog.setDialogWidth();
        dialog.setFileInfo(bean);
        dialog.setCanceledOnTouchOutside(false);
    }

    /*
    * 显示重命名对话框
    * */
    private void showRenameDialog(int position) {
        AudioBean bean = mDatas.get(position);
        String title = bean.getTitle();
        RenameDialog dialog = new RenameDialog(this);
        dialog.show();
        dialog.setDialogWidth();
        dialog.setTipText(title);
        dialog.setOnEnsureListener(new RenameDialog.OnEnsureListener() {
            @Override
            public void onEnsure(String msg) {
                renameByPosition(msg,position);


            }
        });


    }
    /*
    * 对指定位置的文件重新命名
    * */
    private void renameByPosition(String msg, int position) {
        AudioBean audioBean = mDatas.get(position);
        if (audioBean.getTitle().equals(msg)) {
            return;
        }
        String path = audioBean.getPath();
        String fileSuffix = audioBean.getFileSuffix();
        File srcFile = new File(path);  //原来的文件
//        获取修改路径
        String destPath = srcFile.getParentFile()+File.separator+msg+fileSuffix;
        File destFile = new File(destPath);
//        进行重命名物理操作
        srcFile.renameTo(destFile);
//        从内存当中进行修改
        audioBean.setTitle(msg);
        audioBean.setPath(destPath);
        adapter.notifyDataSetChanged();

    }

    /*删除指定位置文件*/
    private void deleteFileByPos(int position) {
        AudioBean bean = mDatas.get(position);
        String title = bean.getTitle();
        String path =bean.getPath();
        DialogUtils.showNormalDialog(this, "提示信息", "删除文件后将无法恢复，确定删除指定文件",
                "确定", new DialogUtils.OnLeftClickListener() {
                    @Override
                    public void onLeftClick() {
                        File file = new File(path);
                        file.getAbsoluteFile().delete();
                        mDatas.remove(bean);
                        adapter.notifyDataSetChanged();

                    }
                },"取消",null);

    }

//    点击每一个播放按钮会回调方法


    private void loadDatas()  {
//        1.获取指定路径下的音频文件
         File fetchFile= new File(Contants.PATH_FETCH_DIR_RECORD);
         File[] listFiles = fetchFile.listFiles(new FilenameFilter() {
             @Override
             public boolean accept(File dir, String name) {
                 if (new File(dir,name).isDirectory()) {
                 }
                 if (name.endsWith("mp3") || name.endsWith("m4a")) {
                     return true;
                 }

                 return false;
             }
         });

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        AudioInfoUtils audioInfoUtils = AudioInfoUtils.getInstance();
//         2.遍历数组当中的文件，依次得到文件信息
        for (int i = 0; i < listFiles.length; i++) {
            File audioFile = listFiles[i];
            String fname = audioFile.getName();
            String title = fname.substring(0,fname.lastIndexOf("."));
            String suffix = fname.substring(fname.lastIndexOf("."));
//            最后文件修改时间
            long flastMod = audioFile.lastModified();
            String time = sdf.format(flastMod);
//            获取文件字节数
            long flength = audioFile.length();
//            获取文件的路径
            String audioPath = audioFile.getAbsolutePath();
            long duration= audioInfoUtils.getAudioFileDuration(audioPath);
            String formatDuration = audioInfoUtils.getAudioFileFormatDuration(duration);
            AudioBean audioBean = new AudioBean(i + "", title, time, formatDuration, audioPath,
                    duration, flastMod, suffix, flength);
            mDatas.add(audioBean);


        }
        try {
            audioInfoUtils.releseRetriever();
        } catch (IOException e) {
            // 处理 IOException，例如记录日志或其他适当的操作
            e.printStackTrace();
        }
        //释放多媒体资料的资料对象

//        将集合中的元素重新排序
        Collections.sort(mDatas, new Comparator<AudioBean>() {
            @Override
            public int compare(AudioBean o1, AudioBean o2) {
                if (o1.getLastModified()<o2.getLastModified()) {
                    return 1;
                }else if(o1.getLastModified()==o2.getLastModified()){
                    return 0;
                }

                return -1;
            }
        });
        adapter.notifyDataSetChanged();
    }
}