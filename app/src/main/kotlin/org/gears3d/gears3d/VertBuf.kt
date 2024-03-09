/* Jordan Justen : gears3d is public domain */
package org.gears3d.gears3d

import java.nio.FloatBuffer
import kotlin.math.cos
import kotlin.math.sin

object VertBuf {
    private fun add_vert(verts: FloatBuffer, x: Double, y: Double, z: Float,
                         normal: Array<Float>) {
        assert(verts.remaining() >= 6)
        assert(normal.size == 3)
        val vn = floatArrayOf(x.toFloat(), y.toFloat(), z,
                normal[0], normal[1], normal[2])
        val start = verts.position()
        verts.put(vn)
        assert(verts.position() == start + FLOATS_PER_VERT)
    }

    private fun add_vert_mult_xy_norm(verts: FloatBuffer, x: Double, y: Double,
                                      z: Float, mult: Float) {
        val norm = arrayOf((mult * x - x).toFloat(),
                           (mult * y - y).toFloat(), 0.0f)
        add_vert(verts, x, y, z, norm)
    }

    private fun tooth(verts: FloatBuffer, innerRadius: Float,
                      outerRadius: Float, width: Float, teeth: Int,
                      toothDepth: Float, toothNum: Int) {
        val r1 = outerRadius - toothDepth / 2.0f
        val r2 = outerRadius + toothDepth / 2.0f
        val da = Math.PI / teeth / 2.0
        val pta = 2.0 * Math.PI / teeth
        val ta = toothNum * pta
        val halfW = 0.5f * width
        val dcos = doubleArrayOf(cos(ta), cos(ta + da),
                cos(ta + 2 * da), cos(ta + 3 * da),
                cos(ta + 4 * da))
        val dsin = doubleArrayOf(sin(ta), sin(ta + da),
                sin(ta + 2 * da), sin(ta + 3 * da),
                sin(ta + 4 * da))
        val lastTooth = toothNum < 0 || toothNum == teeth - 1
        val start = verts.position()
        assert(toothNum >= -1 && toothNum < teeth)
        val posZ = arrayOf(0.0f, 0.0f, 1.0f)
        val negZ = arrayOf(0.0f, 0.0f, -1.0f)
        val norm = Array(3) { 0.0f }

        /* front face of tooth */add_vert(verts, dcos[1] * r2, dsin[1] * r2, halfW, posZ)
        add_vert(verts, dcos[2] * r2, dsin[2] * r2, halfW, posZ)
        add_vert(verts, dcos[0] * r1, dsin[0] * r1, halfW, posZ)
        add_vert(verts, dcos[3] * r1, dsin[3] * r1, halfW, posZ)

        /* front face of gear */add_vert(verts, dcos[4] * r1, dsin[4] * r1, halfW, posZ)
        add_vert(verts, dcos[4] * r1, dsin[4] * r1, halfW, posZ)
        add_vert(verts, dcos[0] * r1, dsin[0] * r1, halfW, posZ)
        add_vert(verts, dcos[4] * innerRadius, dsin[4] * innerRadius, halfW, posZ)
        add_vert(verts, dcos[0] * innerRadius, dsin[0] * innerRadius, halfW, posZ)

        /* inner cylinder */add_vert_mult_xy_norm(verts, dcos[4] * innerRadius, dsin[4] * innerRadius, halfW, 0.5f)
        add_vert_mult_xy_norm(verts, dcos[0] * innerRadius, dsin[0] * innerRadius, halfW, 0.5f)
        add_vert_mult_xy_norm(verts, dcos[4] * innerRadius, dsin[4] * innerRadius, -halfW, 0.5f)
        add_vert_mult_xy_norm(verts, dcos[0] * innerRadius, dsin[0] * innerRadius, -halfW, 0.5f)

        /* back face of gear (first 2 are degenerate to reset normal) */add_vert(verts, dcos[4] * innerRadius, dsin[4] * innerRadius, -halfW, negZ)
        add_vert(verts, dcos[0] * innerRadius, dsin[0] * innerRadius, -halfW, negZ)
        add_vert(verts, dcos[4] * r1, dsin[4] * r1, -halfW, negZ)
        add_vert(verts, dcos[0] * r1, dsin[0] * r1, -halfW, negZ)
        add_vert(verts, dcos[3] * r1, dsin[3] * r1, -halfW, negZ)

        /* back face of tooth */add_vert(verts, dcos[1] * r2, dsin[1] * r2, -halfW, negZ)
        add_vert(verts, dcos[2] * r2, dsin[2] * r2, -halfW, negZ)

        /* two degenerate triangles to jump to drawing the outer edge of gear */add_vert_mult_xy_norm(verts, dcos[2] * r2, dsin[2] * r2, -halfW, 2.0f)
        add_vert_mult_xy_norm(verts, dcos[4] * r1, dsin[4] * r1, -halfW, 2.0f)

        /* tooth recess outer edge */add_vert_mult_xy_norm(verts, dcos[4] * r1, dsin[4] * r1, -halfW, 2.0f)
        add_vert_mult_xy_norm(verts, dcos[4] * r1, dsin[4] * r1, halfW, 2.0f)
        add_vert_mult_xy_norm(verts, dcos[3] * r1, dsin[3] * r1, -halfW, 2.0f)
        add_vert_mult_xy_norm(verts, dcos[3] * r1, dsin[3] * r1, halfW, 2.0f)

        /* tooth leading edge (first 2 are degenerate to reset normal) */norm[0] = (dsin[3] * r1 - dsin[2] * r2).toFloat()
        norm[1] = (-dcos[3] * r1 + dcos[2] * r2).toFloat()
        norm[2] = 0.0f
        add_vert(verts, dcos[3] * r1, dsin[3] * r1, -halfW, norm)
        add_vert(verts, dcos[3] * r1, dsin[3] * r1, halfW, norm)
        add_vert(verts, dcos[2] * r2, dsin[2] * r2, -halfW, norm)
        add_vert(verts, dcos[2] * r2, dsin[2] * r2, halfW, norm)

        /* tooth top edge (first 2 are degenerate to reset normal) */add_vert_mult_xy_norm(verts, dcos[2] * r2, dsin[2] * r2, -halfW, 2.0f)
        add_vert_mult_xy_norm(verts, dcos[2] * r2, dsin[2] * r2, halfW, 2.0f)
        add_vert_mult_xy_norm(verts, dcos[1] * r2, dsin[1] * r2, -halfW, 2.0f)
        add_vert_mult_xy_norm(verts, dcos[1] * r2, dsin[1] * r2, halfW, 2.0f)

        /* tooth trailing edge (first 2 are degenerate to reset normal) */norm[0] = (-dsin[0] * r1 + dsin[1] * r2).toFloat()
        norm[1] = (dcos[0] * r1 - dcos[1] * r2).toFloat()
        norm[2] = 0.0f
        add_vert(verts, dcos[1] * r2, dsin[1] * r2, -halfW, norm)
        add_vert(verts, dcos[1] * r2, dsin[1] * r2, halfW, norm)
        add_vert(verts, dcos[0] * r1, dsin[0] * r1, -halfW, norm)
        add_vert(verts, dcos[0] * r1, dsin[0] * r1, halfW, norm)
        if (!lastTooth) {
            /* two degenerate triangles to jump to drawing the next tooth */
            add_vert(verts, dcos[0] * r1, dsin[0] * r1, halfW, posZ)
            add_vert(verts, cos(ta + da + pta) * r2,
                     sin(ta + da + pta) * r2, halfW, posZ)
        }
        val floatsAdded = verts.position() - start
        val floatsExpected = 6 * if (lastTooth) TOOTH_VERTS - 2 else TOOTH_VERTS
        assert(floatsAdded == floatsExpected)
    }

    fun tooth_vertex_count(): Int {
        return TOOTH_VERTS - 2
    }

    fun fill_tooth_vertices(verts: FloatBuffer, innerRadius: Float,
                            outerRadius: Float, width: Float, teeth: Int,
                            toothDepth: Float) {
        tooth(verts, innerRadius, outerRadius, width, teeth, toothDepth, -1)
    }

    fun gear_vertex_count(teeth: Int): Int {
        assert(teeth > 0)
        return TOOTH_VERTS * teeth - 2
    }

    fun fill_gear_vertices(verts: FloatBuffer, innerRadius: Float,
                           outerRadius: Float, width: Float, teeth: Int,
                           toothDepth: Float) {
        val start = verts.position()
        for (i in 0 until teeth) {
            tooth(verts, innerRadius, outerRadius, width, teeth,
                    toothDepth, i)
        }
        val floatsAdded = verts.position() - start
        val floatsExpected = gear_vertex_count(teeth) * FLOATS_PER_VERT
        assert(floatsAdded == floatsExpected)
    }

    const val FLOATS_PER_VERT = 6
    const val TOOTH_VERTS = 40
}
