package com.mygdx.ships.menus.main

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector3
import com.mygdx.ships.*
import com.mygdx.ships.entity.EntityFactory
import com.mygdx.ships.entity.Mappers
import com.mygdx.ships.menus.Button

class LevelupScreen(menuScreen: MenuScreen) : SlidingScreen(menuScreen) {
    internal val batch = Main.spriteBatch
    internal val font: BitmapFont = Main.assets.get<BitmapFont>(AssetPaths.FONT)
    internal val fontBig: BitmapFont = Main.assets.get<BitmapFont>(AssetPaths.FONT_BIG)
    internal val layout = GlyphLayout()
    internal val thruster = Button("Thrusters", 0f, 0f, 0, layout, font)
    internal val plusWeapon = Button("+Weapon", 0f, 0f, 0, layout, font)
    internal val plusBomb = Button("+Antimatter pulse", 0f, 0f, 0, layout, font)
    internal val attractor = Button("Ore tractor", 0f, 0f, 0, layout, font)
    internal val accuracy = Button("Accuracy", 0f, 0f, 0, layout, font)
    internal val firespeed = Button("Firing speed", 0f, 0f, 0, layout, font)
    internal val power = Button("Weapon power", 0f, 0f, 0, layout, font)
    internal val stress = Button("Fuel actuator", 0f, 0f, 0, layout, font)
    internal val buttons = arrayListOf<Button>()
    internal val buttonPool = arrayListOf(thruster, plusWeapon, plusBomb, attractor, accuracy, firespeed, power, stress)
    internal val texts = hashMapOf(
            "Thrusters" to "Increase ship speed and maneuverability",
            "+Weapon" to "Get an additional weapon",
            "+Antimatter pulse" to "Automatic pulse that mines asteroids around the ship",
            "Ore tractor" to "Attract ore from further away",
            "Accuracy" to "Improve accuracy of all weapons",
            "Firing speed" to "Increase firing speed of all weapons",
            "Weapon power" to "Increase power of all weapons",
            "Fuel actuator" to "Firing speed doubles when fuel is below 40%")
    internal var count = hashMapOf(
            "Thrusters" to 0,
            "+Weapon" to 0,
            "+Antimatter pulse" to 0,
            "Ore tractor" to 0,
            "Accuracy" to 0,
            "Firing speed" to 0,
            "Weapon power" to 0,
            "Fuel actuator" to 0)
    internal var selectedButton: Button? = null
    internal val back = Button("Back", camWidth + camWidth / 2, 100f, 0, layout, font)
    internal var scaleStart = 1f
    internal var scaleEnd = 1.5f
    internal var scale = scaleStart

    override fun draw() {
        camera.update()
        batch.projectionMatrix = camera.combined
        batch.begin()
        batch.enableBlending()

        val touch = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        camera.unproject(touch)
        back.draw(batch, touch, font)

        if (GameData.upgradePoints == 0) {
            font.color = Color.LIGHT_GRAY
            layout.setText(font, "No more upgrades currently available")
            font.draw(batch, "No more upgrades currently available", camWidth + camWidth / 2 - layout.width / 2, 500f)
            batch.end()
            return
        }

        fontBig.color = Color.WHITE
        layout.setText(fontBig, "Select an upgrade")
        fontBig.draw(batch, "Select an upgrade", camWidth + 640f - layout.width / 2, 650f)

        font.color = Color.WHITE
        buttons.forEach {
            if (selectedButton != null && it == selectedButton) {
                layout.setText(font, it.buttonText)
                var widthUnscaled = layout.width / 2
                font.data.setScale(scale)
                font.color.a = (scaleEnd - scale) / (scaleEnd - scaleStart)
                layout.setText(font, it.buttonText)
                font.draw(batch, it.buttonText,
                        it.bounds.x + widthUnscaled - layout.width / 2,
                        it.bounds.y + layout.height)
                font.data.setScale(1f)

                val text = texts[it.buttonText]
                layout.setText(font, text)
                font.color = Color.LIGHT_GRAY
                font.draw(batch, text, camWidth + 640f - layout.width / 2, 300f + layout.height / 2)
            } else {
                it.draw(batch, touch, font)
            }
            font.color = Color.LIGHT_GRAY

            if (selectedButton == null) {
                if (it.bounds.contains(touch.x, touch.y)) {
                    val text = texts[it.buttonText]
                    layout.setText(font, text)
                    font.draw(batch, text, camWidth + 640f - layout.width / 2, 300f + layout.height / 2)
                }
            }
        }

        batch.end()
    }

    override fun update() {
        if (selectedButton != null) {
            if (scale < scaleEnd - 0.05f) {
                scale += (scaleEnd - scale) * 0.07f
            } else {
                scale = scaleStart
                if (selectedButton == plusWeapon) {
                    // reset state so that it starts correctly even though there was no transition out
                    state = SCREEN_STATE.START
                    Main.game.screen = Main.fitScreen
                }
                selectedButton = null
                GameData.upgradePoints--
                if (GameData.upgradePoints > 0)
                    shuffleUpgrades()
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            state = SCREEN_STATE.END
            return
        }

        if (Gdx.input.justTouched()) {
            val touch = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            camera.unproject(touch)

            if (back.bounds.contains(touch.x, touch.y)) {
                this.state = SCREEN_STATE.END
                return
            }

            if (GameData.upgradePoints <= 0) return

            if (selectedButton != null) return // ignore input during the animation
            if (thruster.bounds.contains(touch.x, touch.y)) {
                val player = Main.gameScreen.player
                val pm = Mappers.playerMovement.get(player)
                pm.forwardMagnitude *= Constants.FORWARD_MAGNITUDE_MODIFIER
                pm.turnMagnitude *= Constants.TURN_MAGNITUDE_MODIFIER

                count["Thrusters"] = (count["Thrusters"] ?: 0) + 1
                selectedButton = thruster

                return
            }
            if (plusWeapon.bounds.contains(touch.x, touch.y)) {
                Main.fitScreen.addGun()

                count["+Weapon"] = (count["+Weapon"] ?: 0) + 1
                selectedButton = plusWeapon

                return
            }
            if (plusBomb.bounds.contains(touch.x, touch.y)) {
                if (Main.gameScreen.timebomb == null) {
                    val timebomb = EntityFactory.timebomb(Main.gameScreen.player)
                    Main.gameScreen.timebomb = timebomb
                }

                count["+Antimatter pulse"] = (count["+Antimatter pulse"] ?: 0) + 1
                if (count["+Antimatter pulse"] ?: 0 >= 1) buttonPool.remove(plusBomb)
                selectedButton = plusBomb

                return
            }
            if (attractor.bounds.contains(touch.x, touch.y)) {
                val player = Main.gameScreen.player
                val lc = Mappers.leaderComponent.get(player)
                lc.attractionRadius *= Constants.ATTRACTION_MODIFIER

                count["Ore tractor"] = (count["Ore tractor"] ?: 0) + 1
                if (count["Ore tractor"] ?: 0 >= 2) buttonPool.remove(attractor)

                selectedButton = attractor

                return
            }
            if (accuracy.bounds.contains(touch.x, touch.y)) {
                Main.gameScreen.guns.forEach {
                    val wc = Mappers.weaponComponent.get(it)
                    wc.projectileForce /= Constants.FIRE_FORCE_CURRENT
                }

                Constants.FIRE_FORCE_CURRENT += Constants.FIRE_FORCE_MODIFIER

                Main.gameScreen.guns.forEach {
                    val wc = Mappers.weaponComponent.get(it)
                    wc.projectileForce *= Constants.FIRE_FORCE_CURRENT
                }

                count["Accuracy"] = (count["Accuracy"] ?: 0) + 1
                if (count["Accuracy"] ?: 0 >= 3) buttonPool.remove(accuracy)

                selectedButton = accuracy

                return
            }
            if (firespeed.bounds.contains(touch.x, touch.y)) {
                Constants.BOMB_CHARGE_CURRENT *= 0.8f
                if (Main.gameScreen.timebomb != null) {
                    val bc = Mappers.bombComponent.get(Main.gameScreen.timebomb)
                    bc.charge = Constants.BOMB_CHARGE_CURRENT
                }

                if (count["Firing speed"] ?: 0 >= 3)
                    Constants.WEAPON_CHARGE_CURRENT -= Constants.WEAPON_CHARGE_MODIFER / 2
                else Constants.WEAPON_CHARGE_CURRENT -= Constants.WEAPON_CHARGE_MODIFER
                Main.gameScreen.guns.forEach {
                    val wc = Mappers.weaponComponent.get(it)
                    wc.charge = Constants.WEAPON_CHARGE_CURRENT
                    wc.chargeTimer = wc.charge // reset charge
                }
                count["Firing speed"] = (count["Firing speed"] ?: 0) + 1
                if (count["Firing speed"] ?: 0 >= 4) buttonPool.remove(firespeed)

                selectedButton = firespeed

                return
            }
            if (power.bounds.contains(touch.x, touch.y)) {
                if (Main.gameScreen.timebomb != null) {
                    val bc = Mappers.bombComponent.get(Main.gameScreen.timebomb)
                    bc.explosion!!.scale(1.2f)
                }
                Constants.BOMB_RADIUS_CURRENT *= 1.2f

                if (Constants.PROJECTILE_COLOR_CURRENT == Color.GREEN)
                    Constants.PROJECTILE_COLOR_CURRENT = Color.YELLOW
                else if (Constants.PROJECTILE_COLOR_CURRENT == Color.YELLOW)
                    Constants.PROJECTILE_COLOR_CURRENT = Color.RED
                else if (Constants.PROJECTILE_COLOR_CURRENT == Color.RED)
                    Constants.PROJECTILE_COLOR_CURRENT = Color.PURPLE
                else if (Constants.PROJECTILE_COLOR_CURRENT == Color.PURPLE)
                    Constants.PROJECTILE_COLOR_CURRENT = Color.SLATE
                else if (Constants.PROJECTILE_COLOR_CURRENT == Color.SLATE)
                    Constants.PROJECTILE_COLOR_CURRENT = Color.ROYAL

                Constants.WEAPON_RADIUS_CURRENT *= Constants.POWER_MODIFER

                Main.gameScreen.guns.forEach {
                    val wc = Mappers.weaponComponent.get(it)
                    val oldRadius = wc.projectileExplosion!!.getRadius()
                    wc.projectileExplosion!!.scale(Constants.WEAPON_RADIUS_CURRENT / oldRadius)
                    wc.color = Constants.PROJECTILE_COLOR_CURRENT
                }


                count["Weapon power"] = (count["Weapon power"] ?: 0) + 1

                selectedButton = power

                return
            }
            if (stress.bounds.contains(touch.x, touch.y)) {
                GameData.stressUnlocked = true
                buttonPool.remove(stress)

                count["Fuel actuator"] = (count["Fuel actuator"] ?: 0) + 1
                if (count["Fuel actuator"] ?: 0 >= 1) buttonPool.remove(attractor)

                selectedButton = stress

                return
            }

        }
    }

    fun shuffleUpgrades() {
        buttonPool.forEach {
            it.setPosition(0f, 2000f)
        }
        buttons.clear()
        buttonPool.shuffle()
        for (i in 0..Math.min(2, buttonPool.size - 1)) {
            val button = buttonPool[i]
            button.setPosition(camWidth + 290f + i * 350f, 450f)
            buttons.add(buttonPool[i])
        }
    }

    override fun hide() {
        scale = scaleStart
        selectedButton = null
    }

    fun reset() {
        scale = scaleStart
        selectedButton = null
        buttonPool.clear()
        buttonPool.add(thruster)
        buttonPool.add(plusWeapon)
        buttonPool.add(plusBomb)
        buttonPool.add(attractor)
        buttonPool.add(accuracy)
        buttonPool.add(firespeed)
        buttonPool.add(power)
        buttonPool.add(stress)
        count = hashMapOf(
                "Thrusters" to 0,
                "+Weapon" to 0,
                "+Antimatter pulse" to 0,
                "Ore tractor" to 0,
                "Accuracy" to 0,
                "Firing speed" to 0,
                "Weapon power" to 1,
                "Fuel actuator" to 0)
    }
}

