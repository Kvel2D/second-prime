package com.mygdx.ships.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class LeaderComponent : Component, Pool.Poolable {
    var attractionRadius: Float = 0f
    var touchRadius: Float = 0f

    constructor()

    constructor(attractionRadius: Float, touchRadius: Float) {
        this.attractionRadius = attractionRadius
        this.touchRadius = touchRadius
    }

    operator fun set(attractionRadius: Float, touchRadius: Float) {
        this.attractionRadius = attractionRadius
        this.touchRadius = touchRadius
    }

    fun set(lc: LeaderComponent) {
        this.attractionRadius = lc.attractionRadius
        this.touchRadius = lc.touchRadius
    }

    override fun reset() {
        attractionRadius = 0f
        touchRadius = 0f
    }
}
