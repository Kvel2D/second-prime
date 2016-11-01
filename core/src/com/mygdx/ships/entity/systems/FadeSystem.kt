package com.mygdx.ships.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.mygdx.ships.entity.Mappers
import com.mygdx.ships.entity.components.ColorComponent
import com.mygdx.ships.entity.components.FadeComponent

class FadeSystem : IteratingSystem {
    constructor() :
    super(Family.all(FadeComponent::class.java, ColorComponent::class.java).get())

    constructor(priority: Int) :
    super(Family.all(FadeComponent::class.java, ColorComponent::class.java).get(), priority)

    public override fun processEntity(entity: Entity, deltaTime: Float) {
        val fc = Mappers.fadeComponent.get(entity)
        val cc = Mappers.colorComponent.get(entity)

        fc.currentDuration -= deltaTime
        cc.color!!.a = fc.currentDuration / fc.duration
    }
}
