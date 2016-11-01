package com.mygdx.ships.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import com.mygdx.ships.Constants
import com.mygdx.ships.entity.Mappers
import com.mygdx.ships.entity.components.BodyComponent
import com.mygdx.ships.entity.components.Transform

class PhysicsSystem : IteratingSystem {
    internal val world: World

    constructor(box2dWorld: World) :
    super(Family.all(BodyComponent::class.java, Transform::class.java).get()) {
        this.world = box2dWorld
    }

    constructor(priority: Int, box2dWorld: World) :
    super(Family.all(BodyComponent::class.java, Transform::class.java).get(), priority) {
        this.world = box2dWorld
    }

    override fun update(deltaTime: Float) {
        world.step(Constants.TIME_STEP,
                Constants.VELOCITY_ITERATIONS,
                Constants.POSITION_ITERATIONS)

        super.update(deltaTime)
    }

    public override fun processEntity(entity: Entity, deltaTime: Float) {
        val tc = Mappers.transform.get(entity)
        val bc = Mappers.bodyComponent.get(entity)
        val body = bc.body!!

        val position = body.position
        val angle = body.angle / Constants.DEGTORAD
        tc.x = position.x / Constants.METER_TO_PIXEL
        tc.y = position.y / Constants.METER_TO_PIXEL
        tc.angle = angle
    }
}
