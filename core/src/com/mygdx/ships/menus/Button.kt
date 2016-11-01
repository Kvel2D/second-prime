package com.mygdx.ships.menus

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.mygdx.ships.Constants

open class Button {
    val buttonText: String
    val bounds: Rectangle
    val align: Int

    constructor(buttonText: String, x: Float, y: Float, layout: GlyphLayout, font: BitmapFont) {
        this.buttonText = buttonText
        layout.setText(font, buttonText)
        bounds = Rectangle(x, y, layout.width, layout.height)
        align = 0
    }

    /*
     * 0 = te|xt
     * -1 = text|
     * 1 = |text
     */
    constructor(buttonText: String, x: Float, y: Float, align: Int, layout: GlyphLayout, font: BitmapFont) {
        this.buttonText = buttonText
        layout.setText(font, buttonText)
        when (align) {
            0 -> bounds = Rectangle(x - layout.width/2, y, layout.width, layout.height);
            1 -> bounds = Rectangle(x, y, layout.width, layout.height);
            -1 -> bounds = Rectangle(x - layout.width, y, layout.width, layout.height);
            else -> bounds = Rectangle(x, y, layout.width, layout.height);
        }
        this.align = align
    }

    open fun draw(batch: SpriteBatch, touch: Vector3, font: BitmapFont) {
        if (bounds.contains(touch.x, touch.y))
            font.color = Constants.BUTTON_ACTIVE_COLOR
        else
            font.color = Constants.BUTTON_INACTIVE_COLOR
        font.draw(batch, buttonText, bounds.x, bounds.y + bounds.height)
    }

    open fun draw(batch: SpriteBatch, xOffset: Float, yOffset: Float, touch: Vector3, font: BitmapFont) {
        if (bounds.contains(touch.x, touch.y))
            font.color = Constants.BUTTON_ACTIVE_COLOR
        else
            font.color = Constants.BUTTON_INACTIVE_COLOR
        font.draw(batch, buttonText, bounds.x + xOffset, bounds.y + bounds.height + yOffset)
    }

    fun setPosition(x: Float, y: Float) {
        when (align) {
            0 -> bounds.setPosition(x - bounds.width/2, y);
            1 -> bounds.setPosition(x, y);
            -1 -> bounds.setPosition(x - bounds.width, y);
            else -> bounds.setPosition(x, y);
        }
    }
}
