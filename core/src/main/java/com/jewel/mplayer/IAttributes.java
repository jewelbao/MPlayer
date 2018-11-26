package com.jewel.mplayer;

import com.jewel.mplayer.content.VideoData;

/**
 * 视频属性接口--基础接口
 */
public interface IAttributes {

    /**
     * 设置视频数据源
     * @param data {@link VideoData}
     */
    void setVideo(VideoData data);

    /**
     * 获取当前播放视频源
     * @return {@link VideoData}
     */
    VideoData getVideo();
}
