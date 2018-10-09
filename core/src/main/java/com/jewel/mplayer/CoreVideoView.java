package com.jewel.mplayer;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.jewel.mplayer.content.States;
import com.jewel.mplayer.content.VideoData;

import java.util.ArrayList;
import java.util.List;

public abstract class CoreVideoView extends FrameLayout implements IControl, IAttrubite {

    public static final String TAG = "MPlayer";

    private VideoData videoData;
    private List<OnVideoChangeListener> onVideoChangeListeners;

    public CoreVideoView(Context context) {
        this(context, null);
    }

    public CoreVideoView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoreVideoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 增加视频各状态监听事件
     */
    public void addOnVideoChangeListener(OnVideoChangeListener onVideoChangeListener) {
        if (onVideoChangeListener == null) {
            return;
        }
        if (onVideoChangeListeners == null) {
            onVideoChangeListeners = new ArrayList<>();
        }
        if (!onVideoChangeListeners.contains(onVideoChangeListener)) {
            onVideoChangeListeners.add(onVideoChangeListener);
        }
    }

    @Override
    public void setVideo(VideoData data) {
        this.videoData = data;
    }

    @Override
    public VideoData getVideo() {
        return videoData;
    }

    /**
     * 是否在播放视频过程状态--播放中，缓冲中，暂停状态
     */
    public boolean hadStartedAndNotFinish() {
        int state = getVideo().getCurrentState();
        return state != States.ERROR && state != States.COMPLETED;
    }

    /**
     * 是否正在播放视频
     */
    public boolean isPlaying() {
        return getVideo().getCurrentState() == States.PLAYING;
    }

    /**
     * 是否初始化视频中
     */
    public boolean isPreparing() {
        return getVideo().getCurrentState() == States.PREPARING;
    }

    /**
     * 是否缓冲中
     */
    public boolean isBuffering() {
        return getVideo().getCurrentState() == States.BUFFER;
    }

    /**
     * 是否暂停中
     */
    public boolean isPaused() {
        return getVideo().getCurrentState() == States.PAUSED;
    }

    /**
     * 是否可以快进快退
     */
    public boolean canSeek() {
        return getVideo().canSeekBack() && getVideo().canSeekForward() && hadStartedAndNotFinish();
    }

    /**
     * 获取实际的快进快退时间
     *
     * @param milliseconds 用户预期想在当前时间上快退快进的时间
     */
    public int getRealSeekPosition(int milliseconds) {
        int realSeekPosition;
        int currentPosition = getVideo().getCurrentPosition();
        int total = getVideo().getDuration();
        if (milliseconds > 0) { // 快进
            if (currentPosition + milliseconds < total) {
                realSeekPosition = currentPosition + milliseconds;
            } else {
                realSeekPosition = total;
            }
        } else { // 快退
            if (currentPosition + milliseconds > 0) {
                realSeekPosition = currentPosition + milliseconds;
            } else {
                realSeekPosition = 0;
            }
        }
        getVideo().setCurrentPosition(realSeekPosition);
        return realSeekPosition;
    }

    protected List<OnVideoChangeListener> getOnVideoChangeListeners() {
        if (onVideoChangeListeners == null) {
            onVideoChangeListeners = new ArrayList<>();
        }
        return onVideoChangeListeners;
    }

    protected void listenerOnPrepared() {
        getVideo().setCurrentState(States.PREPARED);
        Log.d(CoreVideoView.TAG, "视频准备完成开始播放");
        for (OnVideoChangeListener onVideoChangeListener : getOnVideoChangeListeners()) {
            onVideoChangeListener.onVideoPrepared(this);
        }
    }

    protected void listenerOnStart() {
        getVideo().setCurrentState(States.PREPARING);
        Log.d(CoreVideoView.TAG, "视频准备播放");
        for (OnVideoChangeListener onVideoChangeListener : getOnVideoChangeListeners()) {
            onVideoChangeListener.onVideoStarted(this);
        }
    }

    protected void listenerOnProgressChanged(int currentPosition, int bufferPercentage, int duration) {
        getVideo().setCurrentState(States.PLAYING);
        String currentTime = Utils.timeToString(currentPosition);
        String totalTime = Utils.timeToString(duration);
        Log.d(CoreVideoView.TAG, String.format("视频播放中，播放进度：%s，缓冲进度：%s，总时长：%s", currentTime, bufferPercentage, totalTime));
        for (OnVideoChangeListener onVideoChangeListener : getOnVideoChangeListeners()) {
            onVideoChangeListener.onVideoProgress(this, currentPosition,
                    bufferPercentage, duration);
        }
    }

    protected void listenerOnSeekCompleted(int currentPosition, int bufferPercentage, int duration) {
        Log.d(CoreVideoView.TAG, String.format("视频跳转完成，播放进度：%s，缓冲进度：%s，总时长：%s", Utils.timeToString(currentPosition), bufferPercentage, Utils.timeToString(duration)));
        for (OnVideoChangeListener onVideoChangeListener : getOnVideoChangeListeners()) {
            onVideoChangeListener.onVideoSeekCompleted(this, currentPosition,
                    bufferPercentage, duration);
        }
    }

    protected void listenerOnBufferStart() {
        getVideo().setCurrentState(States.BUFFER);
        Log.d(CoreVideoView.TAG, "视频缓冲中");
        for (OnVideoChangeListener onVideoChangeListener : getOnVideoChangeListeners()) {
            onVideoChangeListener.onVideoBufferStart(this);
        }
    }

    protected void listenerOnBufferEnd() {
        Log.d(CoreVideoView.TAG, "视频缓冲结束");
        for (OnVideoChangeListener onVideoChangeListener : getOnVideoChangeListeners()) {
            onVideoChangeListener.onVideoBufferEnd(this);
        }
    }

    protected void listenerOnPause() {
        getVideo().setCurrentState(States.PAUSED);
        Log.d(CoreVideoView.TAG, "视频暂停");
        for (OnVideoChangeListener onVideoChangeListener : getOnVideoChangeListeners()) {
            onVideoChangeListener.onVideoPaused(this);
        }
    }

    protected void listenerOnComplete() {
        getVideo().setCurrentState(States.COMPLETED);
        Log.d(CoreVideoView.TAG, "视频播放结束");
        for (OnVideoChangeListener onVideoChangeListener : getOnVideoChangeListeners()) {
            onVideoChangeListener.onVideoCompleted(this);
        }
    }

    protected void listenerOnRelease() {
        getVideo().setCurrentState(States.PREPARING);
        Log.d(CoreVideoView.TAG, "视频释放");
        for (OnVideoChangeListener onVideoChangeListener : getOnVideoChangeListeners()) {
            onVideoChangeListener.onVideoReleased(this);
        }
    }

    protected void listenerOnError(int errorCode, String msg) {
        getVideo().setCurrentState(States.ERROR);
        Log.e(CoreVideoView.TAG, String.format("视频异常：%s", msg));
        for (OnVideoChangeListener onVideoChangeListener : getOnVideoChangeListeners()) {
            onVideoChangeListener.onVideoError(this, errorCode, msg);
        }
    }
}
