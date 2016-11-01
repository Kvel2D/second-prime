package com.mygdx.ships

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.mygdx.ships.entity.EntityFactory
import com.mygdx.ships.entity.Mappers
import com.mygdx.ships.entity.components.BodyComponent
import com.mygdx.ships.entity.components.LineComponent

class ShipScreen(internal val gameScreen: GameScreen) : ScreenAdapter() {
    internal val batch: SpriteBatch
    internal val font: BitmapFont
    internal val fontBig: BitmapFont
    internal val layout: GlyphLayout

    internal val ship1: Texture
    internal val ship2: Texture
    internal val ship3: Texture
    internal val ship4: Texture
    internal val ship5: Texture
    internal val ship1On: Texture
    internal val ship2On: Texture
    internal val ship3On: Texture
    internal val ship4On: Texture
    internal val ship5On: Texture
    internal val rectangle1: Rectangle
    internal val rectangle2: Rectangle
    internal val rectangle3: Rectangle
    internal val rectangle4: Rectangle
    internal val rectangle5: Rectangle

    internal var camera: OrthographicCamera
    internal var camWidth: Float
    internal var camHeight: Float

    init {
        camWidth = Gdx.graphics.width.toFloat()
        camHeight = Gdx.graphics.height.toFloat()
        camera = OrthographicCamera(camWidth, camHeight)
        camera.position.set(camWidth / 2, camHeight / 2, 0f)

        batch = Main.spriteBatch
        font = Main.assets.get<BitmapFont>(AssetPaths.FONT)
        fontBig = Main.assets.get<BitmapFont>(AssetPaths.FONT_BIG)
        layout = GlyphLayout()

        ship1 = Main.assets.get<Texture>(AssetPaths.SHIP1)
        ship2 = Main.assets.get<Texture>(AssetPaths.SHIP2)
        ship3 = Main.assets.get<Texture>(AssetPaths.SHIP3)
        ship4 = Main.assets.get<Texture>(AssetPaths.SHIP4)
        ship5 = Main.assets.get<Texture>(AssetPaths.SHIP5)
        ship1On = Main.assets.get<Texture>(AssetPaths.SHIP1_ON)
        ship2On = Main.assets.get<Texture>(AssetPaths.SHIP2_ON)
        ship3On = Main.assets.get<Texture>(AssetPaths.SHIP3_ON)
        ship4On = Main.assets.get<Texture>(AssetPaths.SHIP4_ON)
        ship5On = Main.assets.get<Texture>(AssetPaths.SHIP5_ON)
        rectangle1 = Rectangle(camWidth / 6 - 50, 300f, ship1.width.toFloat(), ship1.height.toFloat())
        rectangle2 = Rectangle(camWidth * 2 / 6 - 50, 300f, ship2.width.toFloat(), ship2.height.toFloat())
        rectangle3 = Rectangle(camWidth * 3 / 6 - 50, 300f, ship3.width.toFloat(), ship3.height.toFloat())
        rectangle4 = Rectangle(camWidth * 4 / 6 - 55, 300f, ship4.width.toFloat(), ship4.height.toFloat())
        rectangle5 = Rectangle(camWidth * 5 / 6 - 55, 300f, ship5.width.toFloat(), ship5.height.toFloat())
    }

    override fun render(deltaTime: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        draw()
        update()
    }

    fun draw() {
        camera.update()
        batch.projectionMatrix = camera.combined
        batch.begin()

        batch.draw(ship1, camWidth / 6 - 50, 300f)
        batch.draw(ship2, camWidth * 2 / 6 - 50, 300f)
        batch.draw(ship3, camWidth * 3 / 6 - 50, 300f)
        batch.draw(ship4, camWidth * 4 / 6 - 55, 300f)
        batch.draw(ship5, camWidth * 5 / 6 - 55, 300f)

        val touch = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        camera.unproject(touch)

        if (rectangle1.contains(touch.x, touch.y))
            batch.draw(ship1On, camWidth / 6 - 50, 300f)
        else if (rectangle2.contains(touch.x, touch.y))
            batch.draw(ship2On, camWidth * 2 / 6 - 50, 300f)
        else if (rectangle3.contains(touch.x, touch.y))
            batch.draw(ship3On, camWidth * 3 / 6 - 50, 300f)
        else if (rectangle4.contains(touch.x, touch.y))
            batch.draw(ship4On, camWidth * 4 / 6 - 55, 300f)
        else if (rectangle5.contains(touch.x, touch.y))
            batch.draw(ship5On, camWidth * 5 / 6 - 55, 300f)

        font.color = Color.WHITE
//        font.data.setScale(1.5f)
        layout.setText(fontBig, "SELECT SHIP")
        fontBig.draw(batch, "SELECT SHIP", camWidth / 2 - layout.width / 2, 650f)
//        font.data.setScale(1f)

        layout.setText(font, "A-02")
        font.draw(batch, "A-00", camWidth / 6 - layout.width / 2, 450f)
        font.draw(batch, "A-02", camWidth * 2 / 6 - layout.width / 2, 450f)
        font.draw(batch, "A-03", camWidth * 3 / 6 - layout.width / 2, 450f)
        font.draw(batch, "B-01", camWidth * 4 / 6 - layout.width / 2, 450f)
        font.draw(batch, "X-10", camWidth * 5 / 6 - layout.width / 2, 450f)

        batch.end()
    }

    fun update() {
        if (Gdx.input.isTouched()) {
            val touch = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            camera.unproject(touch)

            if (rectangle1.contains(touch.x, touch.y)) {
                setShipType(1)
                Main.game.screen = gameScreen
            } else if (rectangle2.contains(touch.x, touch.y)) {
                setShipType(2)
                Main.game.screen = gameScreen
            } else if (rectangle3.contains(touch.x, touch.y)) {
                setShipType(3)
                Main.game.screen = gameScreen
            } else if (rectangle4.contains(touch.x, touch.y)) {
                setShipType(4)
                Main.game.screen = gameScreen
            } else if (rectangle5.contains(touch.x, touch.y)) {
                setShipType(5)
                Main.game.screen = gameScreen
            }
        }
    }

    fun setShipType(num: Int) {
        GameData.shipNumber = num

        gameScreen.loadShip(num)
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

        lines.scale(3f)
        lines.translate(camWidth * 3 / 2, 400f, 0f)
        Main.fitScreen.shipVertices = lines
    }
}
