package org.gears3d.gears3d

import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Gears3dRenderer : GLSurfaceView.Renderer {
    var mGears3d: Gears3d

    init {
        mGears3d = Gears3d()
    }

    override fun onSurfaceCreated(gl10: GL10, eglConfig: EGLConfig) {
        mGears3d.set_global_state()
    }

    override fun onSurfaceChanged(gl10: GL10, width: Int, height: Int) {
        mGears3d.win_resize(width, height)
    }

    override fun onDrawFrame(gl10: GL10) {
        mGears3d.draw()
    }
}
