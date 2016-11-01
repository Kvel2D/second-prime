package com.mygdx.ships.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool

class TextureComponent : Component, Pool.Poolable {
    var region: TextureRegion? = null

    constructor()

    constructor(region: TextureRegion) {
        this.region = region
    }

    fun set(region: TextureRegion) {
        this.region = region
    }

    fun set(tc: TextureComponent) {
        this.region = tc.region
    }

    override fun reset() {
        region = null
    }
}
