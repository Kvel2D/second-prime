package com.mygdx.ships

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.mygdx.ships.entity.EntityFactory

var typeProbabilities = hashMapOf(
        "blue" to 0,
        "tan" to 0,
        "brown" to 0,
        "teal" to 0,
        "orange" to 0,
        "purple" to 0,
        "sky" to 0,
        "magenta" to 0,
        "olive" to 0,
        "gold" to 0,
        "neongreen" to 0,
        "green" to 0,
        "red" to 0,
        "slate" to 0)

val types = arrayListOf<String>()
val shapes = hashMapOf<String, MutableList<FloatArray>>()

fun generateLayeredTerrain() {
    var percentFilled = 0.15f
    var scale = 1f

    loadShapes()
    var yOffset = 400f;
    for (i in 0..20) {

        typeProbabilities.forEach { s, i -> typeProbabilities[s] = 0 }
        when (i) {
            0, 1 -> {
                typeProbabilities["blue"] = 2
                typeProbabilities["sky"] = 2
            }
            2, 3 -> {
                typeProbabilities["neongreen"] = 2
                typeProbabilities["green"] = 2
            }
            4, 5 -> {
                typeProbabilities["brown"] = 2
                typeProbabilities["olive"] = 2
            }
            6, 7 -> {
                typeProbabilities["sky"] = 2
            }
            8, 9 -> {
                typeProbabilities["purple"] = 2
                typeProbabilities["red"] = 2
                typeProbabilities["magenta"] = 1
            }
            10, 11 -> {
                typeProbabilities["magenta"] = 2
                typeProbabilities["orange"] = 2
            }
            12, 13 -> {
                typeProbabilities["slate"] = 2
                typeProbabilities["tan"] = 2
                typeProbabilities["gold"] = 1
            }
            14, 15 -> {
                typeProbabilities["gold"] = 2
            }
            16 -> {
                typeProbabilities["blue"] = 2
                typeProbabilities["gold"] = 1
            }
            17, 18, 19 -> {
                typeProbabilities["slate"] = 2
                typeProbabilities["blue"] = 1
            }
            20 -> {
                typeProbabilities["tan"] = 2
                typeProbabilities["slate"] = 2
                typeProbabilities["blue"] = 1
            }
        }
        val tealChance = (Math.random() * 3).toInt()
        if (tealChance == 0 && i > 1) typeProbabilities["teal"] = 1

        scale += 1 / 4f
        percentFilled += 0.02f

        yOffset += generateTerrain4(yOffset, percentFilled, scale)
    }

    yOffset = 700f;
    for (i in 0..10) {
        yOffset += generateGray(yOffset, 0.3f)
    }
}

fun generateGray(startY: Float, percentFilled: Float): Float {
    val amount = 2000

    if (shapes.isEmpty()) return 0f

    val radius = shapes["gray"]!![0].getRadius()
    var gridWidth = 3000f;
    var gridHeight = 30 * radius;
    var gridSpacing = 3f * radius;
    val vectors: MutableList<Vector2> = createGrid(-gridWidth / 2, 0f, gridWidth, gridHeight, gridSpacing)

    vectors.shuffle()

    var index = 0
    var yOffset = startY
    for (i in 0..amount) {
        // Check if current layer has been filled with n entities
        // where n = amount of points * percent filled
        if (i % ((vectors.size - 1) * percentFilled).toInt() == 0 && i != 0) {
            break;
        }

        // Get a random shape
        val k = (Math.random() * shapes["gray"]!!.size).toInt()
        val shape = shapes["gray"]!![k].clone()
        val level = GameData.resourceLevels["gray"] ?: 0
        val color = GameData.resourceColors["gray"] ?: Color.WHITE

        // Get coordinate from the Utils.shuffled list
        // if list is exhausted, reUtils.shuffle it
        val x = vectors[index].x + Math.random().toFloat() * 600f - 300f
        val y = vectors[index].y + Math.random().toFloat() * 300f - 150f
        index++
        if (index > vectors.size - 1) {
            index = 0
            vectors.shuffle()
        }

        val angle = (Math.random() * 180).toFloat()

        shape.translate(x, y + yOffset, angle)

        val entity = EntityFactory.terrain(shape, "gray", level, color)
        Main.engine.addEntity(entity)
    }

    return (gridHeight + radius * 2)
}

fun generateTerrain4(startY: Float, percentFilled: Float, scale: Float): Float {
    val amount = 2000

    types.clear()
    typeProbabilities.forEach { s, p ->

        if (p != 0) {
            for (i in 0..(p - 1)) {
                types.add(s)
            }
        }

    }
    if (types.isEmpty()) return 0f
    if (shapes.isEmpty()) return 0f

    val radius = shapes["blue"]!![0].getRadius() * scale
    var gridWidth = 3000f;
    var gridHeight = 30 * radius;
    var gridSpacing = 3f * radius;
    val vectors: MutableList<Vector2> = createGrid(-gridWidth / 2, 0f, gridWidth, gridHeight, gridSpacing)

    vectors.shuffle()

    var index = 0
    var yOffset = startY
    for (i in 0..amount) {
        // Check if current layer has been filled with n entities
        // where n = amount of points * percent filled
        if (i % ((vectors.size - 1) * percentFilled).toInt() == 0 && i != 0) {
            break;
        }

        // Get a random shape and random terrain type(not gray)
        var k = (Math.random() * types.size).toInt()
        var type = types[k]
        while (type == "gray") {
            k = (Math.random() * types.size).toInt()
            type = types[k]
        }
        k = (Math.random() * shapes[type]!!.size).toInt()
        val shape = shapes[type]!![k].clone()
        shape.scale(scale)
        val level = GameData.resourceLevels[type] ?: 0
        val color = GameData.resourceColors[type] ?: Color.WHITE

        // Get coordinate from the Utils.shuffled list
        // if list is exhausted, reUtils.shuffle it
        val x = vectors[index].x + Math.random().toFloat() * 20f - 10f
        val y = vectors[index].y + Math.random().toFloat() * 20f - 10f
        index++
        if (index > vectors.size - 1) {
            index = 0
            vectors.shuffle()
        }

        val angle = (Math.random() * 180).toFloat()

        shape.translate(x, y + yOffset, angle)

        val entity = EntityFactory.terrain(shape, type, level, color)
        Main.engine.addEntity(entity)
    }

    return (gridHeight + radius * 2)
}

fun generateTerrain360() {
    var percentFilled = 0.13f
    var scale = 1f

    loadShapes()
    var radius = 1100f;
    for (i in 0..20) {

        typeProbabilities.forEach { s, i -> typeProbabilities[s] = 0 }
        when (i) {
            0, 1 -> {
                typeProbabilities["blue"] = 2
                typeProbabilities["sky"] = 2
            }
            2, 3 -> {
                typeProbabilities["neongreen"] = 2
                typeProbabilities["green"] = 2
            }
            4, 5 -> {
                typeProbabilities["brown"] = 2
                typeProbabilities["olive"] = 2
            }
            6, 7 -> {
                typeProbabilities["sky"] = 2
            }
            8, 9 -> {
                typeProbabilities["purple"] = 2
                typeProbabilities["red"] = 2
                typeProbabilities["magenta"] = 1
            }
            10, 11 -> {
                typeProbabilities["magenta"] = 2
                typeProbabilities["orange"] = 2
            }
            12, 13 -> {
                typeProbabilities["slate"] = 2
                typeProbabilities["tan"] = 2
                typeProbabilities["gold"] = 1
            }
            14, 15 -> {
                typeProbabilities["gold"] = 2
            }
            16 -> {
                typeProbabilities["blue"] = 2
                typeProbabilities["gold"] = 1
            }
            17, 18, 19 -> {
                typeProbabilities["slate"] = 2
                typeProbabilities["blue"] = 1
            }
            20 -> {
                typeProbabilities["tan"] = 2
                typeProbabilities["slate"] = 2
                typeProbabilities["blue"] = 1
            }
        }
        val tealChance = (Math.random() * 3).toInt()
        if (tealChance == 0 && i > 1) typeProbabilities["teal"] = 1

        scale += 1 / 4f
        percentFilled += 0.02f

        radius = generateTerrain5(radius, percentFilled, scale)
    }

    radius = 2000f;
    for (i in 0..10) {
        radius = generateGrayRing(radius, 0.3f)
    }
}

fun generateTerrain5(startRadius: Float, percentFilled: Float, scale: Float): Float {
    val amount = 2000

    types.clear()
    typeProbabilities.forEach { s, p ->

        if (p != 0) {
            for (i in 0..(p - 1)) {
                types.add(s)
            }
        }

    }
    if (types.isEmpty()) return 0f
    if (shapes.isEmpty()) return 0f

    val radius = shapes["blue"]!![0].getRadius() * scale
    var outerRadius = 30 * radius + startRadius;
    var gridSpacing = 3f * radius;
    val vectors: MutableList<Vector2> = createRing(outerRadius, startRadius, gridSpacing)

    vectors.shuffle()

    var index = 0
    for (i in 0..amount) {
        // Check if current layer has been filled with n entities
        // where n = amount of points * percent filled
        if (i % ((vectors.size - 1) * percentFilled).toInt() == 0 && i != 0) {
            break;
        }

        // Get a random shape and random terrain type(not gray)
        var k = (Math.random() * types.size).toInt()
        var type = types[k]
        while (type == "gray") {
            k = (Math.random() * types.size).toInt()
            type = types[k]
        }
        k = (Math.random() * shapes[type]!!.size).toInt()
        val shape = shapes[type]!![k].clone()
        shape.scale(scale)
        val level = GameData.resourceLevels[type] ?: 0
        val color = GameData.resourceColors[type] ?: Color.WHITE

        // Get coordinate from the Utils.shuffled list
        // if list is exhausted, reUtils.shuffle it
        val x = vectors[index].x + Math.random().toFloat() * 20f - 10f
        val y = vectors[index].y + Math.random().toFloat() * 20f - 10f
        index++
        if (index > vectors.size - 1) {
            index = 0
            vectors.shuffle()
        }

        val angle = (Math.random() * 180).toFloat()

        shape.translate(x, y, angle)

        val entity = EntityFactory.terrain(shape, type, level, color)
        Main.engine.addEntity(entity)
    }

    return (outerRadius)
}

fun generateGrayRing(startRadius: Float, percentFilled: Float): Float {
    val amount = 2000

    if (shapes.isEmpty()) return 0f

    val radius = shapes["gray"]!![0].getRadius()
    var outerRadius = 30 * radius + startRadius;
    var gridSpacing = 3f * radius;
    val vectors: MutableList<Vector2> = createRing(outerRadius, startRadius, gridSpacing)

    vectors.shuffle()

    var index = 0
    for (i in 0..amount) {
        // Check if current layer has been filled with n entities
        // where n = amount of points * percent filled
        if (i % ((vectors.size - 1) * percentFilled).toInt() == 0 && i != 0) {
            break;
        }

        // Get a random shape
        val k = (Math.random() * shapes["gray"]!!.size).toInt()
        val shape = shapes["gray"]!![k].clone()
        val level = GameData.resourceLevels["gray"] ?: 0
        val color = GameData.resourceColors["gray"] ?: Color.WHITE

        // Get coordinate from the Utils.shuffled list
        // if list is exhausted, reUtils.shuffle it
        val x = vectors[index].x + Math.random().toFloat() * 600f - 300f
        val y = vectors[index].y + Math.random().toFloat() * 300f - 150f
        index++
        if (index > vectors.size - 1) {
            index = 0
            vectors.shuffle()
        }

        val angle = (Math.random() * 180).toFloat()

        shape.translate(x, y, angle)

        val entity = EntityFactory.terrain(shape, "gray", level, color)
        Main.engine.addEntity(entity)
    }

    return (outerRadius)
}


fun loadShapes() {
    val pathFile = Gdx.files.local(AssetPaths.TERRAIN_PATHS)
    val paths = pathFile.toStringList()
    paths.forEach {
        val splitPath = it.split("/")
        val type = splitPath[1]

        val file = Gdx.files.local(it)
        val vertices = file.toVertices()
        if (shapes[type] == null)
            shapes.put(type, arrayListOf<FloatArray>())
        shapes[type]!!.add(vertices)
    }
}

fun createGrid(xOrigin: Float, yOrigin: Float,
               width: Float, height: Float,
               spacing: Float): MutableList<Vector2> {
    val list = arrayListOf<Vector2>()
    var x = xOrigin
    var y = yOrigin
    while (y < yOrigin + height) {
        while (x < xOrigin + width) {
            list.add(Vector2(x, y))
            x += spacing
        }
        y += spacing
        x = xOrigin
    }

    return list
}

fun createRing(outerRadius: Float,
               innerRadius: Float,
               spacing: Float): MutableList<Vector2> {
    val list = arrayListOf<Vector2>()
    var x = -outerRadius / 2
    var y = -outerRadius / 2
    while (y < outerRadius / 2) {
        while (x < outerRadius / 2) {
            list.add(Vector2(x, y))
            x += spacing
            if (x < 0 && x > -innerRadius / 2
                    && Math.abs(y).toFloat() < innerRadius / 2)
                x = innerRadius / 2
        }
        y += spacing
        x = -outerRadius / 2
        if (y < 0 && y > -innerRadius / 2
                && Math.abs(x).toFloat() < innerRadius / 2)
            y = innerRadius / 2
    }

    return list
}

fun generateFromFiles() {
    val pathFile = Gdx.files.local(AssetPaths.TERRAIN_PATHS)
    val paths = pathFile.toStringList()
    paths.forEach {
        val splitPath = it.split("/")
        val type = splitPath[1]
        val color = GameData.resourceColors[type]
        val level = GameData.resourceLevels[type]

        val file = Gdx.files.local(it)
        val vertices = file.toVertices()
        val entity = EntityFactory.terrain(vertices, type, level!!, color!!)
        Main.engine.addEntity(entity)
    }
}