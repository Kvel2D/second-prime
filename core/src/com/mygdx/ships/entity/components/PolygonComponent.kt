package com.mygdx.ships.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import com.mygdx.ships.rotateAround

class PolygonComponent : Component, Pool.Poolable {
    var polygon: FloatArray? = null
    var lowY = 0f
    var highY = 0f
    var leftX = 0f
    var rightX = 0f

    constructor()

    constructor(polygon: FloatArray) {
        this.polygon = FloatArray(polygon.size)
        System.arraycopy(polygon, 0, this.polygon, 0, polygon.size)
        lowY = polygon[1]
        highY = polygon[1]
        leftX = polygon[0]
        rightX = polygon[0]
        var i = 0
        while (i < polygon.size) {
            if (polygon[i] < leftX) leftX = polygon[i]
            if (polygon[i] > rightX) rightX = polygon[i]
            if (polygon[i + 1] < lowY) lowY = polygon[i + 1]
            if (polygon[i + 1] > highY) highY = polygon[i + 1]
            i += 2
        }
    }

    fun set(polygon: FloatArray) {
        this.polygon = FloatArray(polygon.size)
        System.arraycopy(polygon, 0, this.polygon, 0, polygon.size)
        lowY = polygon[1]
        highY = polygon[1]
        leftX = polygon[0]
        rightX = polygon[0]
        var i = 0
        while (i < polygon.size) {
            if (polygon[i] < leftX) leftX = polygon[i]
            if (polygon[i] > rightX) rightX = polygon[i]
            if (polygon[i + 1] < lowY) lowY = polygon[i + 1]
            if (polygon[i + 1] > highY) highY = polygon[i + 1]
            i += 2
        }
    }

    fun set(pc: PolygonComponent) {
        val polygon = pc.polygon!!
        this.polygon = FloatArray(polygon.size)
        System.arraycopy(polygon, 0, this.polygon, 0, polygon.size)
        lowY = polygon[1]
        highY = polygon[1]
        leftX = polygon[0]
        rightX = polygon[0]
        var i = 0
        while (i < polygon.size) {
            if (polygon[i] < leftX) leftX = polygon[i]
            if (polygon[i] > rightX) rightX = polygon[i]
            if (polygon[i + 1] < lowY) lowY = polygon[i + 1]
            if (polygon[i + 1] > highY) highY = polygon[i + 1]
            i += 2
        }
    }

    override fun reset() {
        polygon = null
        lowY = 0f
        highY = 0f
        leftX = 0f
        rightX = 0f
    }
}
