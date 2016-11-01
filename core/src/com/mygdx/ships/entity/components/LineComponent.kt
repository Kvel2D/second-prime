package com.mygdx.ships.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class LineComponent : Component, Pool.Poolable {
    var line: FloatArray? = null

    constructor()

    constructor(line: FloatArray) {
        this.line = FloatArray(line.size)
        System.arraycopy(line, 0, this.line, 0, line.size)
    }

    fun set(line: FloatArray) {
        this.line = FloatArray(line.size)
        System.arraycopy(line, 0, this.line, 0, line.size)
    }

    fun set(lc: LineComponent) {
        val line = lc.line!!
        this.line = FloatArray(line.size)
        System.arraycopy(line, 0, this.line, 0, line.size)
    }

    override fun reset() {
        line = null
    }
}
