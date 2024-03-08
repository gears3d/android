/* Jordan Justen : gears3d is public domain */
package org.gears3d.gears3d

class GearInfo {
    var teeth = 0
    var inner_radius = 0f
    var outer_radius = 0f
    var width = 0f
    var tooth_depth = 0f
    var angle_rate = 0f
    var angle_adjust = 0f
    var translate: FloatArray
    var color: FloatArray
    var model: FloatArray?
    var angle = 0f
    var num_vertices = 0
    var vertex_buf_offset = 0
    fun setTeeth(teeth: Int): GearInfo {
        this.teeth = teeth
        return this
    }

    fun setInner_radius(inner_radius: Float): GearInfo {
        this.inner_radius = inner_radius
        return this
    }

    fun setOuter_radius(outer_radius: Float): GearInfo {
        this.outer_radius = outer_radius
        return this
    }

    fun setWidth(width: Float): GearInfo {
        this.width = width
        return this
    }

    fun setTooth_depth(tooth_depth: Float): GearInfo {
        this.tooth_depth = tooth_depth
        return this
    }

    fun setAngle_rate(angle_rate: Float): GearInfo {
        this.angle_rate = angle_rate
        return this
    }

    fun setAngle_adjust(angle_adjust: Float): GearInfo {
        this.angle_adjust = angle_adjust
        return this
    }

    fun setTranslate(translate: FloatArray): GearInfo {
        this.translate = translate
        return this
    }

    fun setColor(color: FloatArray): GearInfo {
        this.color = color
        return this
    }

    fun setModel(model: FloatArray?): GearInfo {
        this.model = model
        return this
    }

    fun setAngle(angle: Float): GearInfo {
        this.angle = angle
        return this
    }

    fun setNum_vertices(num_vertices: Int): GearInfo {
        this.num_vertices = num_vertices
        return this
    }

    fun setVertex_buf_offset(vertex_buf_offset: Int): GearInfo {
        this.vertex_buf_offset = vertex_buf_offset
        return this
    }
}
