package com.mygdx.ships.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Pool
import com.mygdx.ships.Main

class BodyComponent : Component, Pool.Poolable {
    var body: Body? = null

    constructor()

    constructor(body: Body) {
        this.body = body
    }

    fun set(body: Body) {
        this.body = body
    }

    override fun reset() {
        Main.world.destroyBody(body)
        body = null
    }
}
