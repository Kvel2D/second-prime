package com.mygdx.ships.entity

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.mygdx.ships.*
import com.mygdx.ships.entity.components.*
import com.mygdx.ships.entity.components.Transform
import com.mygdx.ships.entity.systems.RenderSystem

object EntityFactory {
    internal var engine = Main.engine
    internal var box2dWorld = Main.world

    fun player(x: Float, y: Float): Entity {
        val e = engine.createEntity()

        val mainTransform = engine.createComponent(Transform::class.java)
        mainTransform.set(x, y, 0, 0f, 1f)
        val playerMovement = engine.createComponent(PlayerMovement::class.java)
        playerMovement.set(Constants.FORWARD_MAGNITUDE_DEFAULT, Constants.TURN_MAGNITUDE_DEFAULT, -7f)
        val leaderComponent = engine.createComponent(LeaderComponent::class.java)
        leaderComponent.set(Constants.ATTRACTION_DEFAULT, Constants.PLAYER_RADIUS)
        val colorComponent = engine.createComponent(ColorComponent::class.java)
        colorComponent.set(Color.WHITE)

        e.add(mainTransform)
                .add(colorComponent)
                .add(playerMovement)
                .add(leaderComponent)
        engine.addEntity(e)

        return e
    }

    fun createPlayerBodyNormal(entity: Entity, x: Float, y: Float): Body {
        val texture = Main.assets.get<Texture>(AssetPaths.SHIP1)

        val width = texture.width * Constants.METER_TO_PIXEL
        val height = texture.height * Constants.METER_TO_PIXEL
        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.position.set(x, y)
        val rectangle = PolygonShape()
        rectangle.setAsBox(width / 2, height / 2) //setAsBox() takes half-width and half-height
        val fixtureDef = FixtureDef()
        fixtureDef.shape = rectangle
        fixtureDef.density = 12.5f
        fixtureDef.friction = 0f
        fixtureDef.filter.groupIndex = Constants.IGNORE_PLAYER
        val body = box2dWorld.createBody(bodyDef)
        val fixture = body.createFixture(fixtureDef)
        fixture.userData = entity
        body.linearDamping = 1f
        body.angularDamping = 0.5f
        body.userData = entity
        rectangle.dispose()

        return body
    }

    fun createPlayerBodySmall(entity: Entity, x: Float, y: Float): Body {
        val texture = Main.assets.get<Texture>(AssetPaths.SHIP1)

        var width = 55 * Constants.METER_TO_PIXEL
        var height = texture.height * Constants.METER_TO_PIXEL
        val xOffset = -(100 - 55) / 2f * Constants.METER_TO_PIXEL
        val yOffset = 0f
        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.position.set(x, y)
        val rectangle = PolygonShape()
        rectangle.setAsBox(width / 2, height / 2, Vector2(xOffset, yOffset), 0f) //setAsBox() takes half-width and half-height
        val fixtureDef = FixtureDef()
        fixtureDef.shape = rectangle
        fixtureDef.density = 17f
        fixtureDef.friction = 0f
        fixtureDef.filter.groupIndex = Constants.IGNORE_PLAYER
        val body = box2dWorld.createBody(bodyDef)
        val fixture = body.createFixture(fixtureDef)
        fixture.userData = entity

        width = 100 * Constants.METER_TO_PIXEL
        height = 10 * Constants.METER_TO_PIXEL
        rectangle.setAsBox(width / 2, height / 2) //setAsBox() takes half-width and half-height
        fixtureDef.shape = rectangle
        val fixture2 = body.createFixture(fixtureDef)
        fixture2.userData = entity

        body.linearDamping = 1f
        body.angularDamping = 0.5f
        body.userData = entity
        rectangle.dispose()

        return body
    }

    fun gun(tetherEntity: Entity,
            offset: Vector2,
            angleOffset: Float,
            visualSize: Float,
            bodySize: Float,
            explosionSize: Float,
            explosionVertices: Int,
            particleForce: Float,
            particleDuration: Float,
            weaponChargeTime: Float,
            animated: Boolean,
            color: Color): Entity {
        val gun = engine.createEntity()

        run {
            val polygonVisual = createSquarePolygon(0f, 0f, visualSize)
            val polygonBody = createSquarePolygon(0f, 0f, bodySize)
            val polygonExplosion = createCirclePolygonRandom(explosionVertices, Vector2(0f, 0f), explosionSize)
            val animation = FloatArray(polygonExplosion.size)
            System.arraycopy(polygonExplosion, 0, animation, 0, polygonExplosion.size)
            animation.scale(0.3f)

            val transform = engine.createComponent(Transform::class.java)
            transform.set(0f, 0f, 0, 0f, 1f)
            val weaponComponent = engine.createComponent(WeaponComponent::class.java)
            weaponComponent.set(polygonVisual, polygonBody, polygonExplosion,
                    particleDuration, particleForce, weaponChargeTime, color)
            val tetherComponent = engine.createComponent(TetherComponent::class.java)
            tetherComponent.set(tetherEntity, offset.x, offset.y, angleOffset, true)

            gun.add(transform)
                    .add(weaponComponent)
                    .add(tetherComponent)
            if (animated) {
                val weaponAnimation = engine.createComponent(WeaponAnimation::class.java)
                weaponAnimation.set(animation, 1.2f, 0.3f)
                val lineComponent = engine.createComponent(LineComponent::class.java)
                lineComponent.set(animation)
                val colorComponent = engine.createComponent(ColorComponent::class.java)
                colorComponent.set(Color.BLACK)
                gun.add(weaponComponent)
                        .add(lineComponent)
                        .add(weaponAnimation)
                        .add(colorComponent)
            }
            engine.addEntity(gun)
        }

        val gunImage = engine.createEntity()

        run {
            val lineFile = Gdx.files.internal(AssetPaths.GUN_LINES)
            val gunVisual = lineFile.toVertices()
            val transform = engine.createComponent(Transform::class.java)
            val lineComponent = engine.createComponent(LineComponent::class.java)
            lineComponent.set(gunVisual)
            val tetherComponent = engine.createComponent(TetherComponent::class.java)
            tetherComponent.set(gun, 0f, 0f, 0f, true)
            val colorComponent = engine.createComponent(ColorComponent::class.java)
            colorComponent.set(Color.WHITE)

            gunImage.add(transform)
                    .add(lineComponent)
                    .add(tetherComponent)
                    .add(colorComponent)
            engine.addEntity(gunImage)
        }

        return gun
    }

    fun gunDefault(tetherEntity: Entity, offset: Vector2, angleOffset: Float, animated: Boolean): Entity {
        val visualSize = 10f
        val bodySize = visualSize
        val explosionSize = Constants.WEAPON_RADIUS_CURRENT
        val explosionVerticesNumber = 12
        val particleForce = bodySize * bodySize / 20 * Constants.FIRE_FORCE_CURRENT
        val particleDuration = 5f
        val color = Constants.PROJECTILE_COLOR_CURRENT
        val weaponChargeTime = Constants.WEAPON_CHARGE_CURRENT

        val gun = gun(tetherEntity, offset, angleOffset, visualSize, bodySize,
                explosionSize, explosionVerticesNumber, particleForce, particleDuration,
                weaponChargeTime, animated, color)
        return gun
    }

    fun timebomb(tetherEntity: Entity): Entity {
        val timebomb = engine.createEntity()
        val explosion = createCirclePolygonRandom(12, Vector2(0f, 0f), Constants.BOMB_RADIUS_CURRENT)
        val chargeTime = Constants.BOMB_CHARGE_CURRENT

        val transform = engine.createComponent(Transform::class.java)
        transform.set(0f, 0f, 0, 0f, 1f)
        val bombComponent = engine.createComponent(BombComponent::class.java)
        bombComponent.set(explosion, chargeTime)
        val tetherComponent = engine.createComponent(TetherComponent::class.java)
        tetherComponent.set(tetherEntity, 0f, 0f, 0f, true)

        timebomb.add(transform)
                .add(bombComponent)
                .add(tetherComponent)

        engine.addEntity(timebomb)

        return timebomb
    }

    fun thruster(tetherEntity: Entity, xOffset: Float, yOffset: Float, angleOffset: Float, charge: Float): Entity {
        val thruster = engine.createEntity()
        val particlePolygon = createSquarePolygon(0f, 0f, 10f)

        val transform = engine.createComponent(Transform::class.java)
        transform.set(0f, 0f, 0, 0f, 1f)
        val tetherComponent = engine.createComponent(TetherComponent::class.java)
        tetherComponent.set(tetherEntity, xOffset, yOffset, angleOffset, true)
        val particleEmitter = engine.createComponent(ParticleEmitter::class.java)
        particleEmitter.set(particlePolygon, 0.9f, charge)

        thruster.add(transform).add(tetherComponent).add(particleEmitter)
        engine.addEntity(thruster)

        return thruster
    }

    fun polygon(vertices: FloatArray): Entity {
        val e = engine.createEntity()

        val bodyDef = BodyDef()
        bodyDef.position.set(0f, 0f)
        bodyDef.type = BodyDef.BodyType.StaticBody
        val polygon = PolygonShape()
        for (i in 0..vertices.size - 1 - 1) {
            vertices[i] /= Constants.METER_TO_PIXEL
        }
        polygon.set(vertices)
        val fixtureDef = FixtureDef()
        fixtureDef.shape = polygon
        fixtureDef.density = 10f
        val body = box2dWorld.createBody(bodyDef)
        body.createFixture(fixtureDef)
        body.userData = e
        polygon.dispose()

        val bodyComponent = engine.createComponent(BodyComponent::class.java)
        bodyComponent.set(body)

        e.add(bodyComponent)
        engine.addEntity(e)

        return e
    }

    fun terrain(polygon: FloatArray, type: String, level: Int, color: Color): Entity {
        val e = engine.createEntity()

        val body = createChainBody(polygon, false)
        body.userData = e

        val polygonComponent = engine.createComponent(PolygonComponent::class.java)
        polygonComponent.set(polygon)
        val terrainComponent = engine.createComponent(TerrainComponent::class.java)
        terrainComponent.set(type, level)
        val bodyComponent = engine.createComponent(BodyComponent::class.java)
        bodyComponent.set(body)
        val colorComponent = engine.createComponent(ColorComponent::class.java)
        colorComponent.set(color.cpy())
        val transform = engine.createComponent(Transform::class.java)
        transform.set(0f, 0f, 0, 0f, 0f)

        e.add(polygonComponent)
                .add(terrainComponent)
                .add(bodyComponent)
                .add(colorComponent)
                .add(transform)

        if (type == "teal") {
            val sapComponent = engine.createComponent(SapComponent::class.java)
            e.add(sapComponent)
        }

        return e
    }

    fun updateTerrain(entity: Entity, polygon: FloatArray) {
        val polygonComponent = Mappers.polygonComponent.get(entity)
        val bodyComponent = Mappers.bodyComponent.get(entity)

        polygonComponent.polygon = polygon
        val body = createChainBody(polygon, false)
        body.userData = entity
        box2dWorld.destroyBody(bodyComponent.body)
        bodyComponent.body = body
    }

    fun createChainBody(vertices: FloatArray, dynamic: Boolean): Body {
        val bodyDef = BodyDef()
        if (dynamic)
            bodyDef.type = BodyDef.BodyType.DynamicBody
        else
            bodyDef.type = BodyDef.BodyType.StaticBody
        bodyDef.linearDamping = 0.5f
        val body = box2dWorld.createBody(bodyDef)

        val fixtureDef = FixtureDef()
        fixtureDef.restitution = 0f
        fixtureDef.friction = 1f
        fixtureDef.density = 100f
        val chain = ChainShape()
        val verticesConverted = FloatArray(vertices.size)
        System.arraycopy(vertices, 0, verticesConverted, 0, vertices.size)
        var i = 0
        while (i < verticesConverted.size) {
            verticesConverted[i] *= Constants.METER_TO_PIXEL
            verticesConverted[i + 1] *= Constants.METER_TO_PIXEL
            i += 2
        }
        chain.createLoop(verticesConverted)
        fixtureDef.shape = chain
        body.createFixture(fixtureDef)
        chain.dispose()

        return body
    }

    fun projectile(polygonVisual: FloatArray,
                   polygonBody: FloatArray,
                   polygonExplosion: FloatArray,
                   duration: Float,
                   color: Color,
                   x: Float,
                   y: Float,
                   velocity: Float,
                   angle: Float): Entity {
        var duration = duration
        val e = engine.createEntity()

        val bodyDef = BodyDef()
        bodyDef.type = BodyDef.BodyType.DynamicBody
        bodyDef.position.set(x * Constants.METER_TO_PIXEL, y * Constants.METER_TO_PIXEL)
        val body = box2dWorld.createBody(bodyDef)

        val fixtureDef = FixtureDef()
        fixtureDef.restitution = 0f
        fixtureDef.friction = 1f
        fixtureDef.density = 1f
        fixtureDef.filter.groupIndex = Constants.IGNORE_PLAYER
        val shape = PolygonShape()
        val verticesConverted = FloatArray(polygonBody.size)
        System.arraycopy(polygonBody, 0, verticesConverted, 0, polygonBody.size)
        var i = 0
        while (i < verticesConverted.size - 1) {
            verticesConverted[i] *= Constants.METER_TO_PIXEL
            verticesConverted[i + 1] *= Constants.METER_TO_PIXEL
            i += 2
        }
        shape.set(verticesConverted)
        fixtureDef.shape = shape
        body.createFixture(fixtureDef)
        shape.dispose()

        val force = Vector2(velocity, 0f)
        force.rotate(angle)

        body.userData = e
        body.applyForce(force, Vector2(x, y), true)
        body.isFixedRotation = true

        val explosion = FloatArray(polygonExplosion.size)
        System.arraycopy(polygonExplosion, 0, explosion, 0, polygonExplosion.size)
        // Random angle is 0 to the value of explosion polygon's sector arc
        val randomAngle = (Math.random() * 360.0 / (explosion.size / 2)).toFloat()
        explosion.rotateAround(0f, 0f, randomAngle)

        val transform = engine.createComponent(Transform::class.java)
        transform.set(x, y, 0, angle, 1f)
        val bodyComponent = engine.createComponent(BodyComponent::class.java)
        bodyComponent.set(body)
        val explosionComponent = engine.createComponent(ExplosionComponent::class.java)
        explosionComponent.set(explosion, angle)
        val limitedLifespan = engine.createComponent(LimitedLifespan::class.java)
        limitedLifespan.set(duration)
        val polygonComponent = engine.createComponent(PolygonComponent::class.java)
        polygonComponent.set(polygonVisual)
        val colorComponent = engine.createComponent(ColorComponent::class.java)
        colorComponent.set(color)

        e.add(transform)
                .add(bodyComponent)
                .add(explosionComponent)
                .add(limitedLifespan)
                .add(polygonComponent)
                .add(colorComponent)
        engine.addEntity(e)

        return e
    }

    fun particle(vertices: FloatArray, duration: Float,
                 x: Float, y: Float): Entity {
        val e = engine.createEntity()

        val transform = engine.createComponent(Transform::class.java)
        transform.set(x, y, 0, 0f, 1f)
        val limitedLifespan = engine.createComponent(LimitedLifespan::class.java)
        limitedLifespan.set(duration)
        val polygonComponent = engine.createComponent(PolygonComponent::class.java)
        polygonComponent.set(vertices)
        val colorComponent = engine.createComponent(ColorComponent::class.java)
        colorComponent.set(Color(1f, 0.8f, 0f, 1f))
        val fadeComponent = engine.createComponent(FadeComponent::class.java)
        fadeComponent.set(duration)

        e.add(transform)
                .add(limitedLifespan)
                .add(polygonComponent)
                .add(colorComponent)
                .add(fadeComponent)
        if (!engine.entities.contains(e))
            engine.addEntity(e)

        return e
    }

    fun energyBeam(p1: Vector2, p2: Vector2, color: Color, duration: Float): Entity {
        val e = engine.createEntity()

        val line = floatArrayOf(p1.x + (Math.random() * 50).toFloat() - 25f,
                p1.y + (Math.random() * 50).toFloat() - 25f,
                p2.x + (Math.random() * 20).toFloat() - 10f,
                p2.y + (Math.random() * 20).toFloat() - 10f)

        val transform = engine.createComponent(Transform::class.java)
        transform.set(0f, 0f, 0, 0f, 1f)
        val limitedLifespan = engine.createComponent(LimitedLifespan::class.java)
        limitedLifespan.set(duration)
        val lineComponent = engine.createComponent(LineComponent::class.java)
        lineComponent.set(line)
        val colorComponent = engine.createComponent(ColorComponent::class.java)
        colorComponent.set(color)
        val fadeComponent = engine.createComponent(FadeComponent::class.java)
        fadeComponent.set(0.05f)

        e.add(transform).add(limitedLifespan).add(lineComponent).add(colorComponent).add(fadeComponent)
        engine.addEntity(e)

        return e
    }

    fun ore(vertices: FloatArray, duration: Float, color: Color, type: String, x: Float, y: Float): Entity {
        val e = engine.createEntity()

        val transform = engine.createComponent(Transform::class.java)
        transform.set(x, y, 0, 0f, 1f)
        val limitedLifespan = engine.createComponent(LimitedLifespan::class.java)
        limitedLifespan.set(duration)
        val fadeComponent = engine.createComponent(FadeComponent::class.java)
        fadeComponent.set(duration)
        val polygonComponent = engine.createComponent(PolygonComponent::class.java)
        polygonComponent.set(vertices)
        val colorComponent = engine.createComponent(ColorComponent::class.java)
        colorComponent.set(Color(color.cpy()))
        val followerComponent = engine.createComponent(FollowerComponent::class.java)
        followerComponent.set(0.2f)
        val scoreComponent = engine.createComponent(ScoreComponent::class.java)
        scoreComponent.set(type)

        e.add(transform)
                .add(limitedLifespan)
                .add(polygonComponent)
                .add(colorComponent)
                .add(followerComponent)
                .add(scoreComponent)
        engine.addEntity(e)

        return e
    }

    fun bubble(origin: Vector2, radius: Float): Entity {
        val e = engine.createEntity()

        val vertices = createCirclePolygon(8, Vector2(0f, 0f), radius);

        val transform = engine.createComponent(Transform::class.java)
        transform.set(origin.x, origin.y, 0, 0f, 1f)
        val limitedLifespan = engine.createComponent(LimitedLifespan::class.java)
        limitedLifespan.set(0.2f)
        val fadeComponent = engine.createComponent(FadeComponent::class.java)
        fadeComponent.set(0.2f)
        val polygonComponent = engine.createComponent(PolygonComponent::class.java)
        polygonComponent.set(vertices)
        val colorComponent = engine.createComponent(ColorComponent::class.java)
        colorComponent.set(Color.LIGHT_GRAY)
        val expandComponent = engine.createComponent(ExpandComponent::class.java)
        expandComponent.set(0.13f);

        e.add(transform)
                .add(limitedLifespan)
                .add(polygonComponent)
                .add(colorComponent)
                .add(fadeComponent)
                .add(expandComponent)
        engine.addEntity(e)

        return e
    }

    fun camera(target: Entity, camera: OrthographicCamera, x: Float, y: Float): Entity {
        val cameraEntity = engine.createEntity()

        camera.position.set(x / Constants.METER_TO_PIXEL, y / Constants.METER_TO_PIXEL, 0f)
        val cameraComponent = engine.createComponent(CameraComponent::class.java)
        cameraComponent.set(target, camera)
        cameraEntity.add(cameraComponent)
        Main.engine.addEntity(cameraEntity)

        return cameraEntity
    }

    fun base(x: Float, y: Float): Entity {
        val base = engine.createEntity()

        val bodyFile = Gdx.files.local(AssetPaths.BASE_BODY)
        val polygon = bodyFile.toVertices()
        val body = createChainBody(polygon, false)
        body.userData = base

        val lineFile = Gdx.files.local(AssetPaths.BASE_LINES)
        val lines = lineFile.toVertices()

        val transform = engine.createComponent(Transform::class.java)
        transform.set(x, y, 0, 0f, 1f)
        val bodyComponent = engine.createComponent(BodyComponent::class.java)
        bodyComponent.set(body)
        val colorComponent = engine.createComponent(ColorComponent::class.java)
        colorComponent.set(Color.WHITE)
        val lineComponent = engine.createComponent(LineComponent::class.java)
        lineComponent.set(lines)

        base.add(transform)
                .add(colorComponent)
                .add(lineComponent)
                .add(bodyComponent)
        engine.addEntity(base)

        return base
    }

    fun borders() {
        run {
            val borderLeft = engine.createEntity()

            val width = 1f
            val height = 3000f
            val bodyDef = BodyDef()
            bodyDef.type = BodyDef.BodyType.StaticBody
            bodyDef.position.set(1002 * Constants.METER_TO_PIXEL, 0f)
            val rectangle = PolygonShape()
            rectangle.setAsBox(width / 2, height / 2) //setAsBox() takes half-width and half-height
            val fixtureDef = FixtureDef()
            fixtureDef.shape = rectangle
            fixtureDef.density = 10f
            fixtureDef.friction = 0f
            val body = box2dWorld.createBody(bodyDef)
            val fixture = body.createFixture(fixtureDef)
            fixture.userData = borderLeft
            body.linearDamping = 1f
            body.angularDamping = 0.5f
            body.userData = borderLeft
            rectangle.dispose()

            val bodyComponent = engine.createComponent(BodyComponent::class.java)
            bodyComponent.set(body)
            val transform = engine.createComponent(Transform::class.java)
            transform.set(0f, 0f, 0, 0f, 0f)
            val terrainComponent = engine.createComponent(TerrainComponent::class.java)
            terrainComponent.set("unobtanium", 1)

            borderLeft.add(bodyComponent)
                    .add(transform)
                    .add(terrainComponent)
            engine.addEntity(borderLeft)
        }

        run {
            val borderRight = engine.createEntity()

            val width = 1f
            val height = 3000f
            val bodyDef = BodyDef()
            bodyDef.type = BodyDef.BodyType.StaticBody
            bodyDef.position.set(-1002 * Constants.METER_TO_PIXEL, 0f)
            val rectangle = PolygonShape()
            rectangle.setAsBox(width / 2, height / 2) //setAsBox() takes half-width and half-height
            val fixtureDef = FixtureDef()
            fixtureDef.shape = rectangle
            fixtureDef.density = 10f
            fixtureDef.friction = 0f
            val body = box2dWorld.createBody(bodyDef)
            val fixture = body.createFixture(fixtureDef)
            fixture.userData = borderRight
            body.linearDamping = 1f
            body.angularDamping = 0.5f
            body.userData = borderRight
            rectangle.dispose()

            val bodyComponent = engine.createComponent(BodyComponent::class.java)
            bodyComponent.set(body)
            val transform = engine.createComponent(Transform::class.java)
            transform.set(0f, 0f, 0, 0f, 0f)
            val terrainComponent = engine.createComponent(TerrainComponent::class.java)
            terrainComponent.set("unobtanium", 1)

            borderRight.add(bodyComponent)
                    .add(transform)
                    .add(terrainComponent)
            engine.addEntity(borderRight)
        }
    }

    fun createCirclePolygon(precision: Int, origin: Vector2, radius: Float): FloatArray {
        val angle = (2 * Math.PI / precision).toFloat()
        val vertices = FloatArray(precision * 2)
        for (i in 0..precision - 1) {
            vertices[i * 2] = (origin.x + radius * Math.cos((angle * i).toDouble())).toFloat()
            vertices[i * 2 + 1] = (origin.y + radius * Math.sin((angle * i).toDouble())).toFloat()
        }

        return vertices
    }

    fun createCirclePolygonRandom(precision: Int, origin: Vector2, radius: Float): FloatArray {
        val vertices = createCirclePolygon(precision, origin, radius)

        val angle = Math.random().toFloat() * 30
        var i = 0
        while (i < precision) {
            val p = Vector2(vertices[i], vertices[i + 1])

            p.rotateAround(0f, 0f, angle)

            vertices[i] = p.x
            vertices[i + 1] = p.y
            i += 2
        }

        return vertices
    }

    fun createSquarePolygon(x: Float, y: Float, sideLength: Float): FloatArray {
        val vertices = floatArrayOf(x - sideLength / 2, y - sideLength / 2, x - sideLength / 2, y + sideLength / 2, x + sideLength / 2, y + sideLength / 2, x + sideLength / 2, y - sideLength / 2)

        return vertices
    }
}
