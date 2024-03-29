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
    int gear_color_loc;
    int model_loc;

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
        int total_vert_count = 0;
        for (GearInfo g : gears) {
            g.vertex_buf_offset = total_vert_count;
            g.num_vertices = VertBuf.gear_vertex_count(g.teeth);
            total_vert_count += g.num_vertices;
        }

        int floats_count = total_vert_count * VertBuf.FLOATS_PER_VERT;
        int bytes_count = floats_count * 4;
        ByteBuffer vertexBuffer = ByteBuffer.allocateDirect(bytes_count);
        vertexBuffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = vertexBuffer.asFloatBuffer();

        for (GearInfo g : gears) {
            VertBuf.fill_gear_vertices(mVertexBuffer, g.inner_radius,
                                       g.outer_radius, g.width,
                                       g.teeth, g.tooth_depth);
        }

        mVertexBuffer.position(0);
    }

    private final String vs_src = "#version 100\n" +
            "\n" +
            "uniform mat4 model;\n" +
            "uniform mat4 view;\n" +
            "uniform mat4 projection;\n" +
            "\n" +
            "uniform float gear_angle;\n" +
            "\n" +
            "attribute vec3 vertex;\n" +
            "attribute vec3 rel_norm;\n" +
            "varying vec3 norm;\n" +
            "varying vec3 light_dir;\n" +
            "\n" +
            "const vec3 light_pos = vec3(5.0, 5.0, 10.0);\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    float ang = gear_angle;\n" +
            "    mat2 rotz = mat2(vec2(cos(ang), sin(ang)),\n" +
            "                     vec2(-sin(ang), cos(ang)));\n" +
            "\n" +
            "    vec3 pos = vec3(rotz * vertex.xy, vertex.z);\n" +
            "    vec4 m_pos = model * vec4(pos, 1.0);\n" +
            "    m_pos = vec4(m_pos.xyz / m_pos.w, 1.0);\n" +
            "    gl_Position = projection * view * m_pos;\n" +
            "\n" +
            "    light_dir = normalize(light_pos - m_pos.xyz);\n" +
            "\n" +
            "    vec3 n_pos = vertex + rel_norm;\n" +
            "    n_pos = vec3(rotz * n_pos.xy, n_pos.z);\n" +
            "    vec4 m_norm = model * vec4(n_pos, 1.0);\n" +
            "    norm = normalize((m_norm.xyz / m_norm.w) - m_pos.xyz);\n" +
            "}\n";

    private final String fs_src = "#version 100\n" +
            "precision highp float;\n" +
            "\n" +
            "varying vec3 norm;\n" +
            "varying vec3 light_dir;\n" +
            "\n" +
            "uniform vec4 gear_color;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    float light_ref = clamp(0.0+dot(norm, light_dir), -0.0, 1.0);\n" +
            "    float light = clamp(0.2+light_ref, 0.1, 1.0);\n" +
            "    gl_FragColor = vec4(light * gear_color.xyz, 1.0);\n" +
            "}\n";

    static void rotate_gears(float x, float y, float z)
    {
        float[] m4, tmp;
        int i;

        for (GearInfo g : gears) {
            m4 = GfxMath.rotate(x, 1.0, 0.0, 0.0);
            if (y != 0.0) {
                tmp = GfxMath.rotate(y, 0.0, 1.0, 0.0);
                m4 = GfxMath.mult_m4m4(m4, tmp);
            }
            if (z != 0.0) {
                tmp = GfxMath.rotate(z, 0.0, 0.0, 1.0);
                m4 = GfxMath.mult_m4m4(m4, tmp);
            }
            tmp = GfxMath.translate(g.translate[0], g.translate[1], 0.0f);
            m4 = GfxMath.mult_m4m4(m4, tmp);
            g.setModel(m4);
        }
    }

    public void set_global_state() {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LESS);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);

        GLES20.glClearColor(0.0f, 0.1f, 0.0f, 1.0f);
        program = GlShader.gl_program_vf_str(vs_src, fs_src);
        GLES20.glUseProgram(program);
        gear_angle_loc = uniformLoc("gear_angle");
        gear_color_loc = uniformLoc("gear_color");
        model_loc = uniformLoc("model");
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

        rotate_gears((float) (20.0f / 180.0f * Math.PI),
                     (float) (30.0f / 180.0f * Math.PI), 0.0f);
    }

    public void win_resize(int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float[] m4 = GfxMath.translate(0.0f, 0.0f, -40.0f);
        GLES20.glUniformMatrix4fv(uniformLoc("view"), 1, false, m4, 0);

        float h = (float) height / width;
        m4 = GfxMath.frustum(-1.0, 1.0, -h, h, 5.0, 200.0);
        GLES20.glUniformMatrix4fv(uniformLoc("projection"), 1, false,
            m4, 0);
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
        for (GearInfo g : gears) {
            g.angle = gear_angle * g.angle_rate + g.angle_adjust;
        }
    }

    public void draw() {
        update_angle();
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        for (GearInfo g : gears) {
            GLES20.glUniform1f(gear_angle_loc, g.angle);
            GLES20.glUniform4fv(gear_color_loc, 1, g.color, 0);
            GLES20.glUniformMatrix4fv(model_loc, 1, false, g.model, 0);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, g.vertex_buf_offset,
                                g.num_vertices);
        }
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
