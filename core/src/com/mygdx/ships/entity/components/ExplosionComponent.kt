package com.mygdx.ships.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class ExplosionComponent : Component, Pool.Poolable {
    var explosion: FloatArray? = null
    var angle: Float = 0f
    var detonated = false

    constructor()
    constructor(explosion: FloatArray, angle: Float) {
        this.explosion = explosion
        this.angle = angle
    }

    operator fun set(explosion: FloatArray, angle: Float) {
        this.explosion = FloatArray(explosion.size)
        System.arraycopy(explosion, 0, this.explosion, 0, explosion.size)
        this.angle = angle
    }

    fun set(ec: ExplosionComponent) {
        val explosion = ec.explosion!!
        this.explosion = FloatArray(explosion.size)
        System.arraycopy(explosion, 0, this.explosion, 0, explosion.size)
        this.angle = ec.angle
    }

    override fun reset() {
        explosion = null
        angle = 0f
        detonated = false
    }
}
