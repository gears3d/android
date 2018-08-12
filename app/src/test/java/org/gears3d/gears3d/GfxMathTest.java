package org.gears3d.gears3d;

import org.junit.Test;

import static org.junit.Assert.*;

public class GfxMathTest {

    @Test
    public void frustum() {
        float[] f = GfxMath.frustum(-1.0, 1.0, 300, -300, 5.0, 200.0);
        float[] expected =
                { 5.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.01667f, 0.0f, 0.0f, 0.0f, -0.0f,
                  -1.051282f, -1.0f, 0.0f, 0.0f, -10.256411f, 0.0f };
        assertArrayEquals(expected, f, 0.01f);
    }
}
