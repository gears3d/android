/* Jordan Justen : gears3d is public domain */

package org.gears3d.gears3d

import org.junit.Assert
import org.junit.Test

class GfxMathTest {
    @Test
    fun frustum() {
        val f = GfxMath.frustum(-1.0, 1.0, 300.0, -300.0, 5.0, 200.0)
        val expected = floatArrayOf(5.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.01667f,
                                    0.0f, 0.0f, 0.0f, -0.0f, -1.051282f,
                                    -1.0f, 0.0f, 0.0f, -10.256411f, 0.0f)
        Assert.assertArrayEquals(expected, f, 0.01f)
    }

    @Test
    fun mult_m4m4() {
        val m1 = floatArrayOf(0.1f, -0.5f, -1.0f, 2.5f, -0.5f, 3.0f, -10f,
                              7.0f, 4.2f, 6.3f, -7.1f, -8.8f, 1.8f, -2.2f,
                              3.1f, -2.1f)
        val m2 = floatArrayOf(5.3f, 0.3f, -1.4f, 5.6f, 3.3f, -2.4f, 7.1f,
                              -2.9f, -6.4f, 2.3f, 8.6f, -4.4f, 6.6f, 3.7f,
                              1.6f, -9.6f)
        val m = GfxMath.mult_m4m4(m1, m2)
        val expected =
            floatArrayOf(4.58f, -22.889999f, 18.999998f, 15.910001f,
                         26.129997f, 42.259998f, -38.699997f, -64.940002f,
                         26.409998f, 73.960007f, -91.300003f, -66.340012f,
                         -11.750001f, 39.0f, -84.720001f, 48.48f)
        Assert.assertArrayEquals(expected, m, 0.01f)
    }

    @Test
    fun translate() {
        val m = GfxMath.translate(-1.0f, 2.0f, 3.0f)
        val expected = floatArrayOf(1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                                    0.0f, 0.0f, 0.0f, 1.0f, 0.0f, -1.0f, 2.0f,
                                    3.0f, 1.0f)
        Assert.assertArrayEquals(expected, m, 0.01f)
    }

    @Test
    fun rotate() {
        val m = GfxMath.rotate(1.0, -1.0, 2.0, 3.0)
        val expected = floatArrayOf(1.0f, 1.605018f, -3.062035f, 0.0f,
                                    -3.443808f, 2.379093f, 1.916715f, 0.0f,
                                    0.303849f, 3.599657f, 4.677582f, 0.0f,
                                    0.0f, 0.0f, 0.0f, 1.0f)
        Assert.assertArrayEquals(expected, m, 0.01f)
    }
}
