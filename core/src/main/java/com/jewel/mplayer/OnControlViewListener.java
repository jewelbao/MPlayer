package com.jewel.mplayer;

/**
 * 控制器视图事件监听
 */
public interface OnControlViewListener {

    /**
     * 返回事件
     */
    void onBackControl();

    /**
     * 开始/暂停事件
     */
    void onStartOrPauseControl();
}
