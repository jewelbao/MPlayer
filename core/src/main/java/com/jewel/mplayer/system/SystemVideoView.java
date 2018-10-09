package com.jewel.mplayer.system;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;

import com.jewel.mplayer.CoreVideoView;
import com.jewel.mplayer.OnVideoChangeListener;
import com.jewel.mplayer.R;
import com.jewel.mplayer.Utils;
import com.jewel.mplayer.content.ErrorMessage;
import com.jewel.mplayer.content.VideoData;

import java.util.List;

public class SystemVideoView extends CoreVideoView {

    public static final int MEDIA_ERROR_SYSTEM = -2147483648; // - low-level system error.
    public static final int MEDIA_INFO_NETWORK_BANDWIDTH = 703;// - bandwidth information is available (as extra kbps)

    private VideoView videoView;

    public SystemVideoView(Context context) {
        this(context, null);
    }

    public SystemVideoView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SystemVideoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View videoGroup = View.inflate(getContext(), R.layout.layout_system_video, this);
        videoView = videoGroup.findViewById(R.id.sys_video);
        videoView.setOnPreparedListener(onPreparedListener);
        videoView.setOnCompletionListener(onCompletionListener);
        videoView.setOnInfoListener(onInfoListener);
        videoView.setOnErrorListener(onErrorListener);
    }

    @Override
    public void setVideo(VideoData data) {
        super.setVideo(data);
        if (data != null) {
            if (!TextUtils.isEmpty(data.getPath())) {
                videoView.setVideoPath(data.getPath());
            } else if (data.getUri() != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    videoView.setVideoURI(data.getUri(), data.getHeaders());
                } else {
                    videoView.setVideoURI(data.getUri());
                }
            }
        }
    }

    @Override
    public void start() {
        videoView.start();
        listenerOnStart();
        recordProgress(500L);
    }

    @Override
    public void seekTo(int milliseconds) {
        if(canSeek()) {
            videoView.seekTo(milliseconds);
        }
    }

    @Override
    public void pause() {
        if(isPlaying() || isBuffering()) { // 播放或缓冲中可以暂停
            stopRecordProgress();
            listenerOnPause();
            int currentPos = videoView.getCurrentPosition();
            Log.d(TAG, "暂停时的播放进度：" + Utils.timeToString(currentPos));
            getVideo().setCurrentPosition(currentPos);
            videoView.pause();
        }
    }

    @Override
    public void resume() {
        if(isPreparing() || isPlaying()) {
            return;
        }
        start();
        seekTo(getVideo().getCurrentPosition());
    }

    @Override
    public void release() {
        videoView.suspend();
        stopRecordProgress();
        listenerOnRelease();
    }

    private void recordProgress(long delayMillis) {
        videoView.getRootView().postDelayed(progressListener, delayMillis);
    }

    private void stopRecordProgress() {
        videoView.getRootView().removeCallbacks(progressListener);
    }

    private Runnable progressListener = new Runnable() {
        @Override
        public void run() {
            int currentPosition = videoView.getCurrentPosition();
            getVideo().setCurrentPosition(currentPosition);
            listenerOnProgressChanged(currentPosition, videoView.getBufferPercentage(), videoView.getDuration());
            recordProgress(1000L);
        }
    };

    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.setOnSeekCompleteListener(onSeekCompleteListener);
            listenerOnPrepared();
            getVideo().setDuration(videoView.getDuration());
            getVideo().setPause(videoView.canPause());
            getVideo().setSeekBack(videoView.canSeekBackward());
            getVideo().setSeekForward(videoView.canSeekForward());
            getVideo().setVideoWidth(mp.getVideoWidth());
            getVideo().setVideoHeight(mp.getVideoHeight());
        }
    };

    private MediaPlayer.OnInfoListener onInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            List<OnVideoChangeListener> onVideoChangeListener = getOnVideoChangeListeners();
            if (onVideoChangeListener == null) {
                return false;
            }
            switch (what) {  // 信息类型
                case MediaPlayer.MEDIA_INFO_UNKNOWN:
                    stopRecordProgress();
                    listenerOnError(ErrorMessage.WHAT_ERROR_UNKNOWN, ErrorMessage.MSG_ERROR_UNKNOWN);
                    break;
                case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                    stopRecordProgress();
                    listenerOnError(ErrorMessage.INFO_VIDEO_TRACK_LAGGING, ErrorMessage.MSG_ERROR_VIDEO_TRACK_LAGGING);
                    break;
                case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    // MediaPlayer暂时暂停内部播放以缓冲更多数据。
                    listenerOnBufferStart();
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    // 填充缓冲区后，MediaPlayer正在恢复播放。
                    listenerOnBufferEnd();
                    break;
                case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                    stopRecordProgress();
                    listenerOnError(ErrorMessage.INFO_BAD_INTERLEAVING, ErrorMessage.MSG_ERROR_BAD_INTERLEAVING);
                    break;
                case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                    stopRecordProgress();
                    listenerOnError(ErrorMessage.INFO_NOT_SEEKABLE, ErrorMessage.MSG_ERROR_NOT_SEEKABLE);
                    break;
                case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                    // 有新的媒体数据可用
                    break;
                case MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                    stopRecordProgress();
                    listenerOnError(ErrorMessage.INFO_NOT_UNSUPPORTED_SUBTITLE, ErrorMessage.MSG_ERROR_UNSUPPORTED_SUBTITLE);
                    break;
                case MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                    stopRecordProgress();
                    listenerOnError(ErrorMessage.INFO_NOT_SUBTITLE_TIMED_OUT, ErrorMessage.MSG_ERROR_SUBTITLE_TIMED_OUT);
                    break;
                case MEDIA_INFO_NETWORK_BANDWIDTH:
                    // 带宽可用
                    break;
            }
            return true;
        }
    };

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            // 播放结束，当播放结束且setLooping(false)时触发
            stopRecordProgress();
            listenerOnComplete();
            getVideo().setCurrentPosition(0);
        }
    };

    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            stopRecordProgress();

            List<OnVideoChangeListener> onVideoChangeListeners = getOnVideoChangeListeners();
            if (onVideoChangeListeners == null || onVideoChangeListeners.isEmpty()) {
                return false;   // 返回false会回调OnCompletionListener。
            }

            int errorCode = ErrorMessage.WHAT_ERROR_UNKNOWN;
            String errorMsg = ErrorMessage.MSG_ERROR_UNKNOWN;

            // 播放器异常
            if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
                errorCode = ErrorMessage.WHAT_ERROR_UNKNOWN;
                errorMsg = ErrorMessage.MSG_ERROR_UNKNOWN;
            } else if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                errorCode = ErrorMessage.WHAT_ERROR_SERVER_DIED;
                errorMsg = ErrorMessage.MSG_ERROR_SERVER_DIED;
            } else {
                switch (extra) { // 异常信息
                    case MediaPlayer.MEDIA_ERROR_IO:
                        errorCode = ErrorMessage.EXTRA_ERROR_IO;
                        errorMsg = ErrorMessage.MSG_ERROR_IO;
                        break;
                    case MediaPlayer.MEDIA_ERROR_MALFORMED:
                        errorCode = ErrorMessage.EXTRA_ERROR_MALFORMED;
                        errorMsg = ErrorMessage.MSG_ERROR_MALFORMED;
                        break;
                    case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                        errorCode = ErrorMessage.EXTRA_ERROR_UNSUPPORTED;
                        errorMsg = ErrorMessage.MSG_ERROR_UNSUPPORTED;
                        break;
                    case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                        errorCode = ErrorMessage.EXTRA_ERROR_TIMED_OUT;
                        errorMsg = ErrorMessage.MSG_ERROR_TIMED_OUT;
                        break;
                    case MEDIA_ERROR_SYSTEM:
                        errorCode = ErrorMessage.EXTRA_ERROR_SYSTEM;
                        errorMsg = ErrorMessage.MSG_ERROR_SYSTEM;
                        break;
                }
            }

            listenerOnError(errorCode, errorMsg);

            return true; // 返回false会回调OnCompletionListener。
        }
    };

    private MediaPlayer.OnSeekCompleteListener onSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {
            listenerOnSeekCompleted(getVideo().getCurrentPosition(), videoView.getBufferPercentage(), videoView.getDuration());
        }
    };
}
