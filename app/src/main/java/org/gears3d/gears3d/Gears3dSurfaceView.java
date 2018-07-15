package org.gears3d.gears3d;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class Gears3dSurfaceView extends GLSurfaceView {
    private final Gears3dRenderer mRenderer;
    public Gears3dSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        mRenderer = new Gears3dRenderer();

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
