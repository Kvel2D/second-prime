package com.mygdx.ships.menus.main

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.mygdx.ships.*
import com.mygdx.ships.menus.Button

class MenuScreen(internal var gameScreen: GameScreen) : ScreenAdapter() {
    internal enum class MENU_STATE {
        START,
        END,
        NORMAL
    }

    internal var state: MENU_STATE

    var camera: OrthographicCamera
    internal var camWidth: Float = 0.toFloat()
    internal var camHeight: Float = 0.toFloat()
    internal var batch: SpriteBatch
    internal var font: BitmapFont
    internal var layout: GlyphLayout
    internal var cover: Texture
    internal var previousMenuButton = false

    internal var fit: Button
    internal var upgrade: Button
    internal var options: Button
    internal var resume: Button
    internal var buttons = arrayListOf<Button>()

    internal val slideDistance = 640f
    internal var slideProgress = slideDistance
    internal var coverAlpha = 0f

    init {
        batch = Main.spriteBatch
        camWidth = Gdx.graphics.width.toFloat()
        camHeight = Gdx.graphics.height.toFloat()
        camera = OrthographicCamera(camWidth, camHeight)
        camera.position.set(camWidth / 2, camHeight / 2, 0f)
        cover = Main.assets.get<Texture>(AssetPaths.MENU_BACKGROUND)
        font = Main.assets.get<BitmapFont>(AssetPaths.FONT)
        layout = GlyphLayout()

        val xOffset = 1000f
        var yOffset = 430f
        val lineSpacing = 60f
        fit = Button("FIT SHIP", xOffset, yOffset, 1, layout, font)
        buttons.add(fit)
        yOffset -= lineSpacing
        upgrade = Button("UPGRADE", xOffset, yOffset, 1, layout, font)
        buttons.add(upgrade)
        yOffset -= lineSpacing
        options = Button("OPTIONS", xOffset, yOffset, 1, layout, font)
        buttons.add(options)
        yOffset -= lineSpacing
        resume = Button("RESUME", xOffset, yOffset, 1, layout, font)
        buttons.add(resume)

        this.state = MENU_STATE.START
    }

    override fun render(deltaTime: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        when (state) {
            MENU_STATE.START -> {
                draw(deltaTime)
                updateStart()
            }
            MENU_STATE.END -> {
                draw(deltaTime)
                updateEnd()
            }
            MENU_STATE.NORMAL -> {
                draw(deltaTime)
                update()
            }
        }
    }

    private fun updateStart() {
        if (slideProgress > 2f) {
            var lerp = 0.2f
            if (slideProgress < 12f) lerp *= 2f
            slideProgress -= slideProgress * lerp
        } else {
            slideProgress = 0f
            this.state = MENU_STATE.NORMAL
        }
        coverAlpha = 0.5f * ((slideDistance - slideProgress) / slideDistance)
    }

    private fun updateEnd() {
        if (slideDistance - slideProgress > 10.5f) {
            var lerp = 0.2f
            if (slideDistance - slideProgress < 5f) lerp *= 2f
            slideProgress += (slideDistance - slideProgress) * lerp
        } else {
            slideProgress = slideDistance
            this.state = MENU_STATE.START
            Main.game.setScreen(Main.gameScreen)
        }
        coverAlpha = 0.5f * ((slideDistance - slideProgress) / slideDistance)
    }

    fun draw(deltaTime: Float) {
        Main.engine.update(deltaTime)
        gameScreen.hud.render()

        camera.update()
        batch.projectionMatrix = camera.combined
        batch.enableBlending()
        batch.begin()

        batch.color = Color(batch.color.r, batch.color.g, batch.color.b, coverAlpha)
        batch.draw(cover, 0f, 0f)
        batch.color = Color(Color.WHITE)

        val touch = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        camera.unproject(touch)

        buttons.forEach {
            it.draw(batch, slideProgress, 0f, touch, font)
        }

        batch.end()
    }

    private fun update() {
        if (Gdx.input.isKeyPressed(GameData.Controls.MENU)) {
            if (previousMenuButton) switchToGame()
            previousMenuButton = false
        } else
            previousMenuButton = true

        if (Gdx.input.isTouched) {
            val touch = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            camera.unproject(touch)

            if (fit.bounds.contains(touch.x, touch.y)) {
                Main.game.screen = Main.fitScreen
            }
            if (upgrade.bounds.contains(touch.x, touch.y)) Main.game.screen = Main.levelupScreen
            if (options.bounds.contains(touch.x, touch.y)) Main.game.screen = Main.optionsScreen
            if (resume.bounds.contains(touch.x, touch.y)) switchToGame()
        }
    }

    private fun switchToGame() {
        slideProgress = 0f
        val systems = Main.engine.systems
        for (i in 0..systems.size() - 1) {
            systems.get(i).setProcessing(true)
        }

        this.state = MENU_STATE.END
    }

    fun setCoverAlpha(newAlpha: Float) {
        coverAlpha = newAlpha
    }
}
