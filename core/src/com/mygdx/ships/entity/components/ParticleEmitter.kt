package com.mygdx.ships.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class ParticleEmitter : Component, Pool.Poolable {
    var particle: FloatArray? = null
    var particleDuration: Float = 0f
    var charge: Float = 0f
    var chargeTimer: Float = 0f

    constructor()

    constructor(particle: FloatArray, particleDuration: Float, charge: Float) {
        this.particle = particle
        this.particleDuration = particleDuration
        this.charge = charge
        this.chargeTimer = charge
    }

    operator fun set(particle: FloatArray, particleDuration: Float, charge: Float) {
        this.particle = FloatArray(particle.size)
        System.arraycopy(particle, 0, this.particle, 0, particle.size)
        this.particleDuration = particleDuration
        this.charge = charge
        this.chargeTimer = charge
    }

    fun set(pec: ParticleEmitter) {
        val particle = pec.particle!!
        this.particle = FloatArray(particle.size)
        System.arraycopy(particle, 0, this.particle, 0, particle.size)
        this.particleDuration = pec.particleDuration
        this.charge = pec.charge
        this.chargeTimer = pec.charge
    }

    override fun reset() {
        particle = null
        particleDuration = 0f
        charge = 0f
        chargeTimer = 0f
    }
}
