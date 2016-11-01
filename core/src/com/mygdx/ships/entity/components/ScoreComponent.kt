package com.mygdx.ships.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class ScoreComponent : Component, Pool.Poolable {
    var type: String? = null

    constructor()

    constructor(type: String) {
        this.type = type
    }

    fun set(type: String) {
        this.type = type
    }

    fun set(sc: ScoreComponent) {
        this.type = sc.type
    }

    override fun reset() {
        type = null
    }
}
