package com.jewel.mplayer.content;

import android.net.Uri;

import java.util.Map;

public class VideoData {

    private int currentPlayer = Player.SYSTEM;
    /**
     * 视频地址
     */
    private String path;
    /**
     * 视频地址
     */
    private Uri uri;
    /**
     * 视频地址请求头
     */
    private Map<String, String> headers;
    /**
     * 视频标题
     */
    private String title;
    /**
     * 视频宽度
     */
    private int videoWidth;
    /**
     * 视频高度
     */
    private int videoHeight;
    /**
     * 缓冲进度0-100
     */
    private int currentBufferPercentage;
    /**
     * 当前状态{@link States}
     */
    private int currentState;
    /**
     * 当前已播放时间，单位毫秒
     */
    private int currentPosition;
    /**
     * 视频时长
     */
    private int duration;

    /**
     * 是否可暂停
     */
    private boolean canPause;
    /**
     * 是否可回退
     */
    private boolean canSeekBack;
    /**
     * 是否可快进
     */
    private boolean canSeekForward;
    /**
     * 是否可循环播放
     */
    private boolean canLooping;

    public VideoData(String path) {
        this(path, "");
    }

    public VideoData(String path, String title) {
        this(Uri.parse(path), title);
        this.path = path;
    }

    public VideoData(Uri uri) {
        this(uri, "");
    }

    public VideoData(Uri uri, String title) {
        this.uri = uri;
        this.title = title;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        this.uri = Uri.parse(path);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

    public int getCurrentBufferPercentage() {
        return currentBufferPercentage;
    }

    public void setCurrentBufferPercentage(int currentBufferPercentage) {
        this.currentBufferPercentage = currentBufferPercentage;
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean canPause() {
        return canPause;
    }

    public void setPause(boolean canPause) {
        this.canPause = canPause;
    }

    public boolean canSeekBack() {
        return canSeekBack;
    }

    public void setSeekBack(boolean canSeekBack) {
        this.canSeekBack = canSeekBack;
    }

    public boolean canSeekForward() {
        return canSeekForward;
    }

    public void setSeekForward(boolean canSeekForward) {
        this.canSeekForward = canSeekForward;
    }

    public boolean canLooping() {
        return canLooping;
    }

    public void setLooping(boolean canlooping) {
        this.canLooping = canlooping;
    }
}
