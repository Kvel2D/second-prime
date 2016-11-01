package com.mygdx.ships.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Pool

class ColorComponent : Component, Pool.Poolable {
    var color: Color? = null

    constructor()

    constructor(color: Color) {
        this.color = color.cpy()
    }

    fun set(color: Color) {
        this.color = color.cpy()
    }

    fun set(cc: ColorComponent) {
        this.color = cc.color!!.cpy()
    }

    override fun reset() {
        color = null
    }
}
