package com.mygdx.ships.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class WeaponAnimation : Component, Pool.Poolable {
    var animationDefault: FloatArray? = null
    var scaleStart: Float = 0f
    var scaleEnd: Float = 0f

    constructor()

    constructor(animation: FloatArray, scaleStart: Float, scaleEnd: Float) {
        this.animationDefault = FloatArray(animation.size)
        System.arraycopy(animation, 0, this.animationDefault, 0, animation.size)
        this.scaleStart = scaleStart
        this.scaleEnd = scaleEnd
    }

    operator fun set(animation: FloatArray, scaleStart: Float, scaleEnd: Float) {
        this.animationDefault = FloatArray(animation.size)
        System.arraycopy(animation, 0, this.animationDefault, 0, animation.size)
        this.scaleStart = scaleStart
        this.scaleEnd = scaleEnd
    }

    fun set(wac: WeaponAnimation) {
        val animation = wac.animationDefault!!
        this.animationDefault = FloatArray(animation.size)
        System.arraycopy(animation, 0, this.animationDefault, 0, animation.size)
        this.scaleStart = scaleStart
        this.scaleEnd = scaleEnd
    }

    override fun reset() {
        animationDefault = null
        scaleStart = 1f
        scaleEnd = 1f
    }
}
