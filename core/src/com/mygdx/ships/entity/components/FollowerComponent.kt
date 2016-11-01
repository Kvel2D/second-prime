package com.mygdx.ships.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool

class FollowerComponent : Component, Pool.Poolable {
    var following: Boolean = false
    var speed: Vector2 = Vector2(0f, 0f)
    var acceleration: Float = 0f

    constructor()

    constructor(acceleration: Float) {
        this.acceleration = acceleration
    }

    fun set(acceleration: Float) {
        this.acceleration = acceleration
    }

    fun set(fc: FollowerComponent) {
        this.acceleration = fc.acceleration
    }

    override fun reset() {
        following = false
        speed = Vector2(0f, 0f)
        acceleration = 0f
    }
}
