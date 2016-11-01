package com.mygdx.ships.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class PlayerMovement : Component, Pool.Poolable {
    var forwardMagnitude = 0f
    var turnMagnitude = 0f
    var stabilizationFactor = 0f
    var forwardTimer = 0f

    constructor()

    constructor(forwardMagnitude: Float, turnMagnitude: Float, stabilizationFactor: Float) {
        this.forwardMagnitude = forwardMagnitude
        this.turnMagnitude = turnMagnitude
        this.stabilizationFactor = stabilizationFactor
    }

    operator fun set(forwardMagnitude: Float, turnMagnitude: Float, stabilizationFactor: Float) {
        this.forwardMagnitude = forwardMagnitude
        this.turnMagnitude = turnMagnitude
        this.stabilizationFactor = stabilizationFactor
    }

    fun set(pmc: PlayerMovement) {
        this.forwardMagnitude = pmc.forwardMagnitude
        this.turnMagnitude = pmc.turnMagnitude
        this.stabilizationFactor = pmc.turnMagnitude
    }

    override fun reset() {
        forwardMagnitude = 0f
        turnMagnitude = 0f
        stabilizationFactor = 0f
        forwardTimer = 0f
    }
}
