package org.gears3d.gears3d;

public class GfxMath {
    static float[] frustum(double left, double right, double bottom,
                           double top, double nearVal, double farVal)
    {
        float[] f = new float[16];
        return f;
    }

    static float[] mult_m4m4(float[] m1, float[] m2)
    {
        float[] d = new float[16];
        return d;
    }

    static float[] translate(float x, float y, float z)
    {
        float[] d = new float[16];
        return d;
    }

    static float[] rotate(double angle, double x, double y, double z)
    {
        /* https://www.khronos.org/registry/OpenGL-Refpages/gl2.1/xhtml/glRotate.xml
         *
         * perspective matrix:
         *
         * /                                                          \
         * | x^2*(1-c) + c     x*y*(1-c) - z*s   x*z*(1-c) + y*s   0  |
         * |                                                          |
         * | y*x*(1-c) + z*s   y^2*(1-c) + c     y*z*(1-c) - x*s   0  |
         * |                                                          |
         * | x*z*(1-c) - y*s   y*z*(1-c) + x*s   z^2*(1-c) + c     0  |
         * |                                                          |
         * |       0                  0                 0          1  |
         * \                                                          /
         *
         * c = cos(angle), s = sin(angle)
         *
         */
        double c = Math.cos(angle);
        double omc = 1.0 - c;
        double s = Math.sin(angle);
        float[] mat4 = new float[16];
        mat4[mat4_idx(0, 0)] = (float) ((x * x * omc) + c);
        mat4[mat4_idx(0, 1)] = (float) ((x * y * omc) - z * s);
        mat4[mat4_idx(0, 2)] = (float) ((x * z * omc) + y * s);
        mat4[mat4_idx(1, 0)] = (float) ((y * x * omc) + z * s);
        mat4[mat4_idx(1, 1)] = (float) ((y * y * omc) + c);
        mat4[mat4_idx(1, 2)] = (float) ((y * z * omc) - x * s);
        mat4[mat4_idx(2, 0)] = (float) ((z * x * omc) - y * s);
        mat4[mat4_idx(2, 1)] = (float) ((z * y * omc) + x * s);
        mat4[mat4_idx(2, 2)] = (float) ((z * z * omc) + c);
        mat4[mat4_idx(3, 3)] = 1.0f;
        return mat4;
    }

    private static int mat4_idx(int row, int column)
    {
        /* column major */
        return row + (column << 2);
    }

}
