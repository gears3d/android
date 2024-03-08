package org.gears3d.gears3d

object GfxMath {
    fun frustum(left: Double, right: Double, bottom: Double,
                top: Double, nearVal: Double, farVal: Double): FloatArray {
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
        assert(left != right && top != bottom && farVal != nearVal)
        val A = (right + left) / (right - left)
        val B = (top + bottom) / (top - bottom)
        val C = -(farVal + nearVal) / (farVal - nearVal)
        val D = -(2.0 * farVal * nearVal) / (farVal - nearVal)
        val mat4 = FloatArray(16)
        mat4[mat4_idx(0, 0)] = (2.0 * nearVal / (right - left)).toFloat()
        mat4[mat4_idx(0, 2)] = A.toFloat()
        mat4[mat4_idx(1, 1)] = (2.0 * nearVal / (top - bottom)).toFloat()
        mat4[mat4_idx(1, 2)] = B.toFloat()
        mat4[mat4_idx(2, 2)] = C.toFloat()
        mat4[mat4_idx(2, 3)] = D.toFloat()
        mat4[mat4_idx(3, 2)] = -1.0f
        return mat4
    }

    fun mult_m4m4(src1: FloatArray?, src2: FloatArray?): FloatArray {
        var i: Int
        var j: Int
        val mat4 = FloatArray(16)
        i = 0
        while (i < 4) {
            j = 0
            while (j < 4) {
                mat4[mat4_idx(i, j)] = src1!![mat4_idx(i, 0)] * src2!![mat4_idx(0, j)] + src1[mat4_idx(i, 1)] * src2[mat4_idx(1, j)] + src1[mat4_idx(i, 2)] * src2[mat4_idx(2, j)] + src1[mat4_idx(i, 3)] * src2[mat4_idx(3, j)]
                j++
            }
            i++
        }
        return mat4
    }

    fun translate(x: Float, y: Float, z: Float): FloatArray {
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
        val mat4 = FloatArray(16)
        mat4[mat4_idx(0, 0)] = 1.0f
        mat4[mat4_idx(1, 1)] = 1.0f
        mat4[mat4_idx(2, 2)] = 1.0f
        mat4[mat4_idx(3, 3)] = 1.0f
        mat4[mat4_idx(0, 3)] = x
        mat4[mat4_idx(1, 3)] = y
        mat4[mat4_idx(2, 3)] = z
        return mat4
    }

    fun rotate(angle: Double, x: Double, y: Double, z: Double): FloatArray {
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
        val c = Math.cos(angle)
        val omc = 1.0 - c
        val s = Math.sin(angle)
        val mat4 = FloatArray(16)
        mat4[mat4_idx(0, 0)] = (x * x * omc + c).toFloat()
        mat4[mat4_idx(0, 1)] = (x * y * omc - z * s).toFloat()
        mat4[mat4_idx(0, 2)] = (x * z * omc + y * s).toFloat()
        mat4[mat4_idx(1, 0)] = (y * x * omc + z * s).toFloat()
        mat4[mat4_idx(1, 1)] = (y * y * omc + c).toFloat()
        mat4[mat4_idx(1, 2)] = (y * z * omc - x * s).toFloat()
        mat4[mat4_idx(2, 0)] = (z * x * omc - y * s).toFloat()
        mat4[mat4_idx(2, 1)] = (z * y * omc + x * s).toFloat()
        mat4[mat4_idx(2, 2)] = (z * z * omc + c).toFloat()
        mat4[mat4_idx(3, 3)] = 1.0f
        return mat4
    }

    private fun mat4_idx(row: Int, column: Int): Int {
        /* column major */
        return row + (column shl 2)
    }
}
