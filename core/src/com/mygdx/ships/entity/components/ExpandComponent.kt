package com.mygdx.ships.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class ExpandComponent: Component, Pool.Poolable {
    var speed = 0f;

    constructor()

    constructor(speed: Float) {
        this.speed = speed
    }

    fun set(speed: Float) {
        this.speed = speed
    }

    fun set(ec: ExpandComponent) {
        this.speed = ec.speed
    }

    override fun reset() {
        speed = 0f;
    }
}
