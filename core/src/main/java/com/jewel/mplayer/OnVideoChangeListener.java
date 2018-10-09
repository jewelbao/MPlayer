package com.jewel.mplayer;

public interface OnVideoChangeListener {
    /**
     * 准备播放
     */
    void onVideoPrepared(CoreVideoView videoView);

    /**
     * 播放中
     */
    void onVideoStarted(CoreVideoView videoView);

    /**
     * 正在缓冲中--{@link com.jewel.mplayer.content.Player#SYSTEM} 播放器会暂停播放，直至缓冲结束
     */
    void onVideoBufferStart(CoreVideoView videoView);

    /**
     * 缓冲结束
     */
    void onVideoBufferEnd(CoreVideoView videoView);

    /**
     * 当前播放进度
     * @param currentPosition 当前播放时间，毫秒
     * @param bufferPercentage 当前缓冲进度（0-100）
     * @param duration 当前视频总时长
     */
    void onVideoProgress(CoreVideoView videoView, int currentPosition, int bufferPercentage, int duration);

    /**
     *  快进/快退跳转结束
     * @param currentPosition 当前跳转播放时间，毫秒
     * @param bufferPercentage 当前缓冲进度（0-100）
     * @param duration 当前视频总时长
     */
    void onVideoSeekCompleted(CoreVideoView videoView, int currentPosition, int bufferPercentage, int duration);

    /**
     * 暂停中
     */
    void onVideoPaused(CoreVideoView videoView);

    /**
     * 播放结束
     *
     */
    void onVideoCompleted(CoreVideoView videoView);

    /**
     * 视频资源释放
     */
    void onVideoReleased(CoreVideoView videoView);

    /**
     * 播放异常
     *
     * @param errorCode 异常信息代码
     * @param msg       异常信息
     */
    void onVideoError(CoreVideoView videoView, int errorCode, String msg);
}
