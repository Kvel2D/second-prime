package com.mygdx.ships.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Pool

class WeaponComponent : Component, Pool.Poolable {
    var projectileBody: FloatArray? = null
    var projectileVisual: FloatArray? = null
    var projectileExplosion: FloatArray? = null
    var color: Color? = null
    var particleDuration: Float = 0f
    var charge: Float = 0f
    var chargeTimer: Float = 0f
    var projectileForce: Float = 0f

    constructor()

    constructor(projectileVisual: FloatArray,
                projectileBody: FloatArray,
                projectileExplosion: FloatArray,
                particleDuration: Float,
                projectileForce: Float,
                charge: Float,
                color: Color) {
        this.projectileVisual = projectileVisual
        this.projectileBody = projectileBody
        this.projectileExplosion = projectileExplosion
        this.particleDuration = particleDuration
        this.charge = charge
        this.chargeTimer = charge
        this.projectileForce = projectileForce
        this.color = color
    }

    operator fun set(projectileVisual: FloatArray,
                     projectileBody: FloatArray,
                     projectileExplosion: FloatArray,
                     particleDuration: Float,
                     projectileForce: Float,
                     charge: Float,
                     color: Color) {
        this.projectileVisual = FloatArray(projectileVisual.size)
        System.arraycopy(projectileVisual, 0, this.projectileVisual, 0, projectileVisual.size)
        this.projectileBody = FloatArray(projectileBody.size)
        System.arraycopy(projectileBody, 0, this.projectileBody, 0, projectileBody.size)
        this.projectileExplosion = FloatArray(projectileExplosion.size)
        System.arraycopy(projectileExplosion, 0, this.projectileExplosion, 0, projectileExplosion.size)
        this.particleDuration = particleDuration
        this.charge = charge
        this.chargeTimer = charge
        this.projectileForce = projectileForce
        this.color = color.cpy()
    }

    fun set(wc: WeaponComponent) {
        val projectileVisual = wc.projectileVisual!!
        val projectileBody = wc.projectileBody!!
        val projectileExplosion = wc.projectileExplosion!!
        this.projectileVisual = FloatArray(projectileVisual.size)
        System.arraycopy(projectileVisual, 0, this.projectileVisual, 0, projectileVisual.size)
        this.projectileBody = FloatArray(projectileBody.size)
        System.arraycopy(projectileBody, 0, this.projectileBody, 0, projectileBody.size)
        this.projectileExplosion = FloatArray(projectileExplosion.size)
        System.arraycopy(projectileExplosion, 0, this.projectileExplosion, 0, projectileExplosion.size)
        this.particleDuration = wc.particleDuration
        this.charge = wc.charge
        this.chargeTimer = wc.charge
        this.projectileForce = wc.projectileForce
        this.color = wc.color!!.cpy()
    }

    override fun reset() {
        projectileBody = null
        projectileVisual = null
        projectileExplosion = null
        particleDuration = 0f
        charge = 0f
        chargeTimer = 0f
        projectileForce = 0f
    }
}
