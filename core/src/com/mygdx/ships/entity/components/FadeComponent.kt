package com.mygdx.ships.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class FadeComponent : Component, Pool.Poolable {
    var duration: Float = 0f
    var currentDuration: Float = 0f

    constructor()

    constructor(duration: Float) {
        this.duration = duration
        this.currentDuration = duration
    }

    fun set(duration: Float) {
        this.duration = duration
        this.currentDuration = duration
    }

    fun set(fc: FadeComponent) {
        this.duration = fc.duration
        this.currentDuration = fc.duration
    }

    override fun reset() {
        duration = 0f
        currentDuration = 0f
    }
}
