package com.jewel.mplayer;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jewel.mplayer.content.States;

import static com.jewel.mplayer.CoreVideoView.TAG;
import static com.jewel.mplayer.Utils.percent;
import static com.jewel.mplayer.Utils.timeToString;

public class ControlView extends FrameLayout implements View.OnClickListener {

    private int playingTimeViewId;
    private int startOrPauseViewId;
    private int totalTimeViewId;
    private int titleViewId;
    private int progressbarId;

    private String title;
    private boolean showProgressBufferView;
    private int leftTitleDrawableRes;
    private boolean showTitleLeftIcon;
    private int rightTitleDrawableRes;
    private boolean showTitleRightIcon;

    private View startOrPauseView;
    private TextView playingTimeView;
    private TextView totalTimeView;
    private TextView titleView;
    private ProgressBar progressbar;

    private CoreVideoView videoView;
    private OnControlViewListener onControlViewListener;

    public ControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ControlView);
            playingTimeViewId = typedArray.getResourceId(R.styleable.ControlView_playingTimeView, 0);
            startOrPauseViewId = typedArray.getResourceId(R.styleable.ControlView_startOrPauseView, 0);
            totalTimeViewId = typedArray.getResourceId(R.styleable.ControlView_totalTimeView, 0);
            titleViewId = typedArray.getResourceId(R.styleable.ControlView_titleView, 0);
            progressbarId = typedArray.getResourceId(R.styleable.ControlView_progressbar, 0);
            title = typedArray.getString(R.styleable.ControlView_title);
            showProgressBufferView = typedArray.getBoolean(R.styleable.ControlView_showProgressBufferView, false);
            leftTitleDrawableRes = typedArray.getResourceId(R.styleable.ControlView_titleLeftIcon, 0);
            showTitleLeftIcon = typedArray.getBoolean(R.styleable.ControlView_showTitleLeftIcon, false);
            rightTitleDrawableRes = typedArray.getResourceId(R.styleable.ControlView_titleRightIcon, 0);
            showTitleRightIcon = typedArray.getBoolean(R.styleable.ControlView_showTitleRightIcon, false);
            typedArray.recycle();
        }
    }

    /**
     * 控制器UI核心方法。
     *
     * @param context   {@link CoreVideoView}所在的Activity
     * @param videoView Activity内的播放器View
     */
    public void attachToVideo(Activity context, CoreVideoView videoView) {
        this.videoView = videoView;
        initPlayingTimeView(context);
        initTotalTimeView(context);
        initProgressbar(context);
        initStartOrPauseView(context);
        initTitleView(context);
        videoView.addOnVideoChangeListener(onVideoChangeListener);
    }

    /**
     * 控制器内置监听事件，由控制器内部定义UI实现的点击事件转发。
     *
     * @param onControlViewListener {@link OnControlViewListener}
     */
    public void setOnControlViewListener(OnControlViewListener onControlViewListener) {
        this.onControlViewListener = onControlViewListener;
    }

    /**
     * 是否显示进度条缓冲视图
     *
     * @return 默认<code>false</code>
     */
    public boolean isShowProgressBufferView() {
        return showProgressBufferView;
    }

    /**
     * 获取标题栏左侧图标
     */
    public int getLeftTitleDrawableRes() {
        return leftTitleDrawableRes;
    }

    /**
     * 是否显示标题栏左侧图标，为<code>true</code>时点击有效。事件触发为{@link OnControlViewListener#onBackControl()}
     *
     * @return 默认<code>false</code>
     */
    public boolean isShowTitleLeftIcon() {
        return showTitleLeftIcon;
    }

    /**
     * 获取标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置标题，此方法请在{@link ControlView#attachToVideo(Activity, CoreVideoView)}之后调用。<br>
     * 如果设置无效果，请检查是否在layout文件中引用<code>app:titleView="@id/你的标题ViewId" </code>且该标题View为TextView或其子类。<br>
     * 其他自定义标题栏View请自行处理。
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        this.title = title;
        if (titleView == null) {
            return;
        }
        if (!TextUtils.isEmpty(title)) {
            titleView.setText(title);
        }
    }

    /**
     * 回退
     *
     * @param millis 回退时间
     */
    public void seekBack(int millis) {
        videoView.seekTo(videoView.getRealSeekPosition(-millis));
    }

    /**
     * 快进
     *
     * @param millis 快进时间
     */
    public void seekForward(int millis) {
        videoView.seekTo(videoView.getRealSeekPosition(millis));
    }

    // 初始化播放/暂停按钮和点击事件
    private void initStartOrPauseView(Activity context) {
        if (startOrPauseView == null) {
            startOrPauseView = context.findViewById(startOrPauseViewId);
        }
        if (startOrPauseView == null) {
            return;
        }
        startOrPauseView.setOnClickListener(this);
    }

    // 初始化播放时间
    private void initPlayingTimeView(Activity context) {
        if (playingTimeView == null) {
            playingTimeView = context.findViewById(playingTimeViewId);
        }
    }

    // 初始化总时长
    private void initTotalTimeView(Activity context) {
        if (totalTimeView == null) {
            totalTimeView = context.findViewById(totalTimeViewId);
        }
    }

    // 初始化标题栏标题、返回图片资源和点击事件
    private void initTitleView(Activity context) {
        if (titleView == null) {
            titleView = context.findViewById(titleViewId);
        }
        if (titleView == null) {
            return;
        }
        if (!TextUtils.isEmpty(getTitle())) {
            titleView.setText(getTitle());
        }
        if (isShowTitleLeftIcon() && getLeftTitleDrawableRes() != 0) {
            setDrawable(titleView, getLeftTitleDrawableRes());
        }
        titleView.setOnClickListener(this);
    }

    private static void setDrawable(TextView view, @DrawableRes int drawableId) {
        Drawable drawable = view.getResources().getDrawable(drawableId);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        view.setCompoundDrawables(drawable, null, null, null);
    }

    // 初始化播放进度条、缓冲进度条
    private void initProgressbar(Activity context) {
        if (progressbar == null) {
            progressbar = context.findViewById(progressbarId);
        }
        if (progressbar == null) {
            return;
        }
        progressbar.setMax(100);
        progressbar.setProgress(0);
        if (isShowProgressBufferView()) {
            progressbar.setSecondaryProgress(0);
        }
    }

    // 进度UI变更
    private void progressChange(int currentPosition, int bufferPercentage, int duration) {
        if (playingTimeView != null) {
            playingTimeView.setText(timeToString(currentPosition));
        }
        if (totalTimeView != null) {
            totalTimeView.setText(timeToString(duration));
        }
        if (progressbar != null) {
            progressbar.setProgress(percent(currentPosition, duration));
            if (isShowProgressBufferView()) {
                progressbar.setSecondaryProgress(bufferPercentage);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (onControlViewListener != null) {
            if (v.getId() == titleViewId) {
                if (isShowTitleLeftIcon()) onControlViewListener.onBackControl();
            } else if (v.getId() == startOrPauseViewId) {
                Log.d(TAG, "当前播放状态：" + videoView.getVideo().getCurrentState());
                if (videoView.getVideo().getCurrentState() == States.PLAYING || videoView.getVideo().getCurrentState() == States.BUFFER) {
                    videoView.pause();
                } else if (videoView.getVideo().getCurrentState() == States.PAUSED) {
                    videoView.resume();
                } else {
                    videoView.start();
                }
                onControlViewListener.onStartOrPauseControl();
            }
        }
    }

    private OnVideoChangeListener onVideoChangeListener = new DefaultVideoChangeListener() {
        @Override
        public void onVideoProgress(CoreVideoView videoView, int currentPosition, int bufferPercentage, int duration) {
            super.onVideoProgress(videoView, currentPosition, bufferPercentage, duration);
            progressChange(currentPosition, bufferPercentage, duration);
        }
    };
}
