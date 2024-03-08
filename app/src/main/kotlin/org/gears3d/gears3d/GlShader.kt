/* Jordan Justen : gears3d is public domain */
package org.gears3d.gears3d

import android.opengl.GLES20

object GlShader {
    fun gl_program_vf_str(vs_src: String, fs_src: String): Int {
        val vs = compile_shader(GLES20.GL_VERTEX_SHADER, vs_src)
        val fs = compile_shader(GLES20.GL_FRAGMENT_SHADER, fs_src)
        val program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vs)
        GLES20.glAttachShader(program, fs)
        GLES20.glLinkProgram(program)
        return program
    }

    private fun compile_shader(target: Int, src: String): Int {
        val shader = GLES20.glCreateShader(target)
        GLES20.glShaderSource(shader, src)
        GLES20.glCompileShader(shader)
        val status = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0)
        if (status[0] == 0) {
            val error = GLES20.glGetShaderInfoLog(shader)
            GLES20.glDeleteShader(shader)
            throw RuntimeException("Error compiling shader\n$error")
        }
        return shader
    }

    private fun attrib_location(program: Int, name: String): Int {
        return GLES20.glGetAttribLocation(program, name)
    }

    fun gl_attrib_ptr(program: Int, name: String, size: Int, type: Int,
                      normalized: Boolean, stride: Int, offset: Int): Boolean {
        val location = attrib_location(program, name)
        if (location < 0) {
            return false
        }
        GLES20.glVertexAttribPointer(location, size, type, normalized, stride, offset)
        GLES20.glEnableVertexAttribArray(location)
        return true
    }
}
