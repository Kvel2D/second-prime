package com.mygdx.ships.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.Vector2
import com.mygdx.ships.*
import com.mygdx.ships.entity.EntityFactory
import com.mygdx.ships.entity.Mappers
import com.mygdx.ships.entity.components.Transform
import com.mygdx.ships.entity.components.WeaponComponent

class WeaponSystem : IteratingSystem {
    internal var activated: Boolean = false
    internal val sound: Sound = Main.assets.get(AssetPaths.SHOOT)
    internal var soundPlayed = false

    constructor() :
    super(Family.all(WeaponComponent::class.java, Transform::class.java).get())

    constructor(priority: Int) :
    super(Family.all(WeaponComponent::class.java, Transform::class.java).get(), priority)

    override fun update(deltaTime: Float) {
        activated = Gdx.input.isKeyPressed(GameData.Controls.SHOOT)
        soundPlayed = false
        super.update(deltaTime)
    }

    public override fun processEntity(entity: Entity, deltaTime: Float) {
        val wc = Mappers.weaponComponent.get(entity)

        if (!activated) {
            if (wc.chargeTimer < wc.charge)
                wc.chargeTimer += deltaTime / 3
            else wc.chargeTimer = wc.charge

            return
        }

        if (wc.chargeTimer > 0) {
            wc.chargeTimer -= if (GameData.stress) deltaTime * 2 else deltaTime
        } else {
            val tc = Mappers.transform.get(entity)
            val rotatedPoint = Vector2(tc.x, tc.y)
            rotatedPoint.rotateAround(tc.x, tc.y, tc.angle)
            EntityFactory.projectile(wc.projectileVisual!!,
                    wc.projectileBody!!,
                    wc.projectileExplosion!!,
                    wc.particleDuration,
                    wc.color!!,
                    rotatedPoint.x,
                    rotatedPoint.y,
                    wc.projectileForce,
                    tc.angle)
            if (!soundPlayed) {
                sound.play(Constants.VOLUME)
                soundPlayed = true
            }
            wc.chargeTimer = wc.charge
        }
    }
}
