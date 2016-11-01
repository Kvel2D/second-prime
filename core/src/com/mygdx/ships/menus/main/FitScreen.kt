package com.mygdx.ships.menus.main

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.mygdx.ships.*
import com.mygdx.ships.entity.EntityFactory
import com.mygdx.ships.entity.systems.TetherSystem
import com.mygdx.ships.entity.systems.WeaponSystem
import com.mygdx.ships.menus.Button
import com.mygdx.ships.menus.GunIcon


class FitScreen(menuScreen: MenuScreen, gameScreen: GameScreen) : SlidingScreen(menuScreen) {
    internal enum class MOUSE_STATE {
        IDLE,
        DRAG,
        ROTATE
    }
    internal var mouseState = MOUSE_STATE.IDLE

    internal val batch: SpriteBatch
    internal val shapeRenderer: ShapeRenderer
    internal val font: BitmapFont
    internal val layout: GlyphLayout
    internal val mouseLeft: Texture
    internal val mouseRight: Texture
    internal val save: Button
    internal var guns = arrayListOf<GunIcon>()
    internal val gunLines: FloatArray
    var shipVertices: FloatArray

    init {
        batch = Main.spriteBatch
        shapeRenderer = ShapeRenderer()
        font = Main.assets.get<BitmapFont>(AssetPaths.FONT)
        layout = GlyphLayout()

        mouseLeft = Main.assets.get(AssetPaths.MOUSE_LEFT)
        mouseRight = Main.assets.get(AssetPaths.MOUSE_RIGHT)

        layout.setText(font, "Save")
        save = Button("Save", camWidth * 3 / 2 - layout.width / 2, 50f, layout, font)

        val file = Gdx.files.local(AssetPaths.SHIP1_LINES)
        shipVertices = file.toVertices()
        shipVertices.scale(3f)
        shipVertices.translate(camWidth * 3 / 2, 400f, 0f)

        val defaultGun = GunIcon(780f + camWidth, 400f, 0f)
        defaultGun.myEntity = gameScreen.guns[0]
        guns.add(defaultGun)

        val lineFile = Gdx.files.internal(AssetPaths.GUN_LINES)
        gunLines = lineFile.toVertices()
    }

    override fun render(deltaTime: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        when (state) {
            SCREEN_STATE.START -> {
                menuScreen.draw(deltaTime)
                draw()
                transitionForward()
            }
            SCREEN_STATE.NORMAL -> {
                draw()
                update()
            }
            SCREEN_STATE.END -> {
                menuScreen.draw(deltaTime)
                draw()
                transitionBack()
            }
        }
    }

    override fun draw() {
        camera.update()
        batch.projectionMatrix = camera.combined
        batch.begin()
        batch.enableBlending()

        val touch = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        camera.unproject(touch)

        save.draw(batch, touch, font)

        font.color = Color.LIGHT_GRAY
        batch.draw(mouseLeft, camWidth + 470f, 650f)
        batch.draw(mouseRight, camWidth + 620f, 650f)

        font.data.setScale(0.75f)
        font.draw(batch, "DRAG           ROTATE", camWidth + 520f, 685f)
        font.data.setScale(1f)

        batch.end()

        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = Color.WHITE

        var j = 0
        while (j < shipVertices.size) {
            shapeRenderer.line(shipVertices[j], shipVertices[j + 1], shipVertices[j + 2], shipVertices[j + 3])
            j += 4
        }

        val gunLines = this.gunLines.clone()
        gunLines.scale(3f)
        guns.forEach {
            shapeRenderer.color = Color.WHITE
            if (it.dragged) {
                shapeRenderer.color = Color.RED

                val boxVertices = floatArrayOf(it.position.x - 15f, it.position.y - 15f,
                        it.position.x - 15f, it.position.y + 15f,
                        it.position.x + 15f, it.position.y + 15f,
                        it.position.x + 15f, it.position.y - 15f)
                val gunHitbox = Polygon(boxVertices)
                var intersecting = false
                var j = 0
                while (j < shipVertices.size) {
                    if (Intersector.intersectSegmentPolygon(Vector2(shipVertices[j], shipVertices[j + 1]),
                            Vector2(shipVertices[j + 2], shipVertices[j + 3]), gunHitbox))
                        intersecting = true
                    j += 4
                }
                if (intersecting) shapeRenderer.color = Color.GREEN
            }
            gunLines.translate(it.position.x, it.position.y, it.angle)
            var i = 0
            while (i < gunLines.size) {
                shapeRenderer.line(gunLines[i], gunLines[i + 1], gunLines[i + 2], gunLines[i + 3])
                i += 4
            }
            gunLines.translate(-it.position.x, -it.position.y, 0f)
            gunLines.translate(0f, 0f, -it.angle)
        }

        shapeRenderer.end()
    }

    override fun update() {
        if (Gdx.input.isTouched) {
            val touch = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            camera.unproject(touch)

            if (save.bounds.contains(touch.x, touch.y))
            {
                this.state = SCREEN_STATE.END
                return
            }

            if (mouseState == MOUSE_STATE.IDLE) {
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                    var closestGun = findClosestGun(touch.x, touch.y, 40f)

                    if (closestGun != null) {
                        mouseState = MOUSE_STATE.DRAG
                        closestGun.dragged = true;
                        closestGun.cursorOffset = Vector2(closestGun.position.x - touch.x,
                                closestGun.position.y - touch.y)

                        val myEntity = closestGun.myEntity
                        if (myEntity != null) {
                            Main.gameScreen.guns.remove(myEntity)
                            Main.engine.getSystem(TetherSystem::class.java).untether(myEntity)
                            Main.engine.removeEntity(myEntity)
                            closestGun.myEntity = null
                        }
                    }

                    return
                }
                else if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                    var closestGun = findClosestGun(touch.x, touch.y, 50f)

                    if (closestGun != null) {
                        mouseState = MOUSE_STATE.ROTATE
                        closestGun.rotated = true;

                        val myEntity = closestGun.myEntity
                        if (myEntity != null) {
                            Main.gameScreen.guns.remove(myEntity)
                            Main.engine.getSystem(TetherSystem::class.java).untether(myEntity)
                            Main.engine.removeEntity(myEntity)
                            closestGun.myEntity = null
                        }
                    }

                    return
                }
            }
        } else {
            if (mouseState == MOUSE_STATE.DRAG || mouseState == MOUSE_STATE.ROTATE) {
                mouseState = MOUSE_STATE.IDLE

                val touch = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
                camera.unproject(touch)

                guns.forEach {
                    if (it.dragged || it.rotated) {
                        if (it.myEntity == null) {
                            val boxVertices = floatArrayOf(it.position.x - 15f, it.position.y - 15f,
                                    it.position.x - 15f, it.position.y + 15f,
                                    it.position.x + 15f, it.position.y + 15f,
                                    it.position.x + 15f, it.position.y - 15f)
                            val gunHitbox = Polygon(boxVertices)
                            var intersecting = false
                            var j = 0
                            while (j < shipVertices.size) {
                                if (Intersector.intersectSegmentPolygon(Vector2(shipVertices[j], shipVertices[j + 1]),
                                        Vector2(shipVertices[j + 2], shipVertices[j + 3]), gunHitbox))
                                    intersecting = true
                                j += 4
                            }

                            if (intersecting) {

                                val offset = Vector2(it.position.x - camWidth * 3 / 2, it.position.y - camHeight / 2 - 40f)
                                offset.scl(1 / 3f)
                                val gun = EntityFactory.gunDefault(Main.gameScreen.player, offset, it.angle, false)
                                it.myEntity = gun
                                Main.gameScreen.guns.add(gun)

                                // Update tethers to place the gun correctly before resuming the game
                                val ts = Main.engine.getSystem(TetherSystem::class.java)
                                ts.update(0.016f)

                                if (it.dragged)
                                    it.position.set(touch.x + it.cursorOffset.x, touch.y + it.cursorOffset.y)
                            } else // if gun wasn't successfully placed, return it to the board
                                it.resetPosition()

                        }

                        it.rotated = false
                        it.dragged = false
                    }
                }
            }
        }

        val touch = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        camera.unproject(touch)

        if (mouseState == MOUSE_STATE.DRAG) {
            guns.forEach {
                if (it.dragged) {
                    it.position.set(touch.x + it.cursorOffset.x, touch.y + it.cursorOffset.y)
                }
            }
        }

        if (mouseState == MOUSE_STATE.ROTATE) {
            guns.forEach {
                if (it.rotated) {
                    val angle = Math.atan2((touch.y - it.position.y).toDouble(), (touch.x - it.position.x).toDouble())
                    it.angle = angle.toFloat() / Constants.DEGTORAD
                }
            }
        }
    }

    internal fun findClosestGun(x: Float, y: Float, maxRadius: Float): GunIcon? {
        var closestGun: GunIcon? = null;
        var closestDistance = maxRadius * maxRadius

        guns.forEach {
            if (it.position.dst2(x, y) < closestDistance) {
                closestGun = it
                closestDistance = it.position.dst2(x, y)
            }
        }

        return closestGun
    }

    override fun hide() {
        // Reset weapon charge
        Main.engine.getSystem(WeaponSystem::class.java).update(10f)
    }

    fun addGun() {
        val xOffset = 100f + guns.size * 50f

        guns.add(GunIcon(camWidth + xOffset, 100f, 0f))
    }

    fun reset() {
        guns.clear()
        val defaultGun = GunIcon(780f + camWidth, 400f, 0f)
        defaultGun.myEntity = Main.gameScreen.guns[0]
        guns.add(defaultGun)
    }
}
