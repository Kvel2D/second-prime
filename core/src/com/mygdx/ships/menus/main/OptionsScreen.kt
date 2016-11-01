package com.mygdx.ships.menus.main

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.mygdx.ships.AssetPaths
import com.mygdx.ships.Constants
import com.mygdx.ships.Main
import com.mygdx.ships.menus.Button

class OptionsScreen(menuScreen: MenuScreen) : SlidingScreen(menuScreen) {
    internal var batch: SpriteBatch = Main.spriteBatch
    internal var font: BitmapFont = Main.assets.get<BitmapFont>(AssetPaths.FONT)
    internal var layout: GlyphLayout = GlyphLayout()
    val shapeRenderer = ShapeRenderer()
    internal var controls = Button("Controls", camWidth + 100f, 350f, layout, font)
    internal val back = Button("Back", camWidth * 2 - 120f, 50f, layout, font)
    internal val prefs = Gdx.app.getPreferences("Settings")
    internal val soundBounds: Rectangle = Rectangle(camWidth + 400f, 465f, 300f, 30f)
    var sound = prefs.getFloat("volume")
    var wasTouched = false

    override fun draw() {
        camera.update()
        batch.projectionMatrix = camera.combined
        batch.begin()

        font.color = Color.WHITE
        font.draw(batch, "Sound", camWidth + 100f, 500f)
        font.draw(batch, "${(sound *100).toInt()}%", camWidth + 300f, 500f)

        val touch = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        camera.unproject(touch)

        back.draw(batch, touch, font)
        controls.draw(batch, touch, font)

        batch.end()

        shapeRenderer.color = Color.LIGHT_GRAY
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.rect(soundBounds.x, soundBounds.y, soundBounds.width, soundBounds.height)
        shapeRenderer.rect(soundBounds.x + sound*300f, soundBounds.y, 10f, soundBounds.height)
        shapeRenderer.end()
    }

    override fun update() {
        if (Gdx.input.isTouched) {
            val touch = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            camera.unproject(touch)

            if (!wasTouched && back.bounds.contains(touch.x, touch.y)) {
                this.state = SCREEN_STATE.END
                return
            }

            if (controls.bounds.contains(touch.x, touch.y)) {
                Main.game.setScreen(Main.controlsScreen)
                return
            }

            if (soundBounds.contains(touch.x, touch.y))
                sound = (touch.x - soundBounds.x) / 300f
        }

        if (!Gdx.input.isTouched)
            wasTouched = false
    }

    override fun hide() {
        prefs.putFloat("volume", sound)
        prefs.flush()
        Constants.VOLUME = sound
    }
}