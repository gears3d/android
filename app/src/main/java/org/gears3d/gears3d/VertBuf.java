/* Jordan Justen : gears3d is public domain */

package org.gears3d.gears3d;

import java.nio.FloatBuffer;

public class VertBuf {

    static void add_vert(FloatBuffer verts, double x, double y, float z,
                         float[] normal)
    {
        assert verts.remaining() >= 6;
        assert normal.length == 3;
        float[] v_n = {
            (float) x, (float) y, z,
            normal[0], normal[1], normal[2],
        };

        int start_pos = verts.position();
        verts.put(v_n);
        assert verts.position() == start_pos + FLOATS_PER_VERT;
    }

    static void add_vert_mult_xy_norm(FloatBuffer verts, double x, double y,
                                      float z, float mult)
    {
        float[] norm_tmp = {
            (float) (mult * x - x), (float) (mult * y - y), 0.0f
        };
        add_vert(verts, x, y, z, norm_tmp);
    }

    static void tooth(FloatBuffer verts, float inner_radius,
                      float outer_radius, float width, int teeth,
                      float tooth_depth, int tooth_num)
    {
        float r0 = inner_radius;
        float r1 = outer_radius - tooth_depth / 2.0f;
        float r2 = outer_radius + tooth_depth / 2.0f;
        double da = Math.PI / teeth / 2.0;
        double pta = 2.0 * Math.PI / teeth;
        double ta = tooth_num * pta;
        float half_width = 0.5f * width;
        double[] dcos = { Math.cos(ta), Math.cos(ta + da),
                          Math.cos(ta + 2 * da), Math.cos(ta + 3 * da),
                          Math.cos(ta + 4 * da) };
        double[] dsin = { Math.sin(ta), Math.sin(ta + da),
                          Math.sin(ta + 2 * da), Math.sin(ta + 3 * da),
                          Math.sin(ta + 4 * da) };
        boolean last_tooth = tooth_num < 0 || tooth_num == teeth - 1;
        int start_pos = verts.position();
        assert tooth_num >= -1 && tooth_num < teeth;

        float[] pos_z = { 0.0f, 0.0f, 1.0f };
        float[] neg_z = { 0.0f, 0.0f, -1.0f };
        float[] norm_tmp = new float[3];

        /* front face of tooth */
        add_vert(verts, dcos[1] * r2, dsin[1] * r2, half_width, pos_z);
        add_vert(verts, dcos[2] * r2, dsin[2] * r2, half_width, pos_z);
        add_vert(verts, dcos[0] * r1, dsin[0] * r1, half_width, pos_z);
        add_vert(verts, dcos[3] * r1, dsin[3] * r1, half_width, pos_z);

        /* front face of gear */
        add_vert(verts, dcos[4] * r1, dsin[4] * r1, half_width, pos_z);
        add_vert(verts, dcos[4] * r1, dsin[4] * r1, half_width, pos_z);
        add_vert(verts, dcos[0] * r1, dsin[0] * r1, half_width, pos_z);
        add_vert(verts, dcos[4] * r0, dsin[4] * r0, half_width, pos_z);
        add_vert(verts, dcos[0] * r0, dsin[0] * r0, half_width, pos_z);

        /* inner cylinder */
        add_vert_mult_xy_norm(verts, dcos[4] * r0, dsin[4] * r0, half_width, 0.5f);
        add_vert_mult_xy_norm(verts, dcos[0] * r0, dsin[0] * r0, half_width, 0.5f);
        add_vert_mult_xy_norm(verts, dcos[4] * r0, dsin[4] * r0, -half_width, 0.5f);
        add_vert_mult_xy_norm(verts, dcos[0] * r0, dsin[0] * r0, -half_width, 0.5f);

        /* back face of gear (first 2 are degenerate to reset normal) */
        add_vert(verts, dcos[4] * r0, dsin[4] * r0, -half_width, neg_z);
        add_vert(verts, dcos[0] * r0, dsin[0] * r0, -half_width, neg_z);
        add_vert(verts, dcos[4] * r1, dsin[4] * r1, -half_width, neg_z);
        add_vert(verts, dcos[0] * r1, dsin[0] * r1, -half_width, neg_z);
        add_vert(verts, dcos[3] * r1, dsin[3] * r1, -half_width, neg_z);

        /* back face of tooth */
        add_vert(verts, dcos[1] * r2, dsin[1] * r2, -half_width, neg_z);
        add_vert(verts, dcos[2] * r2, dsin[2] * r2, -half_width, neg_z);

        /* two degenerate triangles to jump to drawing the outer edge of gear */
        add_vert_mult_xy_norm(verts, dcos[2] * r2, dsin[2] * r2, -half_width, 2.0f);
        add_vert_mult_xy_norm(verts, dcos[4] * r1, dsin[4] * r1, -half_width, 2.0f);

        /* tooth recess outer edge */
        add_vert_mult_xy_norm(verts, dcos[4] * r1, dsin[4] * r1, -half_width, 2.0f);
        add_vert_mult_xy_norm(verts, dcos[4] * r1, dsin[4] * r1, half_width, 2.0f);
        add_vert_mult_xy_norm(verts, dcos[3] * r1, dsin[3] * r1, -half_width, 2.0f);
        add_vert_mult_xy_norm(verts, dcos[3] * r1, dsin[3] * r1, half_width, 2.0f);

        /* tooth leading edge (first 2 are degenerate to reset normal) */
        norm_tmp[0] = (float) ( dsin[3] * r1 - dsin[2] * r2);
        norm_tmp[1] = (float) (-dcos[3] * r1 + dcos[2] * r2);
        norm_tmp[2] = 0.0f;
        add_vert(verts, dcos[3] * r1, dsin[3] * r1, -half_width, norm_tmp);
        add_vert(verts, dcos[3] * r1, dsin[3] * r1, half_width, norm_tmp);
        add_vert(verts, dcos[2] * r2, dsin[2] * r2, -half_width, norm_tmp);
        add_vert(verts, dcos[2] * r2, dsin[2] * r2, half_width, norm_tmp);

        /* tooth top edge (first 2 are degenerate to reset normal) */
        add_vert_mult_xy_norm(verts, dcos[2] * r2, dsin[2] * r2, -half_width, 2.0f);
        add_vert_mult_xy_norm(verts, dcos[2] * r2, dsin[2] * r2, half_width, 2.0f);
        add_vert_mult_xy_norm(verts, dcos[1] * r2, dsin[1] * r2, -half_width, 2.0f);
        add_vert_mult_xy_norm(verts, dcos[1] * r2, dsin[1] * r2, half_width, 2.0f);

        /* tooth trailing edge (first 2 are degenerate to reset normal) */
        norm_tmp[0] = (float) (-dsin[0] * r1 + dsin[1] * r2);
        norm_tmp[1] = (float) ( dcos[0] * r1 - dcos[1] * r2);
        norm_tmp[2] = 0.0f;
        add_vert(verts, dcos[1] * r2, dsin[1] * r2, -half_width, norm_tmp);
        add_vert(verts, dcos[1] * r2, dsin[1] * r2, half_width, norm_tmp);
        add_vert(verts, dcos[0] * r1, dsin[0] * r1, -half_width, norm_tmp);
        add_vert(verts, dcos[0] * r1, dsin[0] * r1, half_width, norm_tmp);

        if (!last_tooth) {
            /* two degenerate triangles to jump to drawing the next tooth */
            add_vert(verts, dcos[0] * r1, dsin[0] * r1, half_width, pos_z);
            add_vert(verts, Math.cos(ta + da + pta) * r2,
                     Math.sin(ta + da + pta) * r2, half_width, pos_z);
        }

        int floats_added = verts.position() - start_pos;
        int floats_expected = 6 * (last_tooth ? TOOTH_VERTS - 2 : TOOTH_VERTS);
        assert floats_added == floats_expected;
    }

    static int tooth_vertex_count()
    {
        return TOOTH_VERTS - 2;
    }

    static void fill_tooth_vertices(FloatBuffer verts, float inner_radius,
                                    float outer_radius, float width, int teeth,
                                    float tooth_depth)
    {
        tooth(verts, inner_radius, outer_radius, width, teeth, tooth_depth, -1);
    }

    static int gear_vertex_count(int teeth)
    {
        assert teeth > 0;
        return (TOOTH_VERTS * teeth) - 2;
    }

    static void fill_gear_vertices(FloatBuffer verts, float inner_radius,
                                   float outer_radius, float width, int teeth,
                                   float tooth_depth)
    {
        int start_pos = verts.position();

        for (int i = 0; i < teeth; i++) {
            tooth(verts, inner_radius, outer_radius, width, teeth,
                  tooth_depth, i);
        }

        int floats_added = verts.position() - start_pos;
        int floats_expected = gear_vertex_count(teeth) * FLOATS_PER_VERT;
        assert floats_added == floats_expected;
    }

    static final int FLOATS_PER_VERT = 6;

    static final int TOOTH_VERTS = 40;

}
