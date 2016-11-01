package com.mygdx.ships.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class LimitedLifespan : Component, Pool.Poolable {
    var lifespan: Float = 0f

    constructor()

    constructor(lifespan: Float) {
        this.lifespan = lifespan
    }

    fun set(lifespan: Float) {
        this.lifespan = lifespan
    }

    fun set(lc: LimitedLifespan) {
        this.lifespan = lc.lifespan
    }

    override fun reset() {
        lifespan = 0f
    }
}
