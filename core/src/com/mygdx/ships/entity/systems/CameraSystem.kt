package com.mygdx.ships.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.mygdx.ships.entity.Mappers
import com.mygdx.ships.entity.components.CameraComponent

class CameraSystem : IteratingSystem {
    internal var shakeIntensity: Float = 0f
    internal var shakeRadius: Float = 0f
    internal var shakeAngle: Float = 0f
    internal var shakeDeterioration = 0.9f
    internal var cameraLerp = 0.1f

    constructor() : super(Family.all(CameraComponent::class.java).get())

    constructor(priority: Int) : super(Family.all(CameraComponent::class.java).get(), priority)

    public override fun processEntity(entity: Entity, deltaTime: Float) {
        val cc = Mappers.cameraComponent.get(entity)
        val camera = cc.camera!!
        val targetTransform = Mappers.transform.get(cc.target)

        if (shakeRadius > 1f) {
            shakeRadius *= shakeDeterioration
            shakeAngle += 180 + (Math.random().toFloat() * 120 - 60)
        } else shakeRadius = 0f

        cc.xOffset = Math.sin(shakeAngle.toDouble()).toFloat() * shakeRadius * shakeIntensity
        cc.yOffset = Math.cos(shakeAngle.toDouble()).toFloat() * shakeRadius * shakeIntensity

        // Move camera's position to the player
        cc.actualX += (targetTransform.x - camera.position.x) * cameraLerp
        cc.actualY += (targetTransform.y - camera.position.y) * cameraLerp

        // Add offsets to the camera
        camera.position.x = cc.actualX + cc.xOffset
        camera.position.y = cc.actualY + cc.yOffset

        camera.update()
    }

    fun addScreenshake(shakeRadius: Float, shakeIntensity: Float, shakeDeterioration: Float) {
        // Change screenshake values only if the new screenshake radius is bigger
        if (shakeRadius > this.shakeRadius) {
            this.shakeRadius = shakeRadius
            this.shakeIntensity = shakeIntensity + Math.random().toFloat() / 2 - 0.25f
            this.shakeDeterioration = shakeDeterioration
            this.shakeAngle = Math.random().toFloat() * 360
        }
    }
}
