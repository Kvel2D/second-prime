package com.mygdx.ships

object AssetPaths {
    const val SHIP1 = "ship1.png"
    const val SHIP2 = "ship2.png"
    const val SHIP3 = "ship3.png"
    const val SHIP4 = "ship4.png"
    const val SHIP5 = "ship5.png"
    const val SHIP1_ON = "ship1_select.png"
    const val SHIP2_ON = "ship2_select.png"
    const val SHIP3_ON = "ship3_select.png"
    const val SHIP4_ON = "ship4_select.png"
    const val SHIP5_ON = "ship5_select.png"
    const val BASE = "base.png"
    const val BASE_BACKGROUND = "base_background.png"
    const val MENU_BACKGROUND = "upgrade_background.png"
    const val CONSTRUCTION_BACKGROUND = "construction_background.png"
    const val MOUSE_LEFT = "mouse_left.png"
    const val MOUSE_RIGHT = "mouse_right.png"
    val textures: List<String> = listOf(SHIP1, SHIP2, SHIP3, SHIP4, SHIP5,
            SHIP1_ON, SHIP2_ON, SHIP3_ON, SHIP4_ON, SHIP5_ON,
            BASE, BASE_BACKGROUND, CONSTRUCTION_BACKGROUND, MENU_BACKGROUND,
            MOUSE_LEFT, MOUSE_RIGHT)

    const val FONT = "fonts/font.fnt"
    const val FONT_BIG = "fonts/font_big.fnt"
    const val FONT_SMALL = "fonts/font_small.fnt"

    const val TERRAIN_PATHS = "terrain_paths.txt"
    const val EDITOR_TEMP = "lines/temp.txt"
    const val SHIP1_LINES = "lines/ship1.txt"
    const val SHIP2_LINES = "lines/ship2.txt"
    const val SHIP3_LINES = "lines/ship3.txt"
    const val SHIP4_LINES = "lines/ship4.txt"
    const val SHIP5_LINES = "lines/ship5.txt"
    const val GUN_LINES = "lines/gun.txt"
    const val BASE_LINES = "lines/base.txt"
    const val BASE_BODY = "lines/baseBody.txt"
    const val TERRAIN_DIR = "terrain"

    const val SHOOT = "sounds/shoot.ogg"
    const val PULSE = "sounds/pulse.ogg"
    const val COIN = "sounds/coin.ogg"
    const val EXPLOSION = "sounds/explosion.ogg"
    val sounds = listOf(SHOOT, PULSE, COIN, EXPLOSION)
}