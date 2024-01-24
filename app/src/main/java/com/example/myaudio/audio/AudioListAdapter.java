package com.example.myaudio.audio;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.myaudio.R;
import com.example.myaudio.bean.AudioBean;
import com.example.myaudio.databinding.ItemAudioBinding;

import java.util.List;

public class AudioListAdapter extends BaseAdapter {
    private Context context;
    private List<AudioBean>mDatas;
//    点击每一个itemView当中的playIv能够回调的接口
    public interface OnItemPlayClickListener{
        void onItemPlayClick(AudioListAdapter adapter,View converView,View playView,int position);
    }
    private OnItemPlayClickListener onItemPlayClickListener;

    public void setOnItemPlayClickListener(OnItemPlayClickListener onItemPlayClickListener) {
        this.onItemPlayClickListener = onItemPlayClickListener;
    }

    public AudioListAdapter(Context context, List<AudioBean> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        ViewHolder holder = null;
        if (converView==null) {
            converView = LayoutInflater.from(context).inflate(R.layout.item_audio,parent,false);
            holder = new ViewHolder(converView);
            converView.setTag(holder);
        }else{
            holder = (ViewHolder) converView.getTag();
        }
//        获取指定位置的数据对于控件进行设置
        AudioBean audioBean= mDatas.get(position);
        holder.ab.tvTime.setText(audioBean.getTime());
        holder.ab.tvDuration.setText(audioBean.getDuration());
        holder.ab.tvTitle.setText(audioBean.getTitle());
        if (audioBean.isPlaying()) {
            holder.ab.lyControll.setVisibility(View.VISIBLE);
            holder.ab.pd.setMax(100);
            holder.ab.pd.setProgress(audioBean.getCurrentProgress());
            holder.ab.ivPlay.setImageResource(R.mipmap.red_pause);
        }else{
            holder.ab.ivPlay.setImageResource(R.mipmap.red_play);
            holder.ab.lyControll.setVisibility(View.GONE);
        }
        View itemView = converView;
//        点击播放图标可以播放或者暂停录音内容
        holder.ab.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemPlayClickListener!=null) {
                    onItemPlayClickListener.onItemPlayClick(AudioListAdapter.this,itemView,v,position);
                }

            }
        });

        return converView;
    }

    class ViewHolder{
        ItemAudioBinding ab;
        public ViewHolder(View v){
            ab=ItemAudioBinding.bind(v);

        }
    }
}
