package com.mygdx.ships.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.Pool

class CameraComponent : Component, Pool.Poolable {
    var target: Entity? = null
    var camera: OrthographicCamera? = null
    var actualX: Float = 0f
    var actualY: Float = 0f
    var xOffset: Float = 0f
    var yOffset: Float = 0f

    constructor()

    constructor(target: Entity, camera: OrthographicCamera) {
        this.target = target
        this.camera = camera
        this.actualX = camera.position.x
        this.actualY = camera.position.y
    }

    operator fun set(target: Entity, camera: OrthographicCamera) {
        this.target = target
        this.camera = camera
        this.actualX = camera.position.x
        this.actualY = camera.position.y
    }

    override fun reset() {
        target = null
        camera = null
        xOffset = 0f
        yOffset = 0f
        actualX = 0f
        actualY = 0f
    }
}
