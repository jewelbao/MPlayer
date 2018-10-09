package com.jewel.mplayer.content;

/**
 * 视频状态
 */
public class States {

    /**
     * 预备状态
     */
    public static final int PREPARING = 0;

    /**
     * 预备状态
     */
    public static final int PREPARED = 1;

    /**
     * 缓冲状态
     */
    public static final int BUFFER = 2;

    /**
     * 播放中状态
     */
    public static final int PLAYING = 3;

    /**
     * 暂停状态
     */
    public static final int PAUSED = 4;

    /**
     * 播放完成状态
     */
    public static final int COMPLETED = 5;

    /**
     * 播放异常
     */
    public static final int ERROR = -1;
}
