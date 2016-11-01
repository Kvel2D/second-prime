package com.mygdx.ships.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.mygdx.ships.entity.Mappers
import com.mygdx.ships.Main
import com.mygdx.ships.entity.components.LimitedLifespan

class RemovalSystem : IteratingSystem {
    constructor() :
    super(Family.all(LimitedLifespan::class.java).get())

    constructor(priority: Int) :
    super(Family.all(LimitedLifespan::class.java).get(), priority)

    public override fun processEntity(entity: Entity, deltaTime: Float) {
        // Don't operate if ore is inside player or base radius
        val fc = Mappers.followerComponent.get(entity)
        if (fc != null && fc.following)
            return

        val lc = Mappers.limitedLifeSpan.get(entity)

        lc.lifespan -= deltaTime

        if (lc.lifespan <= 0)
            Main.engine.removeEntity(entity)
    }
}
