/* Jordan Justen : gears3d is public domain */

package org.gears3d.gears3d;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;

public class Gears3d {
    FloatBuffer mVertexBuffer;
    int program;
    int vert_bo;
    int gear_angle_loc;

    static GearInfo[] gears = {
        new GearInfo()
            .setTeeth(20)
            .setInner_radius(1.0f)
            .setOuter_radius(4.0f)
            .setWidth(1.0f)
            .setTooth_depth(0.7f)
            .setAngle_rate(1.0f)
            .setAngle_adjust(0.0f)
            .setTranslate(new float[] { -3.0f, -2.0f })
            .setColor(new float[] { 0.8f, 0.1f, 0.0f, 1.0f }),
        new GearInfo()
            .setTeeth(10)
            .setInner_radius(0.5f)
            .setOuter_radius(2.0f)
            .setWidth(2.0f)
            .setTooth_depth(0.7f)
            .setAngle_rate(-2.0f)
            .setAngle_adjust((float) (Math.PI * -9.0 / 180.0))
            .setTranslate(new float[] { 3.1f, -2.0f })
            .setColor(new float[] { 0.0f, 0.8f, 0.2f, 1.0f }),
        new GearInfo()
            .setTeeth(10)
            .setInner_radius(1.3f)
            .setOuter_radius(2.0f)
            .setWidth(0.5f)
            .setTooth_depth(0.7f)
            .setAngle_rate(-2.0f)
            .setAngle_adjust((float) (Math.PI * -25.0 / 180.0))
            .setTranslate(new float[] { -3.1f, 4.2f })
            .setColor(new float[] { 0.2f, 0.2f, 1.0f, 1.0f }),
    };

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
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LESS);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);

        GLES20.glClearColor(0.0f, 0.1f, 0.0f, 1.0f);
        program = GlShader.gl_program_vf_str(vs_src, fs_src);
        GLES20.glUseProgram(program);
        gear_angle_loc = uniformLoc("gear_angle");
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
    private static long NANO_PER_REV = 1000000000L * 360 / 70;
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
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUniform1f(gear_angle_loc, gear_angle);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 3);
    }

    int uniformLoc(String u) {
        if (uniform_locs.containsKey(u)) {
            return uniform_locs.get(u);
        }

        int loc = GLES20.glGetUniformLocation(program, u);
        assert loc >= 0;
        uniform_locs.put(u, loc);
        return loc;
    }

    HashMap<String, Integer> uniform_locs = new HashMap();
}
