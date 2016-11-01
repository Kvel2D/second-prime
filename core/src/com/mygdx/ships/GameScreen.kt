package com.mygdx.ships

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.mygdx.ships.entity.EntityFactory
import com.mygdx.ships.entity.Mappers
import com.mygdx.ships.entity.components.BodyComponent
import com.mygdx.ships.entity.components.LineComponent
import com.mygdx.ships.entity.components.TerrainComponent
import com.mygdx.ships.entity.systems.*
import com.mygdx.ships.menus.Button
import java.util.*

class GameScreen : ScreenAdapter() {
    enum class SCREEN_STATE {
        NORMAL,
        RESTART
    }

    var state = SCREEN_STATE.NORMAL

    val gameCamera: OrthographicCamera
    internal val batch = Main.spriteBatch
    internal val hudCamera: OrthographicCamera
    internal val font: BitmapFont = Main.assets.get(AssetPaths.FONT)
    internal val fontBig: BitmapFont = Main.assets.get(AssetPaths.FONT_BIG)
    internal val layout = GlyphLayout()
    internal var gameOver: Texture
    internal val retry: Button
    internal var fadein = 0.1f
    internal var engine = Main.engine
    internal var menuKeyWasPressed = false

    val hud: Hud
    var player: Entity
    var base: Entity
    var guns: MutableList<Entity> = ArrayList()
    var timebomb: Entity? = null

    val editorBounds = Rectangle(600f, 680f, 100f, 50f)

    init {
        val camWidth = Gdx.graphics.width.toFloat()
        val camHeight = Gdx.graphics.height.toFloat()
        hudCamera = OrthographicCamera(camWidth, camHeight)
        hudCamera.position.set(camWidth / 2, camHeight / 2, 0f)
        hudCamera.update()

        hud = Hud(hudCamera)

        gameCamera = OrthographicCamera(camWidth, camHeight)
        gameCamera.zoom = 1.5f
        gameCamera.position.set(0f, 0f, 0f)
        gameCamera.update()

        gameOver = Main.assets.get(AssetPaths.MENU_BACKGROUND)

        layout.setText(font, "Retry")
        retry = Button("Retry", camWidth / 2, 100f, 0, layout, font)

        player = EntityFactory.player(0f, 3f)
        base = EntityFactory.base(0f, 0f)
        val gun = EntityFactory.gunDefault(player, Vector2(45f, 0f), 0f, true)
        guns.add(gun)
        EntityFactory.camera(player, gameCamera, 0f, 3.3f)
        generateTerrain360()

        engine.addSystem(RenderSystem(Main.spriteBatch, gameCamera))
        engine.addSystem(TerrainSystem(gameCamera))
        engine.addSystem(TetherSystem())
        engine.addSystem(CameraSystem())
        engine.addSystem(EmitterSystem())
        engine.addSystem(FadeSystem())
        engine.addSystem(RemovalSystem())
        engine.addSystem(WeaponAnimationSystem())
        engine.addSystem(WeaponSystem(1))
        engine.addSystem(CollisionSystem(Main.world))
        engine.addSystem(PhysicsSystem(10, Main.world))

        engine.addSystem(ShapeRenderSystem(gameCamera))
        engine.addSystem(FollowingSystem(player, base))
        engine.addSystem(ScoreSystem(base))
        engine.addSystem(SapSystem(player))
        engine.addSystem(ExpandSystem())
        engine.addSystem(PlayerMovementSystem(0))
        engine.addSystem(ExplosionSystem(
                engine.getSystem(TerrainSystem::class.java),
                engine.getSystem(CameraSystem::class.java)))
        engine.addSystem(BombSystem(
                engine.getSystem(TerrainSystem::class.java),
                engine.getSystem(CameraSystem::class.java)))
    }

    override fun render(deltaTime: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        when (state) {
            SCREEN_STATE.NORMAL -> {
                GameData.update(deltaTime)
                engine.update(deltaTime)
                hud.render()
                updateNormal()
            }
            SCREEN_STATE.RESTART -> {
                engine.update(deltaTime)
                hud.render()
                drawRestart()
                updateRestart()
            }
        }
    }

    private fun updateNormal() {
        if (Gdx.input.isKeyPressed(GameData.Controls.MENU)) {
            if (!menuKeyWasPressed) {
                val systems = Main.engine.systems
                for (i in 0..systems.size() - 1) {
                    systems.get(i).setProcessing(false)
                }
                engine.getSystem(RenderSystem::class.java).setProcessing(true)
                engine.getSystem(ShapeRenderSystem::class.java).setProcessing(true)
                Main.game.screen = Main.menuScreen
            }
            menuKeyWasPressed = true
        } else
            menuKeyWasPressed = false

        if (GameData.editor && Gdx.input.isTouched)
        {
            val touch = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            hudCamera.unproject(touch)

            if (editorBounds.contains(touch.x, touch.y))
            {
                Main.game.screen = Main.editorScreen
            }
        }
    }

    private fun updateRestart() {
        if (fadein < 0.85f)
            fadein += 0.05f
        else fadein = 0.85f

        if (Gdx.input.isTouched) {
            val touch = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            hudCamera.unproject(touch)

            if (retry.bounds.contains(touch.x, touch.y)) {
                restart()
                state = SCREEN_STATE.NORMAL
                val systems = Main.engine.systems
                for (i in 0..systems.size() - 1) {
                    systems.get(i).setProcessing(true)
                }
            }
            return
        }

    }

    private fun drawRestart() {
        batch.projectionMatrix = hudCamera.combined
        batch.begin()

        batch.enableBlending()
        batch.setColor(batch.color.r, batch.color.g, batch.color.b, fadein)
        batch.draw(gameOver, 0f, 0f)
        batch.setColor(batch.color.r, batch.color.g, batch.color.b, 1f)

        fontBig.color = Color.SALMON.cpy()
        fontBig.color.a = fadein
        fontBig.draw(batch, "OUT OF ENERGY", 420f, 400f)

        val touch = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        hudCamera.unproject(touch)
        font.color = Color.WHITE
        retry.draw(batch, touch, font)

        batch.end()
    }

    fun reloadTerrain() {
        val terrainEntities = Main.engine.getEntitiesFor(Family.all(TerrainComponent::class.java).get())
        val i = 0
        while (i < terrainEntities.size()) {
            engine.removeEntity(terrainEntities.get(i))
        }
        generateLayeredTerrain()
    }

    fun restart() {
        guns.clear()
        timebomb = null
        menuKeyWasPressed = false
        Main.engine.removeAllEntities()
        engine = Main.engine
        player = EntityFactory.player(0f, 3f)
        base = EntityFactory.base(0f, 0f)
        loadShip(GameData.shipNumber)
        val gun = EntityFactory.gunDefault(player, Vector2(45f, 0f), 0f, true)
        guns.add(gun)
        EntityFactory.thruster(player, -50f, 0f, 0f, Constants.THRUSTER_CHARGE_DEFAULT)
        EntityFactory.camera(player, gameCamera, 0f, 3.3f)
        generateTerrain360()

        var followingSystem = engine.getSystem(FollowingSystem::class.java)
        followingSystem.setPlayer(player)
        followingSystem.setBase(base)
        engine.getSystem(ScoreSystem::class.java).base = base
        engine.getSystem(SapSystem::class.java).player = player

        fadein = 0.1f

        GameData.reset()
        Constants.reset()
        Main.levelupScreen.reset()
        Main.fitScreen.reset()
    }

    fun loadShip(num: Int) {
        var lc = Main.engine.createComponent(LineComponent::class.java)
        val file =
                when (num) {
                    1 -> Gdx.files.local(AssetPaths.SHIP1_LINES)
                    2 -> Gdx.files.local(AssetPaths.SHIP2_LINES)
                    3 -> Gdx.files.local(AssetPaths.SHIP3_LINES)
                    4 -> Gdx.files.local(AssetPaths.SHIP4_LINES)
                    5 -> Gdx.files.local(AssetPaths.SHIP5_LINES)
                    else -> Gdx.files.local(AssetPaths.SHIP1_LINES)
                }
        val lines = file.toVertices()
        lc.set(lines)
        player.add(lc)

        var tc = Mappers.transform.get(player)
        val body =
                if (num == 1) EntityFactory.createPlayerBodySmall(player, tc.x, tc.y)
                else EntityFactory.createPlayerBodyNormal(player, tc.x, tc.y)
        val bc = Main.engine.createComponent(BodyComponent::class.java)
        bc.set(body)
        player.add(bc)

        if (num == 3) {
            EntityFactory.thruster(player, -50f, -15f, 0f, Constants.THRUSTER_CHARGE_DEFAULT)
            EntityFactory.thruster(player, -50f, 15f, 0f, Constants.THRUSTER_CHARGE_DEFAULT)
        } else {
            EntityFactory.thruster(player, -50f, 0f, 0f, Constants.THRUSTER_CHARGE_DEFAULT)
        }

        if (num != 1) {
            val pmc = Mappers.playerMovement.get(player)
            Constants.TURN_MAGNITUDE_DEFAULT = 0.035f
            pmc.turnMagnitude = Constants.TURN_MAGNITUDE_DEFAULT
        }
    }
}
