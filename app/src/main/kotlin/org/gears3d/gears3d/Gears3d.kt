/* Jordan Justen : gears3d is public domain */
package org.gears3d.gears3d

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Gears3d {
    var mVertexBuffer: FloatBuffer
    var program = 0
    var vert_bo = 0
    var gear_angle_loc = 0
    var gear_color_loc = 0
    var model_loc = 0
    private val vs_src = """#version 100

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

uniform float gear_angle;

attribute vec3 vertex;
attribute vec3 rel_norm;
varying vec3 norm;
varying vec3 light_dir;

const vec3 light_pos = vec3(5.0, 5.0, 10.0);

void main()
{
    float ang = gear_angle;
    mat2 rotz = mat2(vec2(cos(ang), sin(ang)),
                     vec2(-sin(ang), cos(ang)));

    vec3 pos = vec3(rotz * vertex.xy, vertex.z);
    vec4 m_pos = model * vec4(pos, 1.0);
    m_pos = vec4(m_pos.xyz / m_pos.w, 1.0);
    gl_Position = projection * view * m_pos;

    light_dir = normalize(light_pos - m_pos.xyz);

    vec3 n_pos = vertex + rel_norm;
    n_pos = vec3(rotz * n_pos.xy, n_pos.z);
    vec4 m_norm = model * vec4(n_pos, 1.0);
    norm = normalize((m_norm.xyz / m_norm.w) - m_pos.xyz);
}
"""
    private val fs_src = """#version 100
precision highp float;

varying vec3 norm;
varying vec3 light_dir;

uniform vec4 gear_color;

void main()
{
    float light_ref = clamp(0.0+dot(norm, light_dir), -0.0, 1.0);
    float light = clamp(0.2+light_ref, 0.1, 1.0);
    gl_FragColor = vec4(light * gear_color.xyz, 1.0);
}
"""

    fun set_global_state() {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LESS)
        GLES20.glEnable(GLES20.GL_CULL_FACE)
        GLES20.glCullFace(GLES20.GL_BACK)
        GLES20.glClearColor(0.0f, 0.1f, 0.0f, 1.0f)
        program = GlShader.gl_program_vf_str(vs_src, fs_src)
        GLES20.glUseProgram(program)
        gear_angle_loc = uniformLoc("gear_angle")
        gear_color_loc = uniformLoc("gear_color")
        model_loc = uniformLoc("model")
        val a_vert_bo = IntArray(1)
        GLES20.glGenBuffers(1, a_vert_bo, 0)
        vert_bo = a_vert_bo[0]
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vert_bo)
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mVertexBuffer.capacity() * 4,
                mVertexBuffer, GLES20.GL_STATIC_DRAW)
        GlShader.gl_attrib_ptr(program, "vertex", 3, GLES20.GL_FLOAT, false,
                6 * 4, 0)
        GlShader.gl_attrib_ptr(program, "rel_norm", 3, GLES20.GL_FLOAT, false,
                6 * 4, 3 * 4)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        rotate_gears((20.0f / 180.0f * Math.PI).toFloat(), (30.0f / 180.0f * Math.PI).toFloat(), 0.0f)
    }

    fun win_resize(width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        var m4 = GfxMath.translate(0.0f, 0.0f, -40.0f)
        GLES20.glUniformMatrix4fv(uniformLoc("view"), 1, false,
                                  m4.toFloatArray(), 0)
        val h = height.toFloat() / width
        m4 = GfxMath.frustum(-1.0, 1.0, -h.toDouble(), h.toDouble(), 5.0, 200.0)
        GLES20.glUniformMatrix4fv(uniformLoc("projection"), 1, false,
                m4.toFloatArray(), 0)
    }

    private var gear_angle = 0f
    private val start_time = System.nanoTime()
    private fun update_angle() {
        var nano_time = System.nanoTime() - start_time
        if (NANO_PER_REV >= nano_time) {
            nano_time -= NANO_PER_REV
        }
        gear_angle = (2 * Math.PI * (nano_time.toDouble() / NANO_PER_REV)).toFloat()
        for (g in gears) {
            g.angle = gear_angle * g.angle_rate + g.angle_adjust
        }
    }

    fun draw() {
        update_angle()
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        for (g in gears) {
            GLES20.glUniform1f(gear_angle_loc, g.angle)
            GLES20.glUniform4fv(gear_color_loc, 1, g.color.toFloatArray(), 0)
            GLES20.glUniformMatrix4fv(model_loc, 1, false,
                                      g.model.toFloatArray(), 0)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,
                                g.vertex_buf_offset, g.num_vertices)
        }
    }

    fun uniformLoc(u: String): Int {
        if (uniform_locs.containsKey(u)) {
            return uniform_locs[u]!!
        }
        val loc = GLES20.glGetUniformLocation(program, u)
        assert(loc >= 0)
        uniform_locs[u] = loc
        return loc
    }

    var uniform_locs: HashMap<String, Int> = HashMap()

    init {
        var total_vert_count = 0
        for (g in gears) {
            g.vertex_buf_offset = total_vert_count
            g.num_vertices = VertBuf.gear_vertex_count(g.teeth)
            total_vert_count += g.num_vertices
        }
        val floats_count = total_vert_count * VertBuf.FLOATS_PER_VERT
        val bytes_count = floats_count * 4
        val vertexBuffer = ByteBuffer.allocateDirect(bytes_count)
        vertexBuffer.order(ByteOrder.nativeOrder())
        mVertexBuffer = vertexBuffer.asFloatBuffer()
        for (g in gears) {
            VertBuf.fill_gear_vertices(mVertexBuffer, g.inner_radius,
                    g.outer_radius, g.width,
                    g.teeth, g.tooth_depth)
        }
        mVertexBuffer.position(0)
    }

    companion object {
        var gears = arrayOf(
                GearInfo()
                        .setTeeth(20)
                        .setInner_radius(1.0f)
                        .setOuter_radius(4.0f)
                        .setWidth(1.0f)
                        .setTooth_depth(0.7f)
                        .setAngle_rate(1.0f)
                        .setAngle_adjust(0.0f)
                        .setTranslate(arrayOf(-3.0f, -2.0f))
                        .setColor(arrayOf(0.8f, 0.1f, 0.0f, 1.0f)),
                GearInfo()
                        .setTeeth(10)
                        .setInner_radius(0.5f)
                        .setOuter_radius(2.0f)
                        .setWidth(2.0f)
                        .setTooth_depth(0.7f)
                        .setAngle_rate(-2.0f)
                        .setAngle_adjust((Math.PI * -9.0 / 180.0).toFloat())
                        .setTranslate(arrayOf(3.1f, -2.0f))
                        .setColor(arrayOf(0.0f, 0.8f, 0.2f, 1.0f)),
                GearInfo()
                        .setTeeth(10)
                        .setInner_radius(1.3f)
                        .setOuter_radius(2.0f)
                        .setWidth(0.5f)
                        .setTooth_depth(0.7f)
                        .setAngle_rate(-2.0f)
                        .setAngle_adjust((Math.PI * -25.0 / 180.0).toFloat())
                        .setTranslate(arrayOf(-3.1f, 4.2f))
                        .setColor(arrayOf(0.2f, 0.2f, 1.0f, 1.0f)))

        fun rotate_gears(x: Float, y: Float, z: Float) {
            var m4: Array<Float>
            var tmp: Array<Float>
            var i: Int
            for (g in gears) {
                m4 = GfxMath.rotate(x.toDouble(), 1.0, 0.0, 0.0)
                if (y.toDouble() != 0.0) {
                    tmp = GfxMath.rotate(y.toDouble(), 0.0, 1.0, 0.0)
                    m4 = GfxMath.mult_m4m4(m4, tmp)
                }
                if (z.toDouble() != 0.0) {
                    tmp = GfxMath.rotate(z.toDouble(), 0.0, 0.0, 1.0)
                    m4 = GfxMath.mult_m4m4(m4, tmp)
                }
                tmp = GfxMath.translate(g.translate[0], g.translate[1], 0.0f)
                m4 = GfxMath.mult_m4m4(m4, tmp)
                g.setModel(m4)
            }
        }

        private const val NANO_PER_REV = 1000000000L * 360 / 70
    }
}
