package com.mygdx.ships.entity.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.mygdx.ships.*
import com.mygdx.ships.clipper.*
import com.mygdx.ships.entity.EntityFactory
import com.mygdx.ships.entity.Mappers
import com.mygdx.ships.entity.components.BodyComponent
import com.mygdx.ships.entity.components.ColorComponent
import com.mygdx.ships.entity.components.PolygonComponent
import com.mygdx.ships.entity.components.TerrainComponent

import java.util.ArrayList

class TerrainSystem : EntitySystem {
    lateinit internal var entities: ImmutableArray<Entity>
    internal var explosionPolygons = ArrayList<FloatArray>()
    internal var explosionOrigins: MutableList<Vector2> = ArrayList()
    internal var newEntityList: MutableList<Entity> = ArrayList()
    lateinit internal var engine: Engine
    internal var camera: OrthographicCamera
    internal var explosionRadius = 0f

    constructor(camera: OrthographicCamera) {
        this.camera = camera
    }

    override fun addedToEngine(engine: Engine) {
        this.engine = engine
        entities = engine.getEntitiesFor(Family.all(TerrainComponent::class.java, PolygonComponent::class.java).get())
    }

    override fun update(deltaTime: Float) {
        while (!explosionPolygons.isEmpty()) {
            explosionRadius = explosionPolygons[0].getRadius()

            var entityExploded: Boolean
            entities.forEach {
                entityExploded = processEntity(it)
                if (entityExploded) Main.engine.removeEntity(it)
            }

            newEntityList.forEach { Main.engine.addEntity(it) }
            newEntityList.clear()

            explosionPolygons.removeAt(0)
            explosionOrigins.removeAt(0)
        }
    }

    fun processEntity(entity: Entity): Boolean {
        var entityExploded = true
        val tc = Mappers.terrainComponent.get(entity)
        val pc = Mappers.polygonComponent.get(entity)

        // terrain above a certain level can't be destroyed
        if (tc.level > Constants.INDESTRUCTIBLE_LEVEL)
            return false

        // CULLING
        if (pc.highY + explosionRadius + 10f < explosionOrigins[0].y
                || pc.lowY - explosionRadius - 10f > explosionOrigins[0].y
                || pc.rightX + explosionRadius + 10f < explosionOrigins[0].x
                || pc.leftX - explosionRadius - 10f > explosionOrigins[0].x)
            return false

        val polygon = FloatArray(pc.polygon!!.size)
        System.arraycopy(pc.polygon, 0, polygon, 0, (pc.polygon as FloatArray).size)

        // Scale explosion polygon by terrain level(higher level = smaller explosion)
        val unscaledExplosion = explosionPolygons[0]
        val explosion = FloatArray(unscaledExplosion.size)
        System.arraycopy(unscaledExplosion, 0, explosion, 0, unscaledExplosion.size)
        explosion.scale(1f / tc.level)
        val explosionOrigin = explosionOrigins[0]

        // Create clip polygon path
        val clip = Paths()
        val clipPath = Path()
        run {
            var j = 0
            while (j < explosion.size - 1) {
                val x = (explosion[j] + explosionOrigin.x).toLong()
                val y = (explosion[j + 1] + explosionOrigin.y).toLong()
                clipPath.add(Point.LongPoint(x, y))
                j += 2
            }
        }
        clip.add(clipPath)

        // Set up Clipper
        val subjectPath = Path()
        run {
            var j = 0
            while (j < polygon.size) {
                subjectPath.add(Point.LongPoint(polygon[j].toLong(), polygon[j + 1].toLong()))
                j += 2
            }
        }
        val subject = Paths()
        subject.add(subjectPath)
        val c = DefaultClipper()
        c.addPaths(subject, Clipper.PolyType.SUBJECT, true)
        c.addPaths(clip, Clipper.PolyType.CLIP, true)
        val solutionIntesection = Paths()
        val solutionDifference = Paths()

        // INTERSECTION CHECK
        c.execute(Clipper.ClipType.INTERSECTION, solutionIntesection)
        if (solutionIntesection.isEmpty())
            return false

        // Continues if the polygon was intersected
        c.execute(Clipper.ClipType.DIFFERENCE, solutionDifference)
        val resultPolygons = ArrayList<FloatArray>()
        for (j in solutionDifference.indices) {
            val path = solutionDifference[j]
            if (path.area() < Constants.MINIMUM_POLYGON_AREA) continue
            val pathConverted = FloatArray(path.size * 2)
            for (k in path.indices) {
                val vertex = path[k]
                pathConverted[k * 2] = vertex.x.toFloat()
                pathConverted[k * 2 + 1] = vertex.y.toFloat()
            }
            resultPolygons.add(pathConverted)
        }

        val type = tc.type
        val level = tc.level
        val cc = Mappers.colorComponent.get(entity)
        val color = cc.color
        val areaBefore = polygon.getArea()
        var areaAfter = 0f
        resultPolygons.forEach { areaAfter += it.getArea() }
        val areaDifference = areaBefore - areaAfter
        if (resultPolygons.size == 1) {
            EntityFactory.updateTerrain(entity, resultPolygons[0])
            entityExploded = false
        } else {
            resultPolygons.forEach {
                newEntityList.add(EntityFactory.terrain(it, type!!, level, color!!))
            }
        }

        // Spawn ore if explosion is big enough and is not gray
        if (type != "gray" && areaDifference > Constants.MIN_EXPLOSION_FOR_ORE) {
            resultPolygons.clear()
            for (j in solutionIntesection.indices) {
                val path = solutionIntesection[j]
                val pathConverted = FloatArray(path.size * 2)
                for (k in path.indices) {
                    val vertex = path[k]
                    pathConverted[k * 2] = vertex.x.toFloat()
                    pathConverted[k * 2 + 1] = vertex.y.toFloat()
                }
                resultPolygons.add(pathConverted)
            }

            val amount = ((areaBefore - areaAfter) / 100).toInt()
            val vertices = EntityFactory.createSquarePolygon(0f, 0f, 5f)
            var radius = explosion.getRadius()
            radius /= 1.5f

            for (i in 0..amount - 1) {
                val randomAngle = Math.random() * Math.PI * 2.0
                val spawnX = explosionOrigin.x + (Math.cos(randomAngle) * Math.random() * radius).toFloat()
                val spawnY = explosionOrigin.y + (Math.sin(randomAngle) * Math.random() * radius).toFloat()
                EntityFactory.ore(vertices, 3f, color!!, type!!, spawnX, spawnY)
            }
        }

        return entityExploded
    }

    fun addExplosion(polygon: FloatArray, x: Float, y: Float) {
        explosionPolygons.add(polygon)
        explosionOrigins.add(Vector2(x, y))
    }
}