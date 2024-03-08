package org.gears3d.gears3d

import android.content.Context
import android.opengl.GLSurfaceView

class Gears3dSurfaceView(context: Context?) : GLSurfaceView(context) {
    private val mRenderer: Gears3dRenderer

    init {

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)
        mRenderer = Gears3dRenderer()

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer)
    }
}
