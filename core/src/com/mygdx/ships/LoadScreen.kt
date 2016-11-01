package com.mygdx.ships

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.mygdx.ships.menus.main.*

class LoadScreen : ScreenAdapter() {
    internal var screenHeight: Float = 0f
    internal var minimumShowTime = 60f
    lateinit var batch: SpriteBatch
    lateinit var font: BitmapFont
    val startGameText = "PRESS ENTER TO START"
    val controlsText =
            """W - move forward
A - turn left
D - turn right
L SHIFT - reverse
S - stabilize/stop
ENTER - shoot"""

    override fun show() {
        batch = Main.spriteBatch

        AssetPaths.textures.forEach { Main.assets.load(it, Texture::class.java) }
        AssetPaths.sounds.forEach { Main.assets.load(it, Sound::class.java) }
        Main.assets.load(AssetPaths.FONT, BitmapFont::class.java)
        Main.assets.load(AssetPaths.FONT_BIG, BitmapFont::class.java)
        Main.assets.load(AssetPaths.FONT_SMALL, BitmapFont::class.java)
        Main.assets.finishLoading()

        this.font = Main.assets.get<BitmapFont>(AssetPaths.FONT)
        this.screenHeight = Gdx.graphics.height.toFloat()

        val prefs = Gdx.app.getPreferences("Settings")
        if (!prefs.contains("volume")) {
            prefs.putFloat("volume", 0.5f)
            prefs.flush()
        }
        Constants.VOLUME = prefs.getFloat("volume")

        Main.gameScreen = GameScreen()
        Main.shipScreen = ShipScreen(Main.gameScreen)
        Main.menuScreen = MenuScreen(Main.gameScreen)
        Main.optionsScreen = OptionsScreen(Main.menuScreen)
        Main.controlsScreen = ControlsScreen(Main.optionsScreen)
        Main.fitScreen = FitScreen(Main.menuScreen, Main.gameScreen)
        Main.levelupScreen = LevelupScreen(Main.menuScreen)
        Main.editorScreen = EditorScreen(Main.gameScreen)
    }

    override fun render(deltaTime: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        batch.begin()

        font.draw(batch, controlsText, 150f, screenHeight - 150f)
        font.draw(batch, startGameText, 400f, 100f)

        batch.end()

        minimumShowTime -= deltaTime
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER))
            minimumShowTime = 0f
        if (minimumShowTime <= 0 && Main.assets.update())
            Main.game.screen = Main.shipScreen
    }
}