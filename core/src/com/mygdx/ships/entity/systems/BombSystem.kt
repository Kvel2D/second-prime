package com.mygdx.ships.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.Vector2
import com.mygdx.ships.*
import com.mygdx.ships.entity.EntityFactory
import com.mygdx.ships.entity.Mappers
import com.mygdx.ships.entity.components.BombComponent
import com.mygdx.ships.entity.components.Transform

class BombSystem : IteratingSystem {
    internal val cameraSystem: CameraSystem
    internal val terrainSystem: TerrainSystem
    internal val sound: Sound = Main.assets.get(AssetPaths.PULSE)

    constructor(terrainSystem: TerrainSystem, cameraSystem: CameraSystem) :
    super(Family.all(BombComponent::class.java, Transform::class.java).get()) {
        this.cameraSystem = cameraSystem
        this.terrainSystem = terrainSystem
    }

    constructor(priority: Int, terrainSystem: TerrainSystem, cameraSystem: CameraSystem) :
    super(Family.all(BombComponent::class.java, Transform::class.java).get(), priority) {
        this.cameraSystem = cameraSystem
        this.terrainSystem = terrainSystem
    }

    public override fun processEntity(entity: Entity, deltaTime: Float) {
        val bc = Mappers.bombComponent.get(entity)

        if (bc.chargeTimer > 0) {
            bc.chargeTimer -= if (GameData.stress) deltaTime * 2 else deltaTime
        } else {
            val tc = Mappers.transform.get(entity)

            EntityFactory.bubble(Vector2(tc.x, tc.y), 100f)

            bc.chargeTimer = bc.charge

            val explosion = bc.explosion!!

            terrainSystem.addExplosion(explosion, tc.x, tc.y)

            val explosionRadius = explosion.getRadius()
            val shakeDeterioration = 0.9f
            var shakeIntensity: Float
            var shakeRadius: Float
            when (explosionRadius) {
                in 0f..200f -> {
                    shakeIntensity = 0.3f
                    shakeRadius = 9f
                }
                in 200f..400f -> {
                    shakeIntensity = 0.4f
                    shakeRadius = 10f
                }
                else -> {
                    shakeIntensity = 0.8f
                    shakeRadius = 15f
                }
            }
            // add a bit of randomness
            shakeIntensity += Math.random().toFloat() / 50
            shakeRadius += Math.random().toFloat() / 10
            cameraSystem.addScreenshake(shakeRadius, shakeIntensity, shakeDeterioration)

            sound.play(Constants.VOLUME)
        }
    }
}
