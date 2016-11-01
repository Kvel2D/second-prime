package com.mygdx.ships.menus

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2

class GunIcon {
    internal var positionDefault: Vector2;
    var position: Vector2;
    var cursorOffset: Vector2;
    var angle: Float;
    var dragged = false;
    var rotated = false;
    var myEntity: Entity? = null;

    constructor(x: Float, y: Float, angle: Float) {
        position = Vector2(x, y)
        cursorOffset = Vector2(0f, 0f)
        positionDefault = Vector2(x, y)
        this.angle = angle
    }

    fun resetPosition() {
        position.set(positionDefault)
    }
}
