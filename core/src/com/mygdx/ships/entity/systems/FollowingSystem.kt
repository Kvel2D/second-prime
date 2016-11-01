package com.mygdx.ships.entity.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.Vector2
import com.mygdx.ships.entity.Mappers
import com.mygdx.ships.entity.components.*

class FollowingSystem : EntitySystem {
    lateinit var followerEntities: ImmutableArray<Entity>
    internal var player: Entity
    internal var base: Entity
    internal val basePos: Vector2
    internal val accelerationReverse = 10f

    constructor(player: Entity, base: Entity) {
        this.player = player
        this.base = base;
        val baseTransform = Mappers.transform.get(base)
        basePos = Vector2(baseTransform.x, baseTransform.y)
    }

    constructor(priority: Int, player: Entity, base: Entity) :
    super(priority) {
        this.player = player
        this.base = base;
        val baseTransform = Mappers.transform.get(base)
        basePos = Vector2(baseTransform.x, baseTransform.y)
    }

    override fun addedToEngine(engine: Engine?) {
        followerEntities = engine!!.getEntitiesFor(Family.all(FollowerComponent::class.java, Transform::class.java).get())
    }

    override fun update(deltaTime: Float) {
        val lc = Mappers.leaderComponent.get(player)
        val playerTransform = Mappers.transform.get(player)
        val playerPos = Vector2(playerTransform.x, playerTransform.y)
        val attractionRadius = lc.attractionRadius
        val touchRadius = lc.touchRadius

        followerEntities.forEach {
            processFollower(it, playerPos, basePos, attractionRadius, touchRadius)
        }
    }

    fun processFollower(entity: Entity,
                        playerPos: Vector2,
                        basePos: Vector2,
                        attractionRadius: Float,
                        touchRadius: Float) {
        val fc = Mappers.followerComponent.get(entity)
        val followerTransform = Mappers.transform.get(entity)

        var xDiff: Float;
        var yDiff: Float;
        if (followerTransform.y * followerTransform.y + followerTransform.x * followerTransform.x > 200000f) // attracted to player
        {
            xDiff = playerPos.x - followerTransform.x
            yDiff = playerPos.y - followerTransform.y
            var distanceSquared = xDiff * xDiff + yDiff * yDiff

            if (distanceSquared > attractionRadius * attractionRadius) // out of reach
            {
                fc.following = false
                return
            }

            fc.following = true

            // If particle is inside touch radius, slow down a bit and stop accelerating
            if (distanceSquared < touchRadius * touchRadius) {
                fc.speed.scl(0.99f);
                followerTransform.x += fc.speed.x
                followerTransform.y += fc.speed.y
                return
            }
        } else // attracted to base
        {
            fc.following = true // when going to base, ore is always "touched
            xDiff = basePos.x - followerTransform.x
            yDiff = basePos.y - followerTransform.y
        }

        val angle = Math.atan((yDiff / xDiff).toDouble()).toFloat()
        // If the follower has accelerated past one of the speed's axes and missed the leader
        // acceleration is increased to decrease circling
        var xAcceleration = Math.cos(angle.toDouble()).toFloat() * fc.acceleration * Math.signum(xDiff)
        if (Math.signum(xDiff) != Math.signum(fc.speed.x))
            xAcceleration *= accelerationReverse
        var yAcceleration = Math.abs(Math.sin(angle.toDouble())).toFloat() * fc.acceleration * Math.signum(yDiff)
        if (Math.signum(yDiff) != Math.signum(fc.speed.y))
            yAcceleration *= accelerationReverse

        fc.speed.add(xAcceleration, yAcceleration)

        followerTransform.x += fc.speed.x
        followerTransform.y += fc.speed.y
    }

    fun setPlayer(player: Entity) { this.player = player}
    fun setBase(base: Entity) { this.base = base}
}
