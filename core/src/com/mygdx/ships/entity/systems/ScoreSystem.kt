package com.mygdx.ships.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.Vector2
import com.mygdx.ships.AssetPaths
import com.mygdx.ships.Constants
import com.mygdx.ships.GameData
import com.mygdx.ships.Main
import com.mygdx.ships.entity.Mappers
import com.mygdx.ships.entity.components.FollowerComponent
import com.mygdx.ships.entity.components.ScoreComponent
import com.mygdx.ships.entity.components.Transform

class ScoreSystem : IteratingSystem {
    internal var base: Entity
    internal val basePos: Vector2
    internal val baseRadiusSquared = 60f * 60f;
    internal val sound: Sound = Main.assets.get(AssetPaths.COIN)

    constructor(base: Entity) :
    super(Family.all(Transform::class.java, ScoreComponent::class.java).get()) {
        this.base = base;
        val baseTransform = Mappers.transform.get(base)
        basePos = Vector2(baseTransform.x, baseTransform.y)
    }

    constructor(priority: Int, base: Entity) :
    super(Family.all(FollowerComponent::class.java, ScoreComponent::class.java).get(), priority) {
        this.base = base;
        val baseTransform = Mappers.transform.get(base)
        basePos = Vector2(baseTransform.x, baseTransform.y)
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
    }

    public override fun processEntity(entity: Entity, deltaTime: Float) {
        val tc = Mappers.transform.get(entity)

        if (basePos.dst2(tc.x, tc.y) < baseRadiusSquared) {
            GameData.addMoney()
            Main.engine.removeEntity(entity)
            sound.play(Constants.VOLUME / 2)
        }
    }
}