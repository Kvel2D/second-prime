package com.mygdx.ships.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.physics.box2d.*
import com.mygdx.ships.Main
import com.mygdx.ships.entity.Mappers
import com.mygdx.ships.entity.components.LimitedLifespan

class CollisionSystem : EntitySystem {
    internal var box2dWorld: World
    internal var contactListener: MyContactListener

    constructor(box2dWorld: World) {
        this.box2dWorld = box2dWorld
        this.contactListener = MyContactListener()
        this.box2dWorld.setContactListener(contactListener)
    }

    constructor(priority: Int, box2dWorld: World) : super(priority) {
        this.box2dWorld = box2dWorld
        this.contactListener = MyContactListener()
        this.box2dWorld.setContactListener(contactListener)
    }

    inner class MyContactListener : ContactListener {
        override fun beginContact(contact: Contact) {
            val fixtureA = contact.fixtureA
            val fixtureB = contact.fixtureB
            val entityA = fixtureA.body.userData as Entity
            val entityB = fixtureB.body.userData as Entity

            if (Mappers.explosionComponent.has(entityA) && Mappers.bodyComponent.has(entityB))
                processProjectile(entityA)
            else if (Mappers.explosionComponent.has(entityB) && Mappers.bodyComponent.has(entityA))
                processProjectile(entityB)
        }

        override fun endContact(contact: Contact) {
        }

        override fun preSolve(contact: Contact, oldManifold: Manifold) {
        }

        override fun postSolve(contact: Contact, impulse: ContactImpulse) {
        }
    }

    private fun processProjectile(entity: Entity) {
        val ec = Mappers.explosionComponent.get(entity)
        ec.detonated = true
    }
}