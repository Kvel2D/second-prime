package com.mygdx.ships.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import com.mygdx.ships.entity.Mappers
import com.mygdx.ships.entity.components.*
import com.mygdx.ships.scale

class WeaponAnimationSystem : IteratingSystem {
    constructor() :
    super(Family.all(WeaponAnimation::class.java, WeaponComponent::class.java, LineComponent::class.java).get())

    constructor(priority: Int) :
    super(Family.all(WeaponAnimation::class.java, WeaponComponent::class.java, LineComponent::class.java).get(), priority)

    public override fun processEntity(entity: Entity, deltaTime: Float) {
        val wac = Mappers.weaponAnimation.get(entity)
        val wc = Mappers.weaponComponent.get(entity)
        val lc = Mappers.lineComponent.get(entity)
        val cc = Mappers.colorComponent.get(entity)

        val r = Math.random().toFloat() * 0.5f + 0.5f
        val g = Math.random().toFloat() * 0.5f + 0.5f
        val b = Math.random().toFloat() * 0.5f + 0.5f
        cc.color = Color(r, g, b, 0.7f)

        lc.line = FloatArray(wac.animationDefault!!.size)
        System.arraycopy(wac.animationDefault, 0, lc.line, 0, (wac.animationDefault as FloatArray).size)
        val scale = (wac.scaleStart - wac.scaleEnd) * (wc.chargeTimer / wc.charge) + wac.scaleEnd
        lc.line!!.scale(scale)
    }
}
