package com.mygdx.ships.entity.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.mygdx.ships.GameData
import com.mygdx.ships.entity.EntityFactory
import com.mygdx.ships.entity.Mappers
import com.mygdx.ships.entity.components.PolygonComponent
import com.mygdx.ships.entity.components.SapComponent

class SapSystem : EntitySystem {
    lateinit var entities: ImmutableArray<Entity>
    internal var player: Entity
    internal var playerPos: Vector2
    internal val sapRadius = 300f * 300f;

    constructor(player: Entity) {
        this.player = player;
        val playerTransform = Mappers.transform.get(player)
        playerPos = Vector2(playerTransform.x, playerTransform.y)
    }

    constructor(priority: Int, player: Entity) :
    super(priority) {
        this.player = player;
        val playerTransform = Mappers.transform.get(player)
        playerPos = Vector2(playerTransform.x, playerTransform.y)
    }

    override fun addedToEngine(engine: Engine?) {
        entities = engine!!.getEntitiesFor(Family.all(SapComponent::class.java, PolygonComponent::class.java).get())
    }

    override fun update(deltaTime: Float) {
        val playerTransform = Mappers.transform.get(player)
        playerPos = Vector2(playerTransform.x, playerTransform.y)

        entities.forEach {
            if (processEntity(it))
            {
                GameData.sap = true
                return
            }
        }
        GameData.sap = false
    }

    fun processEntity(entity: Entity): Boolean {
        val pc = Mappers.polygonComponent.get(entity)
        val i = (Math.random()*pc.polygon!!.size/2).toInt()
        val x = pc.polygon!![i*2]
        val y = pc.polygon!![i*2+1]

        val inRadius = playerPos.dst2(x, y) < sapRadius
        if (inRadius)
        {
            EntityFactory.energyBeam(Vector2(x, y), playerPos, Color.CYAN, 0.1f)
        }

        return inRadius
    }
}