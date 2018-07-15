/* Jordan Justen : gears3d is public domain */

package org.gears3d.gears3d;

import android.opengl.GLES20;

public class GlShader {
    public static int gl_program_vf_str(String vs_src, String fs_src) {
        int vs = compile_shader(GLES20.GL_VERTEX_SHADER, vs_src);
        int fs = compile_shader(GLES20.GL_FRAGMENT_SHADER, fs_src);
        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vs);
        GLES20.glAttachShader(program, fs);
        GLES20.glLinkProgram(program);
        return program;
    }

    private static int compile_shader(int target, String src) {
        int shader = GLES20.glCreateShader(target);
        GLES20.glShaderSource(shader, src);
        GLES20.glCompileShader(shader);

        final int[] status = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0);
        if (status[0] == 0) {
            String error = GLES20.glGetShaderInfoLog(shader);
            GLES20.glDeleteShader(shader);
            throw new RuntimeException("Error compiling shader\n" + error);
        }

        return shader;
    }

    private static int attrib_location(int program, String name) {
        return GLES20.glGetAttribLocation(program, name);
    }

    public static boolean gl_attrib_ptr(int program, String name, int size, int type,
                                        boolean normalized, int stride, int offset) {
        int location = attrib_location(program, name);
        if (location < 0) {
            return false;
        }

        GLES20.glVertexAttribPointer(location, size, type, normalized, stride, offset);
        GLES20.glEnableVertexAttribArray(location);

        return true;
    }
}
