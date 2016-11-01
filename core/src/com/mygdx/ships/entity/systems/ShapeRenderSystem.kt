package com.mygdx.ships.entity.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.mygdx.ships.Constants
import com.mygdx.ships.Main
import com.mygdx.ships.entity.Mappers
import com.mygdx.ships.entity.components.LineComponent
import com.mygdx.ships.entity.components.PolygonComponent
import com.mygdx.ships.entity.components.Transform
import com.mygdx.ships.translate

class ShapeRenderSystem : EntitySystem {
    lateinit var polygons: ImmutableArray<Entity>
    lateinit var lines: ImmutableArray<Entity>
    internal val camera: OrthographicCamera
    internal val shapeRenderer: ShapeRenderer

    constructor(camera: OrthographicCamera) {
        this.camera = camera
        shapeRenderer = ShapeRenderer()
    }

    constructor(priority: Int, camera: OrthographicCamera) :
    super(priority) {
        this.camera = camera
        shapeRenderer = ShapeRenderer()
    }

    override fun addedToEngine(engine: Engine?) {
        polygons = engine!!.getEntitiesFor(Family.all(PolygonComponent::class.java, Transform::class.java).get())
        lines = engine.getEntitiesFor(Family.all(LineComponent::class.java, Transform::class.java).get())
    }

    override fun update(deltaTime: Float) {
        Gdx.gl.glEnable(GL20.GL_BLEND)

        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)

        polygons.forEach { processPolygons(it) }
        lines.forEach { processLines(it) }

        shapeRenderer.end()
    }

    private fun processPolygons(entity: Entity) {
        val pc = Mappers.polygonComponent.get(entity)
        val tc = Mappers.transform.get(entity)

        if (tc.y + pc.highY + 540f < camera.position.y
                || tc.y + pc.lowY - 540f > camera.position.y
                || tc.x + pc.rightX + 960f < camera.position.x
                || tc.x + pc.leftX - 960f > camera.position.x) {
            if (Mappers.explosionComponent.has(entity))
                Main.engine.removeEntity(entity)
            return
        }

        val colorComponent = Mappers.colorComponent.get(entity)
        if (colorComponent != null)
            shapeRenderer.color = colorComponent.color
        else shapeRenderer.color = Constants.DEFAULT_SHAPE_COLOR

        val vertices = FloatArray(pc.polygon!!.size)
        System.arraycopy(pc.polygon, 0, vertices, 0, vertices.size)
        vertices.translate(tc.x, tc.y, tc.angle)
        shapeRenderer.polygon(vertices)
    }

    private fun processLines(entity: Entity) {
        val lc = Mappers.lineComponent.get(entity)
        val tc = Mappers.transform.get(entity)
        val colorComponent = Mappers.colorComponent.get(entity)

        if (colorComponent != null)
            shapeRenderer.color = colorComponent.color
        else shapeRenderer.color = Constants.DEFAULT_SHAPE_COLOR

        val vertices = FloatArray(lc.line!!.size)
        System.arraycopy(lc.line, 0, vertices, 0, vertices.size)
        vertices.translate(tc.x, tc.y, tc.angle)
        var j = 0
        while (j < vertices.size) {
            shapeRenderer.line(vertices[j], vertices[j + 1], vertices[j + 2], vertices[j + 3])
            j += 4
        }
    }
}
