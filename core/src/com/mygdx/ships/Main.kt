package com.mygdx.ships

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Application
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Logger
import com.mygdx.ships.menus.main.LevelupScreen
import com.mygdx.ships.menus.main.*

class Main : ApplicationAdapter() {

    override fun create() {
        logger.level = Logger.DEBUG // set 'Logger.NONE' to turn off logging
        Gdx.app.logLevel = Application.LOG_DEBUG
        spriteBatch = SpriteBatch()
        Texture.setAssetManager(assets)
        game.create()
    }

    override fun render() {
        game.render()
    }

    override fun dispose() {
        assets.dispose()
        world.dispose()
        spriteBatch.dispose()
    }

    companion object {
        val game: Game = object : Game() {
            override fun create() {
                this.setScreen(LoadScreen())
            }
        }
        val logger = Logger("")
        val assets = AssetManager()
        val engine = PooledEngine(100, 10000, 100, 10000)
        val world = World(Vector2(0f, 0f), false)
        lateinit var spriteBatch: SpriteBatch
        lateinit var shipScreen: ShipScreen
        lateinit var gameScreen: GameScreen
        lateinit var menuScreen: MenuScreen
        lateinit var controlsScreen: ControlsScreen
        lateinit var optionsScreen: OptionsScreen
        lateinit var fitScreen: FitScreen
        lateinit var editorScreen: EditorScreen
        lateinit var levelupScreen: LevelupScreen
    }
}
