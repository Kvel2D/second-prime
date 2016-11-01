package com.mygdx.ships

import com.badlogic.gdx.graphics.Color

object Constants {
    const val VIEWPORT_WIDTH = 16
    const val VIEWPORT_HEIGHT = 9
    const val METER_TO_PIXEL = 1 / 80f
    const val DEGTORAD = 0.0175f
    const val TIME_STEP = 1 / 60f
    const val VELOCITY_ITERATIONS = 6
    const val POSITION_ITERATIONS = 2
    const val MINIMUM_POLYGON_AREA = 300
    const val MIN_EXPLOSION_FOR_ORE = 10f
    const val IGNORE_PLAYER: Short = -1 // collision group index for ignoring collisions with player entities
    const val PLAYER_RADIUS = 50f
    const val INDESTRUCTIBLE_LEVEL = 10f
    const val ENERGY_DRAIN = 30f // energy drained per minute(frametime)
    val DEFAULT_SHAPE_COLOR = Color(0.5f, 0.5f, 0.5f, 1f)
    val BUTTON_INACTIVE_COLOR = Color.WHITE
    val BUTTON_ACTIVE_COLOR = Color.GOLD
    var VOLUME = 0.5f

    // DEFAULTS
    const val FORWARD_MAGNITUDE_DEFAULT = 40f
    var TURN_MAGNITUDE_DEFAULT = 0.02f
    const val ATTRACTION_DEFAULT = 300f
    const val FIRE_FORCE_DEFAULT = 1f
    const val WEAPON_CHARGE_DEFAULT = 1f
    const val THRUSTER_CHARGE_DEFAULT = 0.3f
    const val WEAPON_RADIUS_DEFAULT = 60f
    const val BOMB_RADIUS_DEFAULT = 250f
    const val BOMB_CHARGE_DEFAULT = 5f
    val PROJECTILE_COLOR_DEFAULT = Color.GREEN

    var FIRE_FORCE_CURRENT = FIRE_FORCE_DEFAULT
    var WEAPON_CHARGE_CURRENT = WEAPON_CHARGE_DEFAULT
    var WEAPON_RADIUS_CURRENT = WEAPON_RADIUS_DEFAULT
    var BOMB_RADIUS_CURRENT = BOMB_RADIUS_DEFAULT
    var BOMB_CHARGE_CURRENT = BOMB_CHARGE_DEFAULT
    var PROJECTILE_COLOR_CURRENT = PROJECTILE_COLOR_DEFAULT

    // UPGRADE MODIFIERS
    const val FORWARD_MAGNITUDE_MODIFIER = 1.25f
    const val TURN_MAGNITUDE_MODIFIER = 1.25f
    const val ATTRACTION_MODIFIER = 2f
    const val FIRE_FORCE_MODIFIER = 1f
    const val WEAPON_CHARGE_MODIFER = 0.25f
    const val POWER_MODIFER = 1.4f

    fun reset() {
        FIRE_FORCE_CURRENT = FIRE_FORCE_DEFAULT
        WEAPON_CHARGE_CURRENT = WEAPON_CHARGE_DEFAULT
        WEAPON_RADIUS_CURRENT = WEAPON_RADIUS_DEFAULT
        BOMB_RADIUS_CURRENT = BOMB_RADIUS_DEFAULT
        BOMB_CHARGE_CURRENT = BOMB_CHARGE_DEFAULT
        PROJECTILE_COLOR_CURRENT = PROJECTILE_COLOR_DEFAULT
    }
}