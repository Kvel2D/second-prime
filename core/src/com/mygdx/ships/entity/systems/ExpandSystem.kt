package com.mygdx.ships.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.mygdx.ships.entity.Mappers
import com.mygdx.ships.entity.components.ExpandComponent
import com.mygdx.ships.entity.components.PolygonComponent
import com.mygdx.ships.scale

class ExpandSystem : IteratingSystem {

    constructor() :
    super(Family.all(ExpandComponent::class.java, PolygonComponent::class.java).get())

    constructor(priority: Int) :
    super(Family.all(ExpandComponent::class.java, PolygonComponent::class.java).get(), priority)

    public override fun processEntity(entity: Entity, deltaTime: Float) {
        val ec = Mappers.expandComponent.get(entity)
        val pc = Mappers.polygonComponent.get(entity)

        pc.polygon!!.scale(1f + ec.speed);
    }
}
