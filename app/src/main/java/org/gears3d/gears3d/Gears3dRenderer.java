package org.gears3d.gears3d;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Gears3dRenderer implements GLSurfaceView.Renderer {
    Gears3d mGears3d;

    public Gears3dRenderer() {
        mGears3d = new Gears3d();
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        mGears3d.set_global_state();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        mGears3d.win_resize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        mGears3d.draw();
    }
}
