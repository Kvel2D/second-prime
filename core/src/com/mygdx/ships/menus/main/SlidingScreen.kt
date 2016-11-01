package com.mygdx.ships.menus.main

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.mygdx.ships.Main

open class SlidingScreen(internal var menuScreen: MenuScreen) : ScreenAdapter() {
    internal enum class SCREEN_STATE {
        START,
        NORMAL,
        END
    }

    internal var state: SCREEN_STATE
    internal var camera: OrthographicCamera
    var camWidth: Float = 0f
    var camHeight: Float = 0f
    internal val cameraStartPosition = 640f
    internal val cameraEndPosition = 1920f

    init {
        camera = menuScreen.camera
        camWidth = Gdx.graphics.width.toFloat()
        camHeight = Gdx.graphics.height.toFloat()

        this.state = SCREEN_STATE.START
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

    open fun draw() {
    }

    open fun update() {
    }

    protected fun transitionForward() {
        val transitionProgress = (camera.position.x - cameraStartPosition) / (cameraEndPosition - cameraStartPosition)
        if (transitionProgress < 0.5f) menuScreen.setCoverAlpha(transitionProgress + 0.5f)

        if (cameraEndPosition - camera.position.x > 0.5f) {
            var lerp = 0.2f
            if (cameraEndPosition - camera.position.x < 5f) lerp *= 2f
            camera.position.x += (cameraEndPosition - camera.position.x) * lerp
        } else {
            camera.position.x = cameraEndPosition
            this.state = SCREEN_STATE.NORMAL
        }
    }

    protected fun transitionBack() {
        val transitionProgress = (camera.position.x - cameraStartPosition) / (cameraEndPosition - cameraStartPosition)
        if (transitionProgress < 0.5f) menuScreen.setCoverAlpha(transitionProgress + 0.5f)

        if (camera.position.x - cameraStartPosition > 0.5f) {
            var lerp = 0.2f
            if (camera.position.x - cameraStartPosition < 5f) lerp *= 2f
            camera.position.x -= (camera.position.x - cameraStartPosition) * lerp
        } else {
            camera.position.x = cameraStartPosition
            menuScreen.setCoverAlpha(0.5f)
            this.state = SCREEN_STATE.START
            Main.game.screen = menuScreen
        }
    }
}
