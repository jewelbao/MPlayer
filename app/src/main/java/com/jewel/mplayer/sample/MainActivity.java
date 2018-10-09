package com.jewel.mplayer.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.jewel.lib.android.SnackbarUtils;
import com.jewel.mplayer.ControlView;
import com.jewel.mplayer.OnControlViewListener;
import com.jewel.mplayer.content.VideoData;
import com.jewel.mplayer.system.SystemVideoView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SystemVideoView videoView;
    private ControlView controlView;

    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.videoView);
        videoView.setVideo(new VideoData("https://mov.bn.netease.com/open-movie/nos/mp4/2018/01/12/SD70VQJ74_sd.mp4"));
        videoView.start();

        controlView = findViewById(R.id.controlView);
        controlView.attachToVideo(this, videoView);
        controlView.setOnControlViewListener(new OnControlViewListener() {
            @Override
            public void onBackControl() {
                SnackbarUtils.showAction(getWindow().getDecorView(), "我不看了，我想走");
            }

            @Override
            public void onStartOrPauseControl() {
                count++;
                SnackbarUtils.showAction(getWindow().getDecorView(), "哈哈哈哈，你点我了" + count);
            }
        });

        Button btnBack = findViewById(R.id.btn_back);
        Button btnForward = findViewById(R.id.btn_forward);
        Button btnChangeTitle = findViewById(R.id.btn_change_title);

        btnBack.setOnClickListener(this);
        btnForward.setOnClickListener(this);
        btnChangeTitle.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.resume();
    }

    @Override
    protected void onDestroy() {
        videoView.release();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                controlView.seekBack(1000*10);
                break;
            case R.id.btn_forward:
                controlView.seekForward(1000*10);
                break;
            case R.id.btn_change_title:
                controlView.setTitle("聪哥");
                break;
        }
    }
}
