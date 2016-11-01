package com.mygdx.ships.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.mygdx.ships.Main
import com.mygdx.ships.entity.Mappers
import com.mygdx.ships.entity.components.TetherComponent
import com.mygdx.ships.entity.components.Transform
import com.mygdx.ships.rotateAround

class TetherSystem : IteratingSystem {
    constructor() :
    super(Family.all(TetherComponent::class.java, Transform::class.java).get())

    constructor(priority: Int) :
    super(Family.all(TetherComponent::class.java, Transform::class.java).get(), priority)

    public override fun processEntity(entity: Entity, deltaTime: Float) {
        val tether = Mappers.tetherComponent.get(entity)
        if (tether.entity == null) {
            Main.engine.removeEntity(entity)
            return
        }

        val tc = Mappers.transform.get(entity)
        val tetherTransform = Mappers.transform.get(tether.entity)

        if (tether.rotated) {
            val position = Vector2(tetherTransform.x + tether.xOffset,
                    tetherTransform.y + tether.yOffset)
            position.rotateAround(tetherTransform.x, tetherTransform.y, tetherTransform.angle)
            tc.x = position.x
            tc.y = position.y
            tc.angle = tetherTransform.angle + tether.angleOffset
        } else {
            tc.x = tetherTransform.x + tether.xOffset
            tc.y = tetherTransform.y + tether.yOffset
            tc.angle = tetherTransform.angle + tether.angleOffset
        }
    }

    fun untether(entity: Entity) {
        for (i in 0..entities.size() - 2) {
            val tether = Mappers.tetherComponent.get(entities.get(i))
            if (tether.entity == entity)
                Main.engine.removeEntity(entities.get(i))
        }
    }
}
