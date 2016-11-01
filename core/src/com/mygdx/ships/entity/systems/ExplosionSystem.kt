package com.mygdx.ships.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.Vector2
import com.mygdx.ships.AssetPaths
import com.mygdx.ships.Constants
import com.mygdx.ships.Main
import com.mygdx.ships.entity.EntityFactory
import com.mygdx.ships.entity.Mappers
import com.mygdx.ships.entity.components.ExplosionComponent
import com.mygdx.ships.getRadius

class ExplosionSystem : IteratingSystem {
    internal var terrainSystem: TerrainSystem
    internal var cameraSystem: CameraSystem
    internal val sound: Sound = Main.assets.get(AssetPaths.EXPLOSION)

    constructor(terrainSystem: TerrainSystem, cameraSystem: CameraSystem) :
    super(Family.all(ExplosionComponent::class.java).get()) {
        this.terrainSystem = terrainSystem
        this.cameraSystem = cameraSystem
    }

    constructor(priority: Int, terrainSystem: TerrainSystem, cameraSystem: CameraSystem) :
    super(Family.all(ExplosionComponent::class.java).get(), priority) {
        this.terrainSystem = terrainSystem
        this.cameraSystem = cameraSystem
    }

    public override fun processEntity(entity: Entity, deltaTime: Float) {
        val ec = Mappers.explosionComponent.get(entity)

        if (ec.detonated) {
            val explosion = ec.explosion!!
            val tc = Mappers.transform.get(entity)
            terrainSystem.addExplosion(explosion, tc.x, tc.y)

            Main.engine.removeEntity(entity)

            val explosionRadius = explosion.getRadius()
            val shakeDeterioration = 0.9f
            var shakeIntensity: Float
            var shakeRadius: Float
            when (explosionRadius) {
                in 0f..40f -> {
                    shakeIntensity = 0.3f
                    shakeRadius = 9f
                }
                in 40f..90f -> {
                    shakeIntensity = 0.4f
                    shakeRadius = 10f
                }
                in 90f..150f -> {
                    shakeIntensity = 0.8f
                    shakeRadius = 15f
                }
                else -> {
                    shakeIntensity = 1.6f
                    shakeRadius = 20f
                }
            }
            // add a bit of randomness
            shakeIntensity += Math.random().toFloat() / 50
            shakeRadius += Math.random().toFloat() / 10
            cameraSystem.addScreenshake(shakeRadius, shakeIntensity, shakeDeterioration)

            EntityFactory.bubble(Vector2(tc.x, tc.y), 30f)

            sound.play(Constants.VOLUME)
        }
    }
}
