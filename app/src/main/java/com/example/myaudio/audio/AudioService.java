package com.example.myaudio.audio;
import static android.media.CamcorderProfile.get;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.myaudio.R;
import com.example.myaudio.bean.AudioBean;
import com.example.myaudio.utils.Contants;

import java.util.Base64;
import java.util.List;

public class AudioService extends Service implements MediaPlayer.OnCompletionListener{
    private MediaPlayer mediaPlayer = null;
    private List<AudioBean> mList;    //播放列表
    private int playPosition = -1;    //记录当前播放位置
    private RemoteViews remoteView;    //通知对应自定义布局生成view对象
    private NotificationManager manager;
    private final int NOTIFY_ID_MUSIC = 100; //发送通知id

    private AudioReceiver receiver;
    /*
    * 接收通知发出的广播action
    * */
    private final String PRE_ACTION_LAST = "com.example.last";
    private final String PRE_ACTION_PLAY = "com.example.play";
    private final String PRE_ACTION_NEXT = "com.example.next";
    private final String PRE_ACTION_CLOSE = "com.example.close";
    private Notification notification;

    public AudioService() {}

//    创建通知对象和远程view对象
    @Override
    public void onCreate() {
        super.onCreate();
        initRegisterReceiver();
        intRemoteView();
        initNotification();
    }


    /* 创建广播接收者  */
    class AudioReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            notifyUIControl(action);

        }
    }

    private void notifyUIControl(String action) {
        switch (action){
            case PRE_ACTION_LAST:
                previousMusio();
                break;
            case PRE_ACTION_NEXT:
                nextMusic();
                break;
            case PRE_ACTION_PLAY:
                pauseOrContinueMusic();
                break;
            case PRE_ACTION_CLOSE:
                closeNotification();
                break;
        }
    }
    /* 关闭通知栏，停止音乐播放*/
    private void closeNotification() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mList.get(playPosition).setPlaying(false);
        }
        notifyActivityRefreshUI();
        manager.cancel(NOTIFY_ID_MUSIC);
    }


    /* 注册广播接收者，用于接收用户点击通知栏按钮发出信息*/
    private void initRegisterReceiver() {
        receiver = new AudioReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PRE_ACTION_LAST);
        filter.addAction(PRE_ACTION_PLAY);
        filter.addAction(PRE_ACTION_NEXT);
        filter.addAction(PRE_ACTION_CLOSE);
        registerReceiver(receiver,filter);

    }

    /* 设置通知栏显示效果以及图片的点击事件*/
    private void intRemoteView() {
        remoteView = new RemoteViews(getPackageName(), R.layout.notify_audio);

        PendingIntent lastPI = PendingIntent
                .getBroadcast(this, 1, new Intent(PRE_ACTION_LAST),PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.ny_iv_last,lastPI);

        PendingIntent nextPI = PendingIntent
                .getBroadcast(this, 1, new Intent(PRE_ACTION_NEXT),PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.ny_iv_next,nextPI);

        PendingIntent playPI = PendingIntent
                .getBroadcast(this, 1, new Intent(PRE_ACTION_PLAY),PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.ny_iv_play,playPI);

        PendingIntent closePI = PendingIntent
                .getBroadcast(this, 1, new Intent(PRE_ACTION_CLOSE),PendingIntent.FLAG_UPDATE_CURRENT);
        remoteView.setOnClickPendingIntent(R.id.ny_iv_close,closePI);


    }

    /* 初始化通知栏*/
    private void initNotification() {
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 创建通知渠道
            NotificationChannel channel = new NotificationChannel("your_channel_id", "Your Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            // 设置其他通知渠道属性，如声音、震动等
            manager.createNotificationChannel(channel);
        }

        // 使用NotificationCompat.Builder代替Notification.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "your_channel_id");

        builder.setSmallIcon(R.mipmap.icon_app_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_app_logo))
                .setContent(remoteView)
                .setAutoCancel(false)
                .setOngoing(true)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notification = builder.build();
    }


    /* 更新通知栏信息函数*/
    @SuppressLint("NotificationPermission")
    private void updateNotification(int position){
//        根据多媒体播放状态显示播放图片
        if (mediaPlayer.isPlaying()) {
            remoteView.setImageViewResource(R.id.ny_iv_play,R.mipmap.red_pause);
        }else{
            remoteView.setImageViewResource(R.id.ny_iv_play,R.mipmap.red_play);
        }
        remoteView.setTextViewText(R.id.ny_tv_title,mList.get(position).getTitle());
        remoteView.setTextViewText(R.id.ny_tv_duration,mList.get(position).getDuration());
//        发送通知
        manager.notify(NOTIFY_ID_MUSIC,notification);
    }
    public interface OnPlayChangeListener{
        public void playChange(int changPos);
    }
    private OnPlayChangeListener onPlayChangeListener;

    public void setOnPlayChangeListener(OnPlayChangeListener onPlayChangeListener) {
        this.onPlayChangeListener = onPlayChangeListener;
    }


    /*
    * 多媒体服务发生变化，提示Activity刷新UI
    * */
    public void notifyActivityRefreshUI(){
        if (onPlayChangeListener!=null) {
            onPlayChangeListener.playChange(playPosition);
        }

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        nextMusic();  //播放完成，直接播放下一个
    }

    public class AudioBinder extends Binder{
        public AudioService getService(){
            return AudioService.this;
        }


    }

    @Override
    public IBinder onBind(Intent intent) {
        return new AudioBinder();
    }
    /*
    * 播放按钮有2种可能性
    * 1.不是当前播放位置被点击了，进行切歌操作
    * 2.当前播放位置被点击了，暂停或继续
    **/
    public void CutMusioOrPause(int position){
        int playPosition = this.playPosition;
        if (position!=playPosition) {
//            判断是否正在播放，如果切歌，把上一曲改为false
            if (playPosition!=-1) {
                mList.get(playPosition).setPlaying(false);
            }
            play(position);
            return;
        }
//        执行暂停或继续操作
        pauseOrContinueMusic();
    }


    /*
    * 播放音乐，点击切歌
    * */
    public void play(int position){
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
//            设置监听事件
            mediaPlayer.setOnCompletionListener(this);
        }
//        播放时，获取当前歌曲列表，判断是否有歌曲
        mList = Contants.getsAudioList();
        if (mList.size()<=0) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
//        切歌之前先重置，释放原来资源

        try {
            mediaPlayer.reset();
            playPosition = position;
//        设置播放音频资源路径
            mediaPlayer.setDataSource(mList.get(position).getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
//            设置当前位置正在播放
            mList.get(position).setPlaying(true);
            notifyActivityRefreshUI();
            setFlagControlThread(true);
            updateProgress();
            updateNotification(position);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /*
    * 暂停/ 继续播放音乐
    * */
    public void pauseOrContinueMusic(){
        int playPosition = this.playPosition;
        AudioBean audioBean = mList.get(playPosition);
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            audioBean.setPlaying(false);
        }else{
            mediaPlayer.start();
            audioBean.setPlaying(true);
        }
        notifyActivityRefreshUI();
        updateNotification(playPosition);
    }


    //  播放下一曲
    private void nextMusic() {
        mList.get(playPosition).setPlaying(false);
        if (playPosition == mList.size()-1) {
            playPosition = 0;
        }else{
            playPosition++;
        }
        mList.get(playPosition).setPlaying(true);
        play(playPosition);
    }



    //    播放上一曲
    private void previousMusio() {
        mList.get(playPosition).setPlaying(false);
        if (playPosition == 0) {
            playPosition = mList.size()-1;
        }else{
            playPosition--;
        }
        mList.get(playPosition).setPlaying(true);
        play(playPosition);
    }

    /*
     *  停止音乐
     * */
    public void closeMusic(){
        if (mediaPlayer!=null) {
            setFlagControlThread(false);
            closeNotification();
            mediaPlayer.stop();
            playPosition = -1;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        closeMusic();
    }

    /*
    * 更新播放进度方法
    **/
    private boolean flag = true;
    private final int PROGRESS_ID =1;
    private final int INTERMINATE_TIME = 1000;
    public void setFlagControlThread(boolean flag){
        this.flag = flag;
    }
    public void updateProgress(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(flag){
//                    获取总时长
                    long total = mList.get(playPosition).getDurationLong();
//                    获取当前播放位置
                    int currentPosition = mediaPlayer.getCurrentPosition();
//                    计算播放进度
                    int progress = (int)(currentPosition*100/total);
                    mList.get(playPosition).setCurrentProgress(progress);
                    handler.sendEmptyMessageDelayed(PROGRESS_ID,INTERMINATE_TIME);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                       e.printStackTrace();
                    }


                }

            }
        }).start();
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == PROGRESS_ID) {
                notifyActivityRefreshUI();
            }
            return false;
        }
    });

}