package com.mygdx.ships.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class Transform : Component, Pool.Poolable {
    var x: Float = 0f
    var y: Float = 0f
    var angle: Float = 0f
    var scale: Float = 0f
    var z: Int = 0

    constructor()

    constructor(x: Float, y: Float, z: Int, angle: Float, scale: Float) {
        this.x = x
        this.y = y
        this.z = z
        this.angle = angle
        this.scale = scale
    }

    operator fun set(x: Float, y: Float, z: Int, angle: Float, scale: Float) {
        this.x = x
        this.y = y
        this.z = z
        this.angle = angle
        this.scale = scale
    }

    fun set(tc: Transform) {
        this.x = tc.x
        this.y = tc.y
        this.z = tc.z
        this.angle = tc.angle
        this.scale = tc.scale
    }

    override fun reset() {
        x = 0f
        y = 0f
        angle = 0f
        scale = 1f
        z = 0
    }
}
