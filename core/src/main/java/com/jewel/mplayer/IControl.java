package com.jewel.mplayer;

/**
 * 视频控制器接口--基础接口
 */
public interface IControl {

    /**
     * 开始播放
     */
    void start();

    /**
     * 快进快退
     * @param milliseconds 大于0表示快进时间，小于0表示快退时间
     */
    void seekTo(int milliseconds);

    /**
     * 暂停视频
     */
    void pause();

    /**
     * 恢复播放
     */
    void resume();

    /**
     * 释放资源
     */
    void release();
}
