package com.mygdx.ships.menus.main

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector3
import com.mygdx.ships.AssetPaths
import com.mygdx.ships.GameData
import com.mygdx.ships.Main
import com.mygdx.ships.menus.Button

class ControlsScreen(internal val optionsScreen: OptionsScreen) : ScreenAdapter() {
    internal var batch: SpriteBatch = Main.spriteBatch
    internal var font: BitmapFont = Main.assets.get<BitmapFont>(AssetPaths.FONT)
    internal var layout: GlyphLayout = GlyphLayout()
    internal var camera: OrthographicCamera
    var camWidth: Float = 0f
    var camHeight: Float = 0f

    internal val controlsText =
            """Move forward

Turn left

Turn right

Reverse

Stabilize

Shoot"""
    internal var forward: Button
    internal var left: Button
    internal var right: Button
    internal var reverse: Button
    internal var stabilize: Button
    internal var shoot: Button
    internal val reset: Button
    internal val back: Button
    internal var controlButtons = arrayListOf<Button>()
    var activatedButton: Button? = null
    internal var timer = 0f
    internal val inputProcessor = MyInputProcessor(this)

    init {
        camWidth = Gdx.graphics.width.toFloat()
        camHeight = Gdx.graphics.height.toFloat()
        camera = OrthographicCamera(camWidth, camHeight)
        camera.position.set(camWidth / 2, camHeight / 2, 0f)
        camera.update()

        val xOffset = 600f
        var yOffset = 635f
        val lineSpacing = 106f
        forward = Button("W", xOffset, yOffset, 0, layout, font)
        controlButtons.add(forward)
        yOffset -= lineSpacing
        left = Button("A", xOffset, yOffset, 0, layout, font)
        controlButtons.add(left)
        yOffset -= lineSpacing
        right = Button("D", xOffset, yOffset, 0, layout, font)
        controlButtons.add(right)
        yOffset -= lineSpacing
        reverse = Button("L SHIFT", xOffset, yOffset, 0, layout, font)
        controlButtons.add(reverse)
        yOffset -= lineSpacing
        stabilize = Button("S", xOffset, yOffset, 0, layout, font)
        controlButtons.add(stabilize)
        yOffset -= lineSpacing
        shoot = Button("ENTER", xOffset, yOffset, 0, layout, font)
        controlButtons.add(shoot)

        back = Button("Back", camWidth - 120f, 50f, layout, font)
        reset = Button("Reset", 850f, yOffset, 1, layout, font)
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

        font.color = Color.WHITE
        font.draw(batch, controlsText, 100f, 670f)

        if (activatedButton != null) {
            timer += 1 / 60f
            if (timer > 2f) timer = 0f
            var xOffset = -15f
            val numberOfDots = (timer * 3 / 2).toInt()
            for (i in 0..numberOfDots) {
                font.draw(batch, ".",
                        activatedButton!!.bounds.x + activatedButton!!.bounds.width / 2 + xOffset,
                        activatedButton!!.bounds.y + 30f)
                xOffset += 10f
            }
        }

        val touch = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        camera.unproject(touch)

        controlButtons.forEach { it.draw(batch, touch, font) }

        reset.draw(batch, touch, font)
        back.draw(batch, touch, font)

        batch.end()
    }

    fun update() {
        if (Gdx.input.isTouched()) {
            val touch = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            camera.unproject(touch)

            if (back.bounds.contains(touch.x, touch.y)) {
                optionsScreen.wasTouched = true
                Main.game.setScreen(optionsScreen)
                return
            }
        }

        if (Gdx.input.justTouched()) {
            val touch = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            camera.unproject(touch)

            if (reset.bounds.contains(touch.x, touch.y)) {
                GameData.Controls.MOVE_FORWARD = Input.Keys.W
                GameData.Controls.REVERSE = Input.Keys.SHIFT_LEFT
                GameData.Controls.TURN_LEFT = Input.Keys.A
                GameData.Controls.TURN_RIGHT = Input.Keys.D
                GameData.Controls.STABILIZE = Input.Keys.S
                GameData.Controls.SHOOT = Input.Keys.ENTER
                controlButtons.clear()
                forward = Button("W", forward.bounds.x + forward.bounds.width / 2,
                        forward.bounds.y, 0, layout, font)
                left = Button("A", left.bounds.x + left.bounds.width / 2,
                        left.bounds.y, 0, layout, font)
                right = Button("D", right.bounds.x + right.bounds.width / 2,
                        right.bounds.y, 0, layout, font)
                stabilize = Button("S", stabilize.bounds.x + stabilize.bounds.width / 2,
                        stabilize.bounds.y, 0, layout, font)
                reverse = Button("L SHIFT", reverse.bounds.x + reverse.bounds.width / 2,
                        reverse.bounds.y, 0, layout, font)
                shoot = Button("ENTER", shoot.bounds.x + shoot.bounds.width / 2,
                        shoot.bounds.y, 0, layout, font)
                controlButtons.add(forward)
                controlButtons.add(left)
                controlButtons.add(right)
                controlButtons.add(reverse)
                controlButtons.add(stabilize)
                controlButtons.add(shoot)
                activatedButton = null
                Gdx.input.inputProcessor = null
            }

            if (activatedButton == null) {
                if (forward.bounds.contains(touch.x, touch.y)) {
                    timer = 0f
                    activatedButton = forward
                    controlButtons.remove(forward)
                    Gdx.input.inputProcessor = inputProcessor
                }
                if (left.bounds.contains(touch.x, touch.y)) {
                    timer = 0f
                    activatedButton = left
                    controlButtons.remove(left)
                    Gdx.input.inputProcessor = inputProcessor
                }
                if (stabilize.bounds.contains(touch.x, touch.y)) {
                    timer = 0f
                    activatedButton = stabilize
                    controlButtons.remove(stabilize)
                    Gdx.input.inputProcessor = inputProcessor
                }
                if (reverse.bounds.contains(touch.x, touch.y)) {
                    timer = 0f
                    activatedButton = reverse
                    controlButtons.remove(reverse)
                    Gdx.input.inputProcessor = inputProcessor
                }
                if (right.bounds.contains(touch.x, touch.y)) {
                    timer = 0f
                    activatedButton = right
                    controlButtons.remove(right)
                    Gdx.input.inputProcessor = inputProcessor
                }
                if (shoot.bounds.contains(touch.x, touch.y)) {
                    timer = 0f
                    activatedButton = shoot
                    controlButtons.remove(shoot)
                    Gdx.input.inputProcessor = inputProcessor
                }
            }
        }
    }

    override fun hide() {
        if (activatedButton != null) {
            controlButtons.add(activatedButton!!)
            activatedButton = null
        }
        Gdx.input.inputProcessor = null
    }

    class MyInputProcessor(internal val controlsScreen: ControlsScreen) : InputAdapter() {

        override fun keyDown(keycode: Int): Boolean {
            if (controlsScreen.activatedButton == null)
                return false

            val newText: String = when (keycode) {
                7 -> "NUM_0"
                8 -> "NUM_1"
                9 -> "NUM_2"
                10 -> "NUM_3"
                11 -> "NUM_4"
                12 -> "NUM_5"
                13 -> "NUM_6"
                14 -> "NUM_7"
                15 -> "NUM_8"
                16 -> "NUM_9"
                29 -> "A"
                57 -> "L ALT"
                58 -> "R ALT"
                75 -> "APOSTROPHE"
                77 -> "AT"
                30 -> "B"
                4 -> "BACK"
                73 -> "BACKSLASH"
                31 -> "C"
                28 -> "CLEAR"
                55 -> "COMMA"
                32 -> "D"
                67 -> "DEL"
                67 -> "BACKSPACE"
                20 -> "DOWN"
                21 -> "LEFT"
                22 -> "RIGHT"
                19 -> "UP"
                23 -> "CENTER"
                20 -> "DOWN"
                21 -> "LEFT"
                22 -> "RIGHT"
                19 -> "UP"
                33 -> "E"
                66 -> "ENTER"
                70 -> "EQUALS"
                34 -> "F"
                35 -> "G"
                68 -> "GRAVE"
                36 -> "H"
                3 -> "HOME"
                37 -> "I"
                38 -> "J"
                39 -> "K"
                40 -> "L"
                71 -> "L BRACKET"
                41 -> "M"
                69 -> "MINUS"
                42 -> "N"
                78 -> "NUM"
                43 -> "O"
                44 -> "P"
                56 -> "PERIOD"
                81 -> "PLUS"
                18 -> "POUND"
                26 -> "POWER"
                45 -> "Q"
                46 -> "R"
                72 -> "R_BRACKET"
                47 -> "S"
                74 -> "SEMICOLON"
                59 -> "L SHIFT"
                60 -> "R SHIFT"
                76 -> "SLASH"
                62 -> "SPACE"
                17 -> "STAR"
                48 -> "T"
                61 -> "TAB"
                49 -> "U"
                50 -> "V"
                51 -> "W"
                52 -> "X"
                53 -> "Y"
                54 -> "Z"
                129 -> "L CTRL"
                130 -> "R CTRL"
                131 -> "ESC"
                132 -> "END"
                133 -> "INSERT"
                92 -> "PAGE_UP"
                93 -> "PAGE_DOWN"
                255 -> "CIRCLE"
                96 -> "BUTTON_A"
                97 -> "BUTTON_B"
                98 -> "BUTTON_C"
                99 -> "BUTTON_X"
                100 -> "BUTTON_Y"
                101 -> "BUTTON_Z"
                102 -> "L1"
                103 -> "R1"
                104 -> "L2"
                105 -> "R2"
                106 -> "L THUB"
                107 -> "R THUMB"
                108 -> "START"
                109 -> "SELECT"

                144 -> "NUMPAD_0"
                145 -> "NUMPAD_1"
                146 -> "NUMPAD_2"
                147 -> "NUMPAD_3"
                148 -> "NUMPAD_4"
                149 -> "NUMPAD_5"
                150 -> "NUMPAD_6"
                151 -> "NUMPAD_7"
                152 -> "NUMPAD_8"
                153 -> "NUMPAD_9"
                else -> " "
            }

            if (newText != " ") {

                val newButton = Button(newText,
                        controlsScreen.activatedButton!!.bounds.x +
                                controlsScreen.activatedButton!!.bounds.width / 2,
                        controlsScreen.activatedButton!!.bounds.y,
                        0, controlsScreen.layout, controlsScreen.font)
                controlsScreen.controlButtons.add(newButton)

                when (controlsScreen.activatedButton) {
                    controlsScreen.shoot -> {
                        GameData.Controls.SHOOT = keycode
                        controlsScreen.shoot = newButton
                    }
                    controlsScreen.forward -> {
                        GameData.Controls.MOVE_FORWARD = keycode
                        controlsScreen.forward = newButton
                    }
                    controlsScreen.left -> {
                        GameData.Controls.TURN_LEFT = keycode
                        controlsScreen.left = newButton
                    }
                    controlsScreen.right -> {
                        GameData.Controls.TURN_RIGHT = keycode
                        controlsScreen.right = newButton
                    }
                    controlsScreen.stabilize -> {
                        GameData.Controls.STABILIZE = keycode
                        controlsScreen.stabilize = newButton
                    }
                    controlsScreen.reverse -> {
                        GameData.Controls.REVERSE = keycode
                        controlsScreen.reverse = newButton
                    }
                }

                if (GameData.Controls.MOVE_FORWARD == Input.Keys.E
                        && GameData.Controls.TURN_LEFT == Input.Keys.D
                        && GameData.Controls.TURN_RIGHT == Input.Keys.I
                        && GameData.Controls.REVERSE == Input.Keys.T
                        && GameData.Controls.STABILIZE == Input.Keys.O
                        && GameData.Controls.SHOOT == Input.Keys.R)
                    GameData.editor = true
            }

            controlsScreen.activatedButton = null
            Gdx.input.inputProcessor = null
            return false
        }
    }
}
