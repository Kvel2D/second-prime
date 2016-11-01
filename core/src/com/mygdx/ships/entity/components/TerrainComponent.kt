package com.mygdx.ships.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class TerrainComponent : Component, Pool.Poolable {
    var type: String? = null
    var level: Int = 0

    constructor()

    constructor(type: String, level: Int) {
        this.type = type
        this.level = level
    }

    operator fun set(type: String, level: Int) {
        this.type = type
        this.level = level
    }

    fun set(tc: TerrainComponent) {
        this.type = tc.type
        this.level = tc.level
    }

    override fun reset() {
        type = null
        level = 0
    }
}
