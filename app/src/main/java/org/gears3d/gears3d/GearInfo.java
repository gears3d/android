/* Jordan Justen : gears3d is public domain */

package org.gears3d.gears3d;

public class GearInfo {

    int teeth;
    float inner_radius, outer_radius, width, tooth_depth;
    float angle_rate;
    float angle_adjust;
    float[] translate;
    float[] color;
    float[] model;
    float angle;
    int num_vertices, vertex_buf_offset;

    public GearInfo setTeeth(int teeth) {
        this.teeth = teeth;
        return this;
    }

    public GearInfo setInner_radius(float inner_radius) {
        this.inner_radius = inner_radius;
        return this;
    }

    public GearInfo setOuter_radius(float outer_radius) {
        this.outer_radius = outer_radius;
        return this;
    }

    public GearInfo setWidth(float width) {
        this.width = width;
        return this;
    }

    public GearInfo setTooth_depth(float tooth_depth) {
        this.tooth_depth = tooth_depth;
        return this;
    }

    public GearInfo setAngle_rate(float angle_rate) {
        this.angle_rate = angle_rate;
        return this;
    }

    public GearInfo setAngle_adjust(float angle_adjust) {
        this.angle_adjust = angle_adjust;
        return this;
    }

    public GearInfo setTranslate(float[] translate) {
        this.translate = translate;
        return this;
    }

    public GearInfo setColor(float[] color) {
        this.color = color;
        return this;
    }

    public GearInfo setModel(float[] model) {
        this.model = model;
        return this;
    }

    public GearInfo setAngle(float angle) {
        this.angle = angle;
        return this;
    }

    public GearInfo setNum_vertices(int num_vertices) {
        this.num_vertices = num_vertices;
        return this;
    }

    public GearInfo setVertex_buf_offset(int vertex_buf_offset) {
        this.vertex_buf_offset = vertex_buf_offset;
        return this;
    }
}
