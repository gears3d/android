/* Jordan Justen : gears3d is public domain */

package org.gears3d.gears3d;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Gears3d {
    FloatBuffer mVertexBuffer;
    int program;
    int vert_bo;
    int gear_angle_loc;

    public Gears3d() {
        final float[] vertData = {
                -0.5f, -0.5f, 0.0f,
                0.0f, 0.0f, 1.0f,

                0.5f, -0.5f, 0.0f,
                0.0f, 0.0f, 1.0f,

                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 1.0f,
        };

        ByteBuffer vertexBuffer = ByteBuffer.allocateDirect(vertData.length * 4);
        vertexBuffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = vertexBuffer.asFloatBuffer();
        mVertexBuffer.put(vertData);
        mVertexBuffer.position(0);
    }

    private final String vs_src = "#version 100\n" +
            "\n" +
            "attribute vec3 vertex;\n" +
            "attribute vec3 rel_norm;\n" +
            "\n" +
            "uniform float gear_angle;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    float ang = gear_angle;\n" +
            "    mat2 rotz = mat2(vec2(cos(ang), sin(ang)),\n" +
            "                     vec2(-sin(ang), cos(ang)));\n" +
            "\n" +
            "    vec3 pos = vec3(rotz * vertex.xy, vertex.z);\n" +
            "    gl_Position = vec4(pos, 1.0);\n" +
            "}\n";

    private final String fs_src = "#version 100\n" +
            "precision highp float;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    gl_FragColor = vec4(0.0, 1.0, 0.0, 1.0);\n" +
            "}\n";

    public void set_global_state() {
        GLES20.glClearColor(0.0f, 0.1f, 0.0f, 1.0f);
        program = GlShader.gl_program_vf_str(vs_src, fs_src);
        GLES20.glUseProgram(program);
        gear_angle_loc = GLES20.glGetUniformLocation(program, "gear_angle");
        final int[] a_vert_bo = new int[1];
        GLES20.glGenBuffers(1, a_vert_bo, 0);
        vert_bo = a_vert_bo[0];
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vert_bo);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mVertexBuffer.capacity() * 4,
                mVertexBuffer, GLES20.GL_STATIC_DRAW);
        GlShader.gl_attrib_ptr(program, "vertex", 3, GLES20.GL_FLOAT, false,
                6 * 4, 0);
        GlShader.gl_attrib_ptr(program, "rel_norm", 3, GLES20.GL_FLOAT, false,
                6 * 4, 3 * 4);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    public void win_resize(int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    private float gear_angle;
    private static long NANO_PER_REV = 1000000000L * 60 / 70;
    private long start_time = System.nanoTime();
    private void update_angle() {
        long nano_time = System.nanoTime() - start_time;
        if (NANO_PER_REV >= nano_time) {
            nano_time -= NANO_PER_REV;
        }
        gear_angle = (float)(2 * Math.PI * ((double)nano_time / NANO_PER_REV));
    }

    public void draw() {
        update_angle();
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUniform1f(gear_angle_loc, gear_angle);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 3);
    }
}
