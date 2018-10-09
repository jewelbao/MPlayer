package com.jewel.mplayer;

public interface IControl {

    void start();
    void seekTo(int milliseconds);
    void pause();
    void resume();
    void release();
}
