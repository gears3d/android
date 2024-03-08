/* Jordan Justen : gears3d is public domain */
package org.gears3d.gears3d

import java.nio.FloatBuffer

object VertBuf {
    fun add_vert(verts: FloatBuffer, x: Double, y: Double, z: Float,
                 normal: FloatArray) {
        assert(verts.remaining() >= 6)
        assert(normal.size == 3)
        val v_n = floatArrayOf(x.toFloat(), y.toFloat(), z,
                normal[0], normal[1], normal[2])
        val start_pos = verts.position()
        verts.put(v_n)
        assert(verts.position() == start_pos + FLOATS_PER_VERT)
    }

    fun add_vert_mult_xy_norm(verts: FloatBuffer, x: Double, y: Double,
                              z: Float, mult: Float) {
        val norm_tmp = floatArrayOf((mult * x - x).toFloat(), (mult * y - y).toFloat(), 0.0f
        )
        add_vert(verts, x, y, z, norm_tmp)
    }

    fun tooth(verts: FloatBuffer, inner_radius: Float,
              outer_radius: Float, width: Float, teeth: Int,
              tooth_depth: Float, tooth_num: Int) {
        val r1 = outer_radius - tooth_depth / 2.0f
        val r2 = outer_radius + tooth_depth / 2.0f
        val da = Math.PI / teeth / 2.0
        val pta = 2.0 * Math.PI / teeth
        val ta = tooth_num * pta
        val half_width = 0.5f * width
        val dcos = doubleArrayOf(Math.cos(ta), Math.cos(ta + da),
                Math.cos(ta + 2 * da), Math.cos(ta + 3 * da),
                Math.cos(ta + 4 * da))
        val dsin = doubleArrayOf(Math.sin(ta), Math.sin(ta + da),
                Math.sin(ta + 2 * da), Math.sin(ta + 3 * da),
                Math.sin(ta + 4 * da))
        val last_tooth = tooth_num < 0 || tooth_num == teeth - 1
        val start_pos = verts.position()
        assert(tooth_num >= -1 && tooth_num < teeth)
        val pos_z = floatArrayOf(0.0f, 0.0f, 1.0f)
        val neg_z = floatArrayOf(0.0f, 0.0f, -1.0f)
        val norm_tmp = FloatArray(3)

        /* front face of tooth */add_vert(verts, dcos[1] * r2, dsin[1] * r2, half_width, pos_z)
        add_vert(verts, dcos[2] * r2, dsin[2] * r2, half_width, pos_z)
        add_vert(verts, dcos[0] * r1, dsin[0] * r1, half_width, pos_z)
        add_vert(verts, dcos[3] * r1, dsin[3] * r1, half_width, pos_z)

        /* front face of gear */add_vert(verts, dcos[4] * r1, dsin[4] * r1, half_width, pos_z)
        add_vert(verts, dcos[4] * r1, dsin[4] * r1, half_width, pos_z)
        add_vert(verts, dcos[0] * r1, dsin[0] * r1, half_width, pos_z)
        add_vert(verts, dcos[4] * inner_radius, dsin[4] * inner_radius, half_width, pos_z)
        add_vert(verts, dcos[0] * inner_radius, dsin[0] * inner_radius, half_width, pos_z)

        /* inner cylinder */add_vert_mult_xy_norm(verts, dcos[4] * inner_radius, dsin[4] * inner_radius, half_width, 0.5f)
        add_vert_mult_xy_norm(verts, dcos[0] * inner_radius, dsin[0] * inner_radius, half_width, 0.5f)
        add_vert_mult_xy_norm(verts, dcos[4] * inner_radius, dsin[4] * inner_radius, -half_width, 0.5f)
        add_vert_mult_xy_norm(verts, dcos[0] * inner_radius, dsin[0] * inner_radius, -half_width, 0.5f)

        /* back face of gear (first 2 are degenerate to reset normal) */add_vert(verts, dcos[4] * inner_radius, dsin[4] * inner_radius, -half_width, neg_z)
        add_vert(verts, dcos[0] * inner_radius, dsin[0] * inner_radius, -half_width, neg_z)
        add_vert(verts, dcos[4] * r1, dsin[4] * r1, -half_width, neg_z)
        add_vert(verts, dcos[0] * r1, dsin[0] * r1, -half_width, neg_z)
        add_vert(verts, dcos[3] * r1, dsin[3] * r1, -half_width, neg_z)

        /* back face of tooth */add_vert(verts, dcos[1] * r2, dsin[1] * r2, -half_width, neg_z)
        add_vert(verts, dcos[2] * r2, dsin[2] * r2, -half_width, neg_z)

        /* two degenerate triangles to jump to drawing the outer edge of gear */add_vert_mult_xy_norm(verts, dcos[2] * r2, dsin[2] * r2, -half_width, 2.0f)
        add_vert_mult_xy_norm(verts, dcos[4] * r1, dsin[4] * r1, -half_width, 2.0f)

        /* tooth recess outer edge */add_vert_mult_xy_norm(verts, dcos[4] * r1, dsin[4] * r1, -half_width, 2.0f)
        add_vert_mult_xy_norm(verts, dcos[4] * r1, dsin[4] * r1, half_width, 2.0f)
        add_vert_mult_xy_norm(verts, dcos[3] * r1, dsin[3] * r1, -half_width, 2.0f)
        add_vert_mult_xy_norm(verts, dcos[3] * r1, dsin[3] * r1, half_width, 2.0f)

        /* tooth leading edge (first 2 are degenerate to reset normal) */norm_tmp[0] = (dsin[3] * r1 - dsin[2] * r2).toFloat()
        norm_tmp[1] = (-dcos[3] * r1 + dcos[2] * r2).toFloat()
        norm_tmp[2] = 0.0f
        add_vert(verts, dcos[3] * r1, dsin[3] * r1, -half_width, norm_tmp)
        add_vert(verts, dcos[3] * r1, dsin[3] * r1, half_width, norm_tmp)
        add_vert(verts, dcos[2] * r2, dsin[2] * r2, -half_width, norm_tmp)
        add_vert(verts, dcos[2] * r2, dsin[2] * r2, half_width, norm_tmp)

        /* tooth top edge (first 2 are degenerate to reset normal) */add_vert_mult_xy_norm(verts, dcos[2] * r2, dsin[2] * r2, -half_width, 2.0f)
        add_vert_mult_xy_norm(verts, dcos[2] * r2, dsin[2] * r2, half_width, 2.0f)
        add_vert_mult_xy_norm(verts, dcos[1] * r2, dsin[1] * r2, -half_width, 2.0f)
        add_vert_mult_xy_norm(verts, dcos[1] * r2, dsin[1] * r2, half_width, 2.0f)

        /* tooth trailing edge (first 2 are degenerate to reset normal) */norm_tmp[0] = (-dsin[0] * r1 + dsin[1] * r2).toFloat()
        norm_tmp[1] = (dcos[0] * r1 - dcos[1] * r2).toFloat()
        norm_tmp[2] = 0.0f
        add_vert(verts, dcos[1] * r2, dsin[1] * r2, -half_width, norm_tmp)
        add_vert(verts, dcos[1] * r2, dsin[1] * r2, half_width, norm_tmp)
        add_vert(verts, dcos[0] * r1, dsin[0] * r1, -half_width, norm_tmp)
        add_vert(verts, dcos[0] * r1, dsin[0] * r1, half_width, norm_tmp)
        if (!last_tooth) {
            /* two degenerate triangles to jump to drawing the next tooth */
            add_vert(verts, dcos[0] * r1, dsin[0] * r1, half_width, pos_z)
            add_vert(verts, Math.cos(ta + da + pta) * r2,
                    Math.sin(ta + da + pta) * r2, half_width, pos_z)
        }
        val floats_added = verts.position() - start_pos
        val floats_expected = 6 * if (last_tooth) TOOTH_VERTS - 2 else TOOTH_VERTS
        assert(floats_added == floats_expected)
    }

    fun tooth_vertex_count(): Int {
        return TOOTH_VERTS - 2
    }

    fun fill_tooth_vertices(verts: FloatBuffer, inner_radius: Float,
                            outer_radius: Float, width: Float, teeth: Int,
                            tooth_depth: Float) {
        tooth(verts, inner_radius, outer_radius, width, teeth, tooth_depth, -1)
    }

    fun gear_vertex_count(teeth: Int): Int {
        assert(teeth > 0)
        return TOOTH_VERTS * teeth - 2
    }

    fun fill_gear_vertices(verts: FloatBuffer, inner_radius: Float,
                           outer_radius: Float, width: Float, teeth: Int,
                           tooth_depth: Float) {
        val start_pos = verts.position()
        for (i in 0 until teeth) {
            tooth(verts, inner_radius, outer_radius, width, teeth,
                    tooth_depth, i)
        }
        val floats_added = verts.position() - start_pos
        val floats_expected = gear_vertex_count(teeth) * FLOATS_PER_VERT
        assert(floats_added == floats_expected)
    }

    const val FLOATS_PER_VERT = 6
    const val TOOTH_VERTS = 40
}
