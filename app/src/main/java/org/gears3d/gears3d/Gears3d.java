/* Jordan Justen : gears3d is public domain */

package org.gears3d.gears3d;

import android.opengl.GLES20;

public class Gears3d {
    public void set_global_state() {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    public void win_resize(int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    public void draw() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }
}
