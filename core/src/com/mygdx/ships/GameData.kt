package com.mygdx.ships

import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.mygdx.ships.entity.Mappers
import com.mygdx.ships.entity.components.FadeComponent
import com.mygdx.ships.entity.systems.RenderSystem
import com.mygdx.ships.entity.systems.ShapeRenderSystem
import com.mygdx.ships.menus.main.LevelupScreen
import java.util.*

object GameData {

    object Controls {
        var MOVE_FORWARD = Input.Keys.W
        var REVERSE = Input.Keys.SHIFT_LEFT
        var TURN_LEFT = Input.Keys.A
        var TURN_RIGHT = Input.Keys.D
        var STABILIZE = Input.Keys.S
        var SHOOT = Input.Keys.ENTER
        var MENU = Input.Keys.TAB

        var ZOOM_OUT = Input.Keys.PAGE_UP
        var ZOOM_IN = Input.Keys.PAGE_DOWN
        var UP = Input.Keys.UP
        var DOWN = Input.Keys.DOWN
        var LEFT = Input.Keys.LEFT
        var RIGHT = Input.Keys.RIGHT
        var UNDO = Input.Keys.Z
        var SELECT_ENTITY = Input.Keys.Q
        var DELETE_ENTITY = Input.Keys.TAB
    }

    val resourceOrder = listOf(
            "gray",
            "blue",
            "green",
            "gold",
            "purple",
            "red",
            "unobtanium",
            "brown",
            "sky",
            "teal",
            "neongreen",
            "olive",
            "orange",
            "tan",
            "magenta",
            "slate")

    val resourceColors = mapOf(
            "gray" to Color.GRAY,
            "blue" to Color.ROYAL,
            "green" to Color.LIME,
            "gold" to Color.GOLD,
            "purple" to Color.PURPLE,
            "red" to Color.FIREBRICK,
            "unobtanium" to Color.WHITE,
            "brown" to Color.BROWN,
            "sky" to Color.SKY,
            "teal" to Color.TEAL,
            "neongreen" to Color.CHARTREUSE,
            "olive" to Color.OLIVE,
            "orange" to Color.ORANGE,
            "tan" to Color.TAN,
            "magenta" to Color.MAGENTA,
            "slate" to Color.SLATE
    )

    val resourceLevels = mapOf(
            "gray" to 1,
            "blue" to 1,
            "green" to 1,
            "gold" to 1,
            "purple" to 1,
            "red" to 1,
            "unobtanium" to 30,
            "brown" to 1,
            "sky" to 1,
            "teal" to 1,
            "neongreen" to 1,
            "olive" to 1,
            "orange" to 1,
            "tan" to 1,
            "magenta" to 1,
            "slate" to 1)

    var editor = false
    var energyMax = 15f
    var energy = energyMax
    var money = 0
    var moneyMax = 150
    var upgradePoints = 0
    var shipNumber = 1
    var stress = false
    var stressUnlocked = false // flag for firing fast at low fuel upgrade
    var distanceToBase = 0f
    var sap = false

    fun addMoney() {
        if (energy < energyMax) energy += 0.1f
        else money += 1
    }

    fun update(deltaTime: Float) {
        if (money >= moneyMax)
        {
            money = 0
            moneyMax = (moneyMax * 1.4f).toInt();
            energyMax += 0.5f
            upgradePoints++
            if (upgradePoints == 1)
                Main.levelupScreen.shuffleUpgrades()
        }

        val tc = Mappers.transform.get(Main.gameScreen.player)
        distanceToBase = (tc.y * tc.y + tc.x * tc.x)
        if (energy > 0 && distanceToBase > 160000f)
            energy -= Constants.ENERGY_DRAIN * deltaTime / 60f

        if (sap)
            energy -= 0.01f

        if (energy < 0)
        {
            val systems = Main.engine.systems
            for (i in 0..systems.size() - 1) {
                systems.get(i).setProcessing(false)
            }
            Main.engine.getSystem(RenderSystem::class.java).setProcessing(true)
            Main.engine.getSystem(ShapeRenderSystem::class.java).setProcessing(true)
            Main.gameScreen.state = GameScreen.SCREEN_STATE.RESTART
        }

        stress = (energy/ energyMax < 0.4f) && stressUnlocked
    }

    fun reset() {
        energy = energyMax
        money = 0
        upgradePoints = 0
        stress = false
        stressUnlocked = false
    }
}
