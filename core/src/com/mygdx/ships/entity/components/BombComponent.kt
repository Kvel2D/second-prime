package com.mygdx.ships.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class BombComponent : Component, Pool.Poolable {
    var explosion: FloatArray? = null
    var charge: Float = 0f
    var chargeTimer: Float = 0f

    constructor()

    constructor(explosion: FloatArray, charge: Float) {
        this.explosion = FloatArray(explosion.size)
        System.arraycopy(explosion, 0, this.explosion, 0, explosion.size)
        this.charge = charge
        this.chargeTimer = charge
    }

    operator fun set(explosion: FloatArray, charge: Float) {
        this.explosion = FloatArray(explosion.size)
        System.arraycopy(explosion, 0, this.explosion, 0, explosion.size)
        this.charge = charge
        this.chargeTimer = charge
    }

    fun set(bc: BombComponent) {
        val explosion = bc.explosion!!
        this.explosion = FloatArray(explosion.size)
        System.arraycopy(explosion, 0, this.explosion, 0, explosion.size)
        this.charge = bc.charge
        this.chargeTimer = bc.charge
    }

    override fun reset() {
        explosion = null
        charge = 0f
        chargeTimer = 0f
    }
}
