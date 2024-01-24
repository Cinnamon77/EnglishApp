package com.example.myaudio.bean;

public class AudioBean {
    private String id;
    private String title;//文件名称
    private String time;//显示时间
    private String duration;//持续时间
    private String path;
    private long durationLong;
    private long lastModified;//文件修改时间
    private String fileSuffix;//文件后缀名
    private boolean isPlaying = false;

    private int currentProgress = 0; //播放进度

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDurationLong() {
        return durationLong;
    }

    public void setDurationLong(long durationLong) {
        this.durationLong = durationLong;
    }

    public AudioBean() {
    }

    public AudioBean(String id, String title, String time, String duration, String path, long durationLong, long lastModified, String fileSuffix, long fileLength) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.duration = duration;
        this.path = path;
        this.durationLong = durationLong;
        this.lastModified = lastModified;
        this.fileSuffix = fileSuffix;
        this.fileLength = fileLength;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    private long fileLength;//文件字节数



}
