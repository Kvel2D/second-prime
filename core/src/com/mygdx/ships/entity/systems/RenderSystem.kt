package com.mygdx.ships.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.mygdx.ships.Constants
import com.mygdx.ships.Main
import com.mygdx.ships.entity.Mappers
import com.mygdx.ships.entity.components.TextureComponent
import com.mygdx.ships.entity.components.Transform
import java.util.*

class RenderSystem : SortedIteratingSystem {
    internal val batch: SpriteBatch
    internal val camera: OrthographicCamera
    internal val debugRenderer = Box2DDebugRenderer()

    constructor(batch: SpriteBatch, camera: OrthographicCamera) :
    super(Family.all(TextureComponent::class.java, Transform::class.java).get(), ZComparator()) {
        this.batch = batch
        this.camera = camera
    }

    constructor(priority: Int, batch: SpriteBatch, camera: OrthographicCamera) :
    super(Family.all(TextureComponent::class.java, Transform::class.java).get(), ZComparator(), priority) {
        this.batch = batch
        this.camera = camera
    }

    override fun update(deltaTime: Float) {
        batch.projectionMatrix = camera.combined
        batch.begin()
        super.update(deltaTime)
        batch.end()

//                  BOX2D DEBUG RENDERING
//        var box2dmatrix = camera.combined.cpy();
//        box2dmatrix.scl(1 / Constants.METER_TO_PIXEL);
//        debugRenderer.render(Main.world, box2dmatrix);
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = Mappers.transform.get(entity)
        val textureComponent = Mappers.textureComponent.get(entity)

        if (!isInFrustum(transform, textureComponent)) return

        if (Mappers.colorComponent.has(entity)) {
            val colorComponent = Mappers.colorComponent.get(entity)
            batch.color = colorComponent.color
        } else
            batch.color = Color.WHITE

        val scale = transform.scale
        val width = textureComponent.region!!.regionWidth.toFloat()
        val height = (textureComponent.region as TextureRegion).regionHeight.toFloat()
        val originX = 0.5f * width
        val originY = 0.5f * height

        batch.draw(textureComponent.region,
                transform.x - originX,
                transform.y - originY,
                originX,
                originY,
                width,
                height,
                scale,
                scale,
                transform.angle)
    }

    private fun isInFrustum(transform: Transform, textureComponent: TextureComponent): Boolean {
        val width = textureComponent.region!!.regionWidth.toFloat()
        val height = (textureComponent.region as TextureRegion).regionHeight.toFloat()
        val originX = width * 0.5f
        val originY = height * 0.5f
        val scale = transform.scale
        val halfWidth = camera.viewportWidth * camera.zoom * 0.5f
        val halfHeight = camera.viewportHeight * camera.zoom * 0.5f

        if (transform.x + width * scale - originX < camera.position.x - halfWidth
                || transform.x - originX > camera.position.x + halfWidth)
            return false
        if (transform.y + height * scale - originY < camera.position.y - halfHeight
                || transform.y - originY > camera.position.y + halfHeight)
            return false

        return true
    }

    private class ZComparator : Comparator<Entity> {
        override fun compare(e1: Entity, e2: Entity): Int {
            return Integer.signum(Mappers.transform.get(e1).z - Mappers.transform.get(e2).z)
        }
    }
}
