package com.jewel.mplayer;

import android.view.SurfaceHolder;

public interface ISurfaceCallback {
    void surfaceCreated(SurfaceHolder holder);

    void surfaceChanged(SurfaceHolder holder, int format, int width, int height);

    void surfaceDestroyed(SurfaceHolder holder);
}
