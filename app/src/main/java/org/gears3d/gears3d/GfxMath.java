package org.gears3d.gears3d;

public class GfxMath {
    static float[] frustum(double left, double right, double bottom,
                           double top, double nearVal, double farVal)
    {
        /* https://www.khronos.org/registry/OpenGL-Refpages/gl2.1/xhtml/glFrustum.xml
         *
         * perspective matrix:
         *
         * /   2 * nearVal                                \
         * |  ------------       0            A      0    |
         * |  right - left                                |
         * |                                              |
         * |                 2 * nearVal                  |
         * |       0        ------------      B      0    |
         * |                top - bottom                  |
         * |                                              |
         * |       0             0            C      D    |
         * |                                              |
         * |       0             0           -1      0    |
         * \                                              /
         *
         *     right + left
         * A = ------------
         *     right - left
         *
         *     top + bottom
         * B = ------------
         *     top - bottom
         *
         *       farVal + nearVal
         * C = - ----------------
         *       farVal - nearVal
         *
         *       2 * farVal * nearVal
         * D = - --------------------
         *         farVal - nearVal
         *
         */
        assert left != right && top != bottom && farVal != nearVal;
        double A = (right + left) / (right - left);
        double B = (top + bottom) / (top - bottom);
        double C = - (farVal + nearVal) / (farVal - nearVal);
        double D = - (2.0 * farVal * nearVal) / (farVal - nearVal);
        float[] mat4 = new float[16];
        mat4[mat4_idx(0, 0)] = (float) ((2.0 * nearVal) / (right - left));
        mat4[mat4_idx(0, 2)] = (float) A;
        mat4[mat4_idx(1, 1)] = (float) ((2.0 * nearVal) / (top - bottom));
        mat4[mat4_idx(1, 2)] = (float) B;
        mat4[mat4_idx(2, 2)] = (float) C;
        mat4[mat4_idx(2, 3)] = (float) D;
        mat4[mat4_idx(3, 2)] = -1.0f;
        return mat4;
    }

    static float[] mult_m4m4(float[] src1, float[] src2)
    {
        int i, j;
        float[] mat4 = new float[16];

        for (i = 0; i < 4; i++) {
            for (j = 0; j < 4; j++) {
                mat4[mat4_idx(i, j)] =
                    src1[mat4_idx(i, 0)] * src2[mat4_idx(0, j)] +
                    src1[mat4_idx(i, 1)] * src2[mat4_idx(1, j)] +
                    src1[mat4_idx(i, 2)] * src2[mat4_idx(2, j)] +
                    src1[mat4_idx(i, 3)] * src2[mat4_idx(3, j)];
            }
        }

        return mat4;
    }

    static float[] translate(float x, float y, float z)
    {
        /* https://www.khronos.org/registry/OpenGL-Refpages/gl2.1/xhtml/glTranslate.xml
         *
         * /              \
         * |  1  0  0  x  |
         * |              |
         * |  0  1  0  y  |
         * |              |
         * |  0  0  1  z  |
         * |              |
         * |  0  0  0  1  |
         * \              /
         *
         */
        float[] mat4 = new float[16];
        mat4[mat4_idx(0, 0)] = 1.0f;
        mat4[mat4_idx(1, 1)] = 1.0f;
        mat4[mat4_idx(2, 2)] = 1.0f;
        mat4[mat4_idx(3, 3)] = 1.0f;
        mat4[mat4_idx(0, 3)] = x;
        mat4[mat4_idx(1, 3)] = y;
        mat4[mat4_idx(2, 3)] = z;
        return mat4;
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
