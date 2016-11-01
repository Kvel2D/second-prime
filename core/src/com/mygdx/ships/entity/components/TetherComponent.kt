package com.mygdx.ships.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool

class TetherComponent : Component, Pool.Poolable {
    var entity: Entity? = null
    var xOffset: Float = 0f
    var yOffset: Float = 0f
    var angleOffset: Float = 0f
    var rotated: Boolean = false

    constructor()

    constructor(entity: Entity, xOffset: Float, yOffset: Float, angleOffset: Float, rotated: Boolean) {
        this.entity = entity
        this.xOffset = xOffset
        this.yOffset = yOffset
        this.angleOffset = angleOffset
        this.rotated = rotated
    }

    operator fun set(entity: Entity, xOffset: Float, yOffset: Float, angleOffset: Float, rotated: Boolean) {
        this.entity = entity
        this.xOffset = xOffset
        this.yOffset = yOffset
        this.angleOffset = angleOffset
        this.rotated = rotated
    }

    fun set(tc: TetherComponent) {
        this.entity = tc.entity
        this.xOffset = tc.xOffset
        this.yOffset = tc.yOffset
        this.angleOffset = tc.angleOffset
        this.rotated = tc.rotated
    }

    override fun reset() {
        entity = null
        xOffset = 0f
        yOffset = 0f
        angleOffset = 0f
        rotated = false
    }
}
