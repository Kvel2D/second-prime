package com.mygdx.ships.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.mygdx.ships.AssetPaths
import com.mygdx.ships.GameData
import com.mygdx.ships.Main
import com.mygdx.ships.entity.Mappers
import com.mygdx.ships.entity.components.BodyComponent
import com.mygdx.ships.entity.components.PlayerMovement

class PlayerMovementSystem : IteratingSystem {
    internal var forward = false
    internal var left = false
    internal var right = false
    internal var stabilize = false
    internal var shoot = false
    internal var reverse = false

    constructor() :
    super(Family.all(PlayerMovement::class.java, BodyComponent::class.java).get())

    constructor(priority: Int) :
    super(Family.all(PlayerMovement::class.java, BodyComponent::class.java).get(), priority)

    override fun update(deltaTime: Float) {
        forward = Gdx.input.isKeyPressed(GameData.Controls.MOVE_FORWARD)
        left = Gdx.input.isKeyPressed(GameData.Controls.TURN_LEFT)
        right = Gdx.input.isKeyPressed(GameData.Controls.TURN_RIGHT)
        stabilize = Gdx.input.isKeyPressed(GameData.Controls.STABILIZE)
        shoot = Gdx.input.isKeyPressed(GameData.Controls.SHOOT)
        reverse = Gdx.input.isKeyPressed(GameData.Controls.REVERSE)

        super.update(deltaTime)
    }

    public override fun processEntity(entity: Entity, deltaTime: Float) {
        val bc = Mappers.bodyComponent.get(entity)
        val pmc = Mappers.playerMovement.get(entity)
        val body = bc.body!!

        val angle = body.angle
        val force = Vector2()
        force.x = Math.cos(angle.toDouble()).toFloat()
        force.y = Math.sin(angle.toDouble()).toFloat()
        force.scl(1f + pmc.forwardTimer/ 4f)

        // Control precedence: stabilize > forward > reverse > turn
        // Turning is exclusive, i.e. turning left and right results in no turning
        if (stabilize) {
            val angularVelocity = body.angularVelocity
            body.applyAngularImpulse(-2f * Math.signum(angularVelocity) * pmc.turnMagnitude, true)
            val velocity = body.linearVelocity
            if (Math.abs(velocity.x) < 0.2f) {
                body.setLinearVelocity(0f, 0f)
                return
            }
            body.applyForceToCenter(velocity.scl(-pmc.stabilizationFactor), true)
        }
        if (forward) {
            if (pmc.forwardTimer < 8f)
                pmc.forwardTimer += deltaTime
            force.scl(pmc.forwardMagnitude)
            if (shoot) force.scl(0.5f)
            body.applyForceToCenter(force, true)
        }
        else if (reverse) {
            pmc.forwardTimer = 0f
            force.scl(-pmc.forwardMagnitude / 8)
            body.applyForceToCenter(force, true)
        }
        else pmc.forwardTimer = 0f
        if (left && !right)
            body.applyAngularImpulse(pmc.turnMagnitude, true)
        if (right && !left)
            body.applyAngularImpulse(-pmc.turnMagnitude, true)
    }
}
