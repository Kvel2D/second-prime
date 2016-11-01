package com.mygdx.ships

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.mygdx.ships.entity.Mappers
import java.util.*

class Hud(internal val camera: OrthographicCamera) {
    internal val batch: SpriteBatch = Main.spriteBatch
    internal val shapeRenderer: ShapeRenderer = ShapeRenderer()
    internal val font: BitmapFont = Main.assets.get(AssetPaths.FONT_SMALL)

    val lines = Gdx.files.internal(AssetPaths.GUN_LINES).toVertices()

    fun render() {
        batch.projectionMatrix = camera.combined
        batch.begin()

        if (GameData.upgradePoints != 0)
        {
            if (Main.game.screen == Main.gameScreen) {
                val r = Math.random().toFloat() * 0.5f + 0.5f
                val g = Math.random().toFloat() * 0.5f + 0.5f
                val b = Math.random().toFloat() * 0.5f + 0.5f
                font.color = Color(r, g, b, 0.9f)
            }
            font.draw(batch, "UPGRADE READY", 1115f, 610f)
            font.draw(batch, "PRESS TAB", 1140f, 580f)
        }

        if (GameData.sap) {
            font.color = Color.RED
            font.draw(batch, "ENERGY DRAIN", 970f, 700f)
        }


        if (GameData.editor) {
            font.color = Color.WHITE
            font.draw(batch, "EDITOR", Main.gameScreen.editorBounds.x, Main.gameScreen.editorBounds.y + 20f)
        }

        batch.end()

        shapeRenderer.color = Color.WHITE
        val percentEnergy = Math.round(GameData.energy / GameData.energyMax * 100)
        if (percentEnergy < 40) shapeRenderer.color = Color.SALMON
        if (percentEnergy < 25) shapeRenderer.color = Color.FIREBRICK
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.setAutoShapeType(true)
        var energyBars = (GameData.energy/GameData.energyMax * 20).toInt()
        var xOffset = 1270f
        var yOffset = 680f
        for (i in 0..(energyBars - 1))
        {
            shapeRenderer.box(xOffset, yOffset, 0f, 5f, 30f, 0f)
            xOffset -= 8f
        }
        shapeRenderer.box(1115f, 678f, 0f, 163f, 34f, 0f)

        shapeRenderer.color = Color.LIGHT_GRAY
        shapeRenderer.circle(80f, 640f, 70f)
        shapeRenderer.box(80f, 640f, 1f, 1f, 1f, 1f)
        val tc = Mappers.transform.get(Main.gameScreen.player)
        val angle = MathUtils.atan2(tc.y , tc.x)
        val sin = MathUtils.sin(angle)
        val cos = MathUtils.cos(angle)
        var radius = 70f
        if (Math.abs(tc.x) < 960f && Math.abs(tc.y) < 540f) {
            radius = Math.min(70f,
                    (tc.x * tc.x + tc.y * tc.y) / ((960f * cos) * (960f * cos) + (540f * sin) * (540f * sin) ) * 70f)
        }
        val x = 80f - radius * cos
        val y = 640f - radius * sin
        shapeRenderer.color = Color.WHITE
        shapeRenderer.circle(x, y, 7f)

        shapeRenderer.color = Color.LIME
        shapeRenderer.box(1115f, 628f, 0f, 163f, 34f, 0f)
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled)
        var expBars = (GameData.money*53/GameData.moneyMax).toInt()
        if (GameData.money > GameData.moneyMax) expBars = 53
        xOffset = 1273f
        yOffset = 630f
        for (i in 0..(expBars - 1))
        {
            shapeRenderer.box(xOffset, yOffset, 0f, 3f, 30f, 0f)
            xOffset -= 3f
        }

        shapeRenderer.end()
    }
}
