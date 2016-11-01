package com.mygdx.ships.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.mygdx.ships.GameData
import com.mygdx.ships.entity.EntityFactory
import com.mygdx.ships.entity.Mappers
import com.mygdx.ships.entity.components.ParticleEmitter
import com.mygdx.ships.entity.components.Transform
import com.mygdx.ships.rotateAround

class EmitterSystem : IteratingSystem {
    internal var activated = false

    constructor() :
    super(Family.all(ParticleEmitter::class.java, Transform::class.java).get())

    constructor(priority: Int) :
    super(Family.all(ParticleEmitter::class.java, Transform::class.java).get(), priority)

    override fun update(deltaTime: Float) {
        activated = Gdx.input.isKeyPressed(GameData.Controls.MOVE_FORWARD)

        super.update(deltaTime)
    }

    public override fun processEntity(entity: Entity, deltaTime: Float) {
        val pec = Mappers.particleEmitter.get(entity)

        if (pec.chargeTimer > 0)
            pec.chargeTimer -=
                    if (Gdx.input.isKeyPressed(GameData.Controls.SHOOT))
                        deltaTime * 0.5f
                    else deltaTime
        else if (activated) {
            pec.chargeTimer = pec.charge

            val tc = Mappers.transform.get(entity)
            val position = Vector2(tc.x, tc.y)
            position.rotateAround(tc.x, tc.y, tc.angle)
            EntityFactory.particle(
                    pec.particle!!,
                    pec.particleDuration,
                    position.x,
                    position.y)
        }
    }
}
