package com.mygdx.ships

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.mygdx.ships.entity.Mappers
import com.mygdx.ships.entity.components.*
import com.mygdx.ships.entity.systems.CameraSystem
import com.mygdx.ships.entity.systems.RenderSystem
import com.mygdx.ships.menus.Button
import java.text.SimpleDateFormat
import java.util.*

class EditorScreen(internal var gameScreen: GameScreen) : ScreenAdapter() {
    internal var gameCamera: OrthographicCamera
    internal var guiCamera: OrthographicCamera
    internal var camWidth: Float = 0.toFloat()
    internal var camHeight: Float = 0.toFloat()
    internal var batch: SpriteBatch
    internal var shapeRenderer: ShapeRenderer
    internal var font: BitmapFont
    internal var layout: GlyphLayout
    internal var textInputListener: Input.TextInputListener
    internal var freeCamOn = false
    internal var recordArray = Array<Vector2>()
    internal var line_mode = 0
    internal var polygon_mode = 1
    internal var preview_mode = polygon_mode
    internal var terrainModeOn = true
    internal var selectingEntity = false
    internal var currentLayer = ""
    internal var camSpeed = 5f
    internal var selectedEntity: Entity? = null
    internal var originalColor = Color.WHITE
    internal var preview: Entity

    internal var freeCam: Button
    internal var resetCam: Button
    internal var undoAll: Button
    internal var saveFile: Button
    internal var loadLine: Button
    internal var loadPolygon: Button
    internal var loadTerrain: Button
    internal var saveTemp: Button
    internal var saveTerrain: Button
    internal var saveEntity: Button
    internal var changeMode: Button
    internal var changeLayer: Button
    internal var editEntity: Button
    internal var deleteEntity: Button
    internal var terrainMode: Button
    internal var updatePaths: Button
    internal var unlockEnergy: Button
    internal var unlockXP: Button
    internal var back: Button
    internal val buttons = arrayListOf<Button>()

    init {
        batch = Main.spriteBatch
        camWidth = Gdx.graphics.width.toFloat()
        camHeight = Gdx.graphics.height.toFloat()
        guiCamera = OrthographicCamera(camWidth, camHeight)
        guiCamera.position.set(camWidth / 2, camHeight / 2, 0f)
        gameCamera = Main.gameScreen.gameCamera
        font = Main.assets.get<BitmapFont>(AssetPaths.FONT_SMALL)
        layout = GlyphLayout()
        shapeRenderer = ShapeRenderer()

        freeCam = Button("Free cam", camWidth - 110, camHeight - 20, layout, font)
        buttons.add(freeCam)
        resetCam = Button("Reset zoom", camWidth - 115, camHeight - 50f - layout.height, layout, font)
        buttons.add(resetCam)
        undoAll = Button("Save to file", 10f, camHeight - 170, layout, font)
        buttons.add(undoAll)
        changeMode = Button("Preview mode", undoAll.bounds.x, undoAll.bounds.y - 50, layout, font)
        buttons.add(changeMode)
        loadLine = Button("Load temp as line", 200f, 10f, layout, font)
        buttons.add(loadLine)
        loadPolygon = Button("Load temp as polygon", loadLine.bounds.x, loadLine.bounds.y + 20, layout, font)
        buttons.add(loadPolygon)
        loadTerrain = Button("Reload terrain entities", loadLine.bounds.x, loadLine.bounds.y + 40, layout, font)
        buttons.add(loadTerrain)
        saveFile = Button("Save to file", 10f, 10f, layout, font)
        buttons.add(saveFile)
        saveTemp = Button("Save to temp", saveFile.bounds.x, saveFile.bounds.y + 20, layout, font)
        buttons.add(saveTemp)
        saveTerrain = Button("Save to terrain", saveFile.bounds.x, saveFile.bounds.y + 40, layout, font)
        buttons.add(saveTerrain)
        saveEntity = Button("Overwrite to selected entity", saveFile.bounds.x, saveFile.bounds.y + 60, layout, font)
        buttons.add(saveEntity)
        changeLayer = Button("Change", camWidth - 150, camHeight / 2, layout, font)
        buttons.add(changeLayer)
        editEntity = Button("Edit entity", 10f, 120f, layout, font)
        buttons.add(editEntity)
        deleteEntity = Button("Delete entity", editEntity.bounds.x, editEntity.bounds.y + 30, layout, font)
        buttons.add(deleteEntity)
        terrainMode = Button("Terrain mode:", camWidth - 150, camHeight / 2 + 150, layout, font)
        buttons.add(terrainMode)
        updatePaths = Button("Update paths", 10f, camHeight - 100, layout, font)
        buttons.add(updatePaths)
        unlockXP = Button("Unlimited EXP", 1100f, 120f, layout, font)
        buttons.add(unlockXP)
        unlockEnergy = Button("Unlimited energy", 1100f, 70f, layout, font)
        buttons.add(unlockEnergy)
        back = Button("Back to game", 1100f, 20f, layout, font)
        buttons.add(back)

        preview = Entity()
        val transform = Transform(0f, 0f, 0, 0f, 1f)
        preview.add(transform)
        preview.add(ColorComponent(Color.RED))
        Main.engine.addEntity(preview)

        textInputListener = object : Input.TextInputListener {
            override fun input(text: String) {
                currentLayer = text
            }

            override fun canceled() {
            }
        }
    }

    override fun render(deltaTime: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        draw(deltaTime)
        update()
    }

    fun draw(deltaTime: Float) {
        Main.gameScreen.engine.update(deltaTime)

        guiCamera.update()
        batch.projectionMatrix = guiCamera.combined
        batch.enableBlending()
        batch.begin()

        font.color = Color.YELLOW
        font.data.setScale(2f)
        font.draw(batch, "EDITOR", 0f, camHeight)
        if (selectingEntity)
            font.draw(batch, "SELECTING ENTITY", camWidth / 2 - 100, camHeight)
        font.data.setScale(1f)

        font.color = Color.WHITE
        font.draw(batch, "Current layer:\n" + currentLayer, changeLayer.bounds.x, changeLayer.bounds.y + 70)

        if (freeCamOn)
            font.draw(batch, "ON", freeCam.bounds.x - 50f, freeCam.bounds.y - 5)
        else
            font.draw(batch, "OFF", freeCam.bounds.x - 50f, freeCam.bounds.y - 5)
        if (terrainModeOn)
            font.draw(batch, "ON", terrainMode.bounds.x, terrainMode.bounds.y - 5)
        else
            font.draw(batch, "OFF", terrainMode.bounds.x, terrainMode.bounds.y - 5)

        if (preview_mode == polygon_mode)
            font.draw(batch, "polygon", changeMode.bounds.x, changeMode.bounds.y - 5)
        else
            font.draw(batch, "line", changeMode.bounds.x, changeMode.bounds.y - 5)

        val touch = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        guiCamera.unproject(touch)

        font.color = Color.WHITE
        buttons.forEach { it.draw(batch, touch, font) }
        font.draw(batch, freeCam.buttonText, freeCam.bounds.x, freeCam.bounds.y)

        batch.end()


        // Draw last dot
        shapeRenderer.projectionMatrix = gameCamera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = Color.ORANGE
        if (recordArray.size > 0) {
            val lastPoint = recordArray.get(recordArray.size - 1)
            shapeRenderer.box(lastPoint.x - 1, lastPoint.y - 1, 0f, 2f, 2f, 0f)
        }
        shapeRenderer.end()
    }

    private fun update() {
        if (Gdx.input.isKeyPressed(GameData.Controls.ZOOM_OUT)) {
            gameCamera.zoom += 0.3f
        }
        if (Gdx.input.isKeyPressed(GameData.Controls.ZOOM_IN)) {
            gameCamera.zoom -= 0.3f
        }
        if (Gdx.input.isKeyJustPressed(GameData.Controls.UNDO)) {
            if (recordArray.size > 1)
                recordArray.removeIndex(recordArray.size - 1)
            updatePreview()
        }
        if (Gdx.input.isKeyJustPressed(GameData.Controls.SELECT_ENTITY)) {
            recordArray.clear()
            updatePreview()
            selectingEntity = !selectingEntity
        }
        if (Gdx.input.isKeyJustPressed(GameData.Controls.DELETE_ENTITY)) {
            deleteEntity()
            recordArray.clear()
            updatePreview()
            gameScreen.reloadTerrain()
        }
        if (freeCamOn) {
            if (Gdx.input.isKeyPressed(GameData.Controls.UP)) gameCamera.position.y += camSpeed * gameCamera.zoom
            if (Gdx.input.isKeyPressed(GameData.Controls.DOWN)) gameCamera.position.y -= camSpeed * gameCamera.zoom
            if (Gdx.input.isKeyPressed(GameData.Controls.LEFT)) gameCamera.position.x -= camSpeed * gameCamera.zoom
            if (Gdx.input.isKeyPressed(GameData.Controls.RIGHT)) gameCamera.position.x += camSpeed * gameCamera.zoom
            gameCamera.update()
        }

        if (Gdx.input.justTouched()) {
            val touch = Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            guiCamera.unproject(touch)

            if (back.bounds.contains(touch.x, touch.y)) {
                Main.game.screen = Main.gameScreen
                return
            }

            if (unlockEnergy.bounds.contains(touch.x, touch.y)) {
                GameData.energyMax = 1000f
                GameData.energy = 1000f
                return
            }

            if (unlockXP.bounds.contains(touch.x, touch.y)) {
                GameData.upgradePoints = 1000
                Main.levelupScreen.shuffleUpgrades()
                return
            }

            if (selectingEntity) {
                val zoom = gameCamera.zoom
                selectClosestEntity(
                        gameCamera.position.x + (touch.x - camWidth / 2) * zoom,
                        gameCamera.position.y + (touch.y - camHeight / 2) * zoom)
                selectingEntity = false
                return
            }
            if (freeCam.bounds.contains(touch.x, touch.y)) {
                gameScreen.engine.getSystem(CameraSystem::class.java).setProcessing(freeCamOn)
                freeCamOn = !freeCamOn
                return
            }
            if (resetCam.bounds.contains(touch.x, touch.y)) {
                gameCamera.zoom = 1.5f
                return
            }
            if (saveFile.bounds.contains(touch.x, touch.y)) {
                saveFile()
                return
            }
            if (saveTemp.bounds.contains(touch.x, touch.y)) {
                saveTemp()
                return
            }
            if (saveTerrain.bounds.contains(touch.x, touch.y)) {
                saveTerrain()
                return
            }
            if (saveEntity.bounds.contains(touch.x, touch.y)) {
                saveEntity()
                return
            }
            if (undoAll.bounds.contains(touch.x, touch.y)) {
                recordArray.clear()
                updatePreview()
                return
            }
            if (loadLine.bounds.contains(touch.x, touch.y)) {
                loadTempLines()
                return
            }
            if (loadPolygon.bounds.contains(touch.x, touch.y)) {
                loadTempPolygon()
                return
            }
            if (loadTerrain.bounds.contains(touch.x, touch.y)) {
                gameScreen.reloadTerrain()
                return
            }
            if (changeMode.bounds.contains(touch.x, touch.y)) {
                if (preview_mode == line_mode)
                    preview_mode = polygon_mode
                else if (preview_mode == polygon_mode) preview_mode = line_mode
                updatePreview()
                return
            }
            if (changeLayer.bounds.contains(touch.x, touch.y)) {
                Gdx.input.getTextInput(textInputListener, "New layer", "", "")
                return
            }
            if (editEntity.bounds.contains(touch.x, touch.y)) {
                recordArray.clear()
                updatePreview()
                selectingEntity = !selectingEntity
                return
            }
            if (terrainMode.bounds.contains(touch.x, touch.y)) {
                terrainModeOn = !terrainModeOn
                return
            }
            if (deleteEntity.bounds.contains(touch.x, touch.y)) {
                deleteEntity()
                recordArray.clear()
                updatePreview()
                gameScreen.reloadTerrain()
                return
            }
            if (terrainModeOn && Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                if (selectedEntity != null)
                    saveEntity()
                else
                    saveTerrain()
                recordArray.clear()
                updatePreview()
                gameScreen.reloadTerrain()
                return
            }
            if (updatePaths.bounds.contains(touch.x, touch.y)) updatePaths()

            // Record points, if no menus buttons were pressed
            val zoom = gameCamera.zoom
            recordArray.add(Vector2(
                    gameCamera.position.x + (touch.x - camWidth / 2) * zoom,
                    gameCamera.position.y + (touch.y - camHeight / 2) * zoom))
            updatePreview()
        }
    }

    private fun saveFile() {
        // remove loose points
//        if (recordArray.size % 2 != 0) recordArray.removeIndex(recordArray.size - 1)
        val dateFormat = SimpleDateFormat("dd.MM.yy HH-mm-ss")
        val date = Date()
        val file = Gdx.files.local("editor_out/" + dateFormat.format(date) + ".txt")
        for (point in recordArray) {
            file.writeString(point.x.toString() + " " + point.y, true)
            file.writeString("\n", true)
        }
    }

    private fun saveTemp() {
        // remove loose points if in line mode(lines require even number of points
        if (line_mode == line_mode && recordArray.size % 2 != 0)
            recordArray.removeIndex(recordArray.size - 1)
        val file = Gdx.files.local(AssetPaths.EDITOR_TEMP)
        file.writeString("", false)
        for (i in 0..recordArray.size - 1) {
            file.writeString("" + recordArray.get(i).x + " " + recordArray.get(i).y, true)
            if (i != recordArray.size - 1) file.writeString("\n", true)
        }
    }

    private fun saveTerrain() {
        if (recordArray.size < 3) {
            println("Terrain must have at least 3 vertices")
            return
        }

        var incorrectLayer = true
        for (type in GameData.resourceOrder) {
            if (type == currentLayer)
                incorrectLayer = false
        }
        if (incorrectLayer) {
//            println("No layer \"$currentLayer\" found in GameData")
//            return
            currentLayer = "gray"
        }

        val dateFormat = SimpleDateFormat("dd.MM.yy HH-mm-ss")
        val date = Date()
        val file = Gdx.files.local(
                AssetPaths.TERRAIN_DIR + "/" + currentLayer + "/" + dateFormat.format(date) + ".txt")
        file.writeString("", false)
        for (i in 0..recordArray.size - 1) {
            file.writeString("" + recordArray.get(i).x + " " + recordArray.get(i).y, true)
            if (i != recordArray.size - 1) file.writeString("\n", true)
        }

        updatePaths()
    }

    private fun saveEntity() {
        if (selectedEntity == null) {
            println("No entity selected")
            return
        }

        if (recordArray.size < 3) {
            println("Terrain must have at least 3 vertices")
            return
        }

        val type = Mappers.terrainComponent.get(selectedEntity).type
        val dir = Gdx.files.local(AssetPaths.TERRAIN_DIR + "/" + type)
        val files = dir.list()

        val polygonComponent = Mappers.polygonComponent.get(selectedEntity)
        val entityVertices = polygonComponent.polygon
        var file: FileHandle? = null
        for (i in files.indices) {
            val vertices = files[i].toVertices()

            if (Math.round(vertices[0]) == Math.round(entityVertices!![0]) && Math.round(vertices[1]) == Math.round(entityVertices[1])) {
                file = files[i]
                break
            }
        }
        if (file == null) {
            println("No terrain file found")
            return
        }

        file.writeString("", false)
        for (i in 0..recordArray.size - 1) {
            file.writeString("" + recordArray.get(i).x + " " + recordArray.get(i).y, true)
            if (i != recordArray.size - 1) file.writeString("\n", true)
        }

        updatePaths()

        selectedEntity = null
    }

    private fun deleteEntity() {
        if (selectedEntity == null) {
            println("No entity selected")
            return
        }

        val type = Mappers.terrainComponent.get(selectedEntity).type
        val dir = Gdx.files.local(AssetPaths.TERRAIN_DIR + "/" + type)
        val files = dir.list()

        val polygonComponent = Mappers.polygonComponent.get(selectedEntity)
        val entityVertices = polygonComponent.polygon
        var file: FileHandle? = null
        for (i in files.indices) {
            val vertices = files[i].toVertices()

            if (Math.round(vertices[0]) == Math.round(entityVertices!![0]) && Math.round(vertices[1]) == Math.round(entityVertices[1])) {
                file = files[i]
                break
            }
        }
        if (file == null) {
            println("No terrain file found")
            return
        }

        file.delete()
        updatePaths()

        selectedEntity = null
    }

    private fun loadTempLines() {
        val entity = Entity()
        val transform = Transform(0f, 0f, 0, 0f, 1f)
        entity.add(transform)
        entity.add(ColorComponent(Color.RED))
        val file = Gdx.files.local(AssetPaths.EDITOR_TEMP)
        val lines = file.toVertices()
        if (lines.size < 4) {
            println("Error loading line file:\n" + "Line must containt at least 2 points")
            return
        }
        entity.add(LineComponent(lines))
        Main.engine.addEntity(entity)
    }

    private fun loadTempPolygon() {
        val entity = Entity()
        val transform = Transform(0f, 0f, 0, 0f, 1f)
        entity.add(transform)
        entity.add(ColorComponent(Color.RED))
        val file = Gdx.files.local(AssetPaths.EDITOR_TEMP)
        val polygon = file.toVertices()
        if (polygon.size < 3 * 2) {
            println("Error loading temp file:\n" + "Polygon must containt at least 3 points")
            return
        }
        entity.add(PolygonComponent(polygon))
        Main.engine.addEntity(entity)
    }

    // Updates preview, if there's an even amount of vertices
    private fun updatePreview() {
        if (recordArray.size < 2)
            preview.remove(LineComponent::class.java)
        else if (preview_mode == line_mode) {
            var size = recordArray.size
            if (size % 2 != 0) size--

            val lines = FloatArray(size * 2)

            for (i in 0..size - 1) {
                lines[i * 2] = recordArray.get(i).x
                lines[i * 2 + 1] = recordArray.get(i).y
            }
            preview.remove(LineComponent::class.java)
            preview.add(LineComponent(lines))
        } else if (preview_mode == polygon_mode) {
            val array = Array<Vector2>()
            array.add(recordArray.get(0))
            for (point in recordArray) {
                array.add(point)
                array.add(point)
            }
            array.add(recordArray.get(0))
            var size = array.size
            if (size % 2 != 0) size--

            val lines = FloatArray(size * 2)

            for (i in 0..size - 1) {
                lines[i * 2] = array.get(i).x
                lines[i * 2 + 1] = array.get(i).y
            }
            preview.remove(LineComponent::class.java)
            preview.add(LineComponent(lines))
        }
    }

    private fun selectClosestEntity(x: Float, y: Float) {
        var distance = 1000000f
        val terrainEntities = Main.engine.getEntitiesFor(Family.all(TerrainComponent::class.java).get())
        var closestEntity = terrainEntities.get(0)
        for (i in 0..terrainEntities.size() - 1) {
            val entity = terrainEntities.get(i)
            val pc = Mappers.polygonComponent.get(entity) ?: continue
            val vertices = pc.polygon
            val centroid = vertices!!.getCentroid()
            val currentDistance = centroid.dst2(x, y)
            if (currentDistance < distance) {
                distance = currentDistance
                closestEntity = entity
            }
        }
        if (selectedEntity != null) {
            Mappers.colorComponent.get(selectedEntity).set(originalColor)
        }
        val colorComponent = Mappers.colorComponent.get(closestEntity)
        originalColor = colorComponent.color
        colorComponent.set(Color.PINK)
        selectedEntity = closestEntity
    }

    private fun updatePaths() {
        val pathFile = Gdx.files.local(AssetPaths.TERRAIN_PATHS)
        pathFile.writeString("", false)
        for (type in GameData.resourceOrder) {
            val dir = Gdx.files.local(AssetPaths.TERRAIN_DIR + "/" + type)
            if (dir.isDirectory) {
                val files = dir.list()
                files.forEach {
                    val fileName = it.toString()
                    pathFile.writeString("$fileName\n", true)
                }
            }
        }
    }
}
