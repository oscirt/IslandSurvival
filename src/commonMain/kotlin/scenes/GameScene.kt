package scenes

import Inventory
import InventoryCell
import Thing
import character.CharMoves
import com.soywiz.klock.milliseconds
import com.soywiz.korev.Key
import com.soywiz.korge.input.keys
import com.soywiz.korge.input.onDown
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.UI_DEFAULT_WIDTH
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.bitmap.BitmapSlice
import com.soywiz.korim.bitmap.slice
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.dynamic.KDynamic.Companion.str
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.RectangleInt
import world.TileType
import world.World
import kotlin.math.abs

const val inventoryCell = 100

var control = true
var startFrame = 0
var dir = 0

class GameScene() : Scene() {
    override suspend fun Container.sceneInit() {
        init()

        val ground = Container().xy(0, -8)
        ground.addTo(this)

        //земля
        val world = World(ArrayList())
        world.generateWorld(256, 256)
        world.array.forEach { array ->
            array.forEach { cell ->
                cell.image.addTo(ground)
            }
        }

        val cam = camera {}


        //персонаж
        val spriteMap = resourcesVfs["char.png"].readBitmap()
        val person = Sprite(spriteMap.slice(RectangleInt(10, 0, 100, 100))).centerOn(this@sceneInit)
        person.addTo(cam)
        println("${person.x}|${person.y}||${person.width}|${person.height}")

//        addUpdater {
//            cam.x += 1
//            cam.y += 1
//        }
//
//
        //инвентарь
        val inventoryBackground = RoundRect(980.0, 500.0, 5.0).centerOn(this)
        inventoryBackground.color = RGBA(198, 198, 198)
        val inventoryContainer = Container()
        inventoryContainer.addChild(
            roundRect(this.width, this.height, 0.0) {
                color = RGBA(0x00, 0x00, 0x00, 0x88)
            }
        )
        inventoryContainer.addChild(inventoryBackground)


        //создание ячеек инвентаря
        val inventory = Inventory(arrayListOf(), 4, 8)
        for (i in 0 until inventory.rows) {
            inventory.array.add(arrayListOf())
            for (j in 0 until inventory.cols) {
                inventory.array[i].add(InventoryCell(cell = roundRect(100.0, 100.0, 20.0) {
                    color = RGBA(139, 139, 139, 255)
                    x += (j * (inventoryCell + 20)) + inventoryBackground.x + 20
                    y += (i * (inventoryCell + 20)) + inventoryBackground.y + 20
                }))
                inventoryContainer.addChild(inventory.array[i][j].cell)
            }
        }


        //мечики
        val sword = Thing(
            Image(resourcesVfs["sword.png"].readBitmap())
                .position(views.virtualWidth / 2 + 100, views.virtualHeight / 2 + 100)
        )
        ground.addChild(sword.img)
        val sword2 = Thing(
            Image(resourcesVfs["sword.png"].readBitmap())
                .position(views.virtualWidth / 2 - 100, views.virtualHeight / 2 - 100)
        )
        ground.addChild(sword2.img)


        //сидеть
        val buttonBitmap = resourcesVfs["button.png"].readBitmap()
        image(buttonBitmap) {
            position(
                views.virtualWidth - buttonBitmap.width * 1.5,
                views.virtualHeight - buttonBitmap.height * 1.5
            )
            onDown {
                person.playAnimation(CharMoves.SIT.animation, 200.milliseconds, startFrame = dir, endFrame = dir)
                person.stopAnimation()
            }
        }


        //геймпад
        val joystickBitmap = resourcesVfs["Controls.png"].readBitmap()
        image(joystickBitmap) {
            position(0.0, views.virtualHeightDouble - joystickBitmap.height + 1)
            onDown {
                val point = it.currentPosLocal
                when {
                    point.x in 54.0..108.0 && point.y in 0.0..54.0 -> {
                        person.y -= 32
                        cam.y += 32
                    }
                    point.x in 0.0..54.0 && point.y in 54.0..108.0 -> {
                        person.x -= 32
                        cam.x += 32
                    }
                    point.x in 54.0..108.0 && point.y in 108.0..162.0 -> {
                        person.y += 32
                        cam.y -= 32
                    }
                    point.x in 108.0..162.0 && point.y in 54.0..108.0 -> {
                        person.x += 32
                        cam.x -= 32
                    }
                }
            }
        }


        //кнопка взятия предмета(меча, который на самом деле КИРКА)
        image(buttonBitmap) {
            position(
                views.virtualWidth - buttonBitmap.width * 1.5,
                views.virtualHeight - buttonBitmap.height * 1.5 - 100
            )
            onDown {
                //не нравится куча выражений
                //жестко надо переработать
                if ((abs(sword.img.x - person.x) < 100) && (abs(sword.img.y - person.y) < 100)) {
                    ground.removeChild(sword.img)
                    val index = inventory.getFreeCellIndex()
                    val i = index / 4
                    val j = index % 8
                    if (index != -1) {
                        inventory.array[i][j].thing = sword
                        inventoryContainer.addChild(
                            inventory.array[i][j].thing!!.img.xy(
                                (j * (inventoryCell + 20)) + inventoryBackground.x + 54,
                                (i * (inventoryCell + 20)) + inventoryBackground.y + 54
                            )
                        )
                    }
                }
                if ((abs(sword2.img.x - person.x) < 100) && (abs(sword2.img.y - person.y) < 100)) {
                    ground.removeChild(sword2.img)
                    val index = inventory.getFreeCellIndex()
                    val i = index / 4
                    val j = index % 8
                    if (index != -1) {
                        inventory.array[i][j].thing = sword2
                        inventoryContainer.addChild(
                            inventory.array[i][j].thing!!.img.xy(
                                (j * (inventoryCell + 20)) + inventoryBackground.x + 54,
                                (i * (inventoryCell + 20)) + inventoryBackground.y + 54
                            )
                        )
                    }
                }
            }
        }


        //кнопка инвентаря
        uiButton(
            text = "Inventory"
        ) {
            position(views.virtualWidth - UI_DEFAULT_WIDTH, 0.0)
            onDown {
                control = false
                inventoryContainer.x = -cam.x
                inventoryContainer.y = -cam.y
                cam.addChild(inventoryContainer)
            }
        }


        //чето связанное с анимацией хз че это но работает не трогай пж
        person.onFrameChanged {
            person.stopAnimation()
        }


        //привязка ко кнопкам клавиатуры
        keys {
            down(Key.DOWN) {
                if (control) {
                    println(if (getTile(world, person) == TileType.GROUND) 1 else 2)
                    person.playAnimation(
                        CharMoves.DOWN.animation,
                        startFrame = startFrame,
                        endFrame = startFrame
                    )
                    changeFrame()
                    dir = 0
                    cam.y -= 32
                    person.y += 32
                }
            }
            down(Key.UP) {
                if (control) {
                    println(if (getTile(world, person) == TileType.GROUND) 1 else 2)
                    person.playAnimation(
                        CharMoves.UP.animation,
                        startFrame = startFrame,
                        endFrame = startFrame
                    )
                    changeFrame()
                    dir = 1
                    cam.y += 32
                    person.y -= 32
                }
            }
            down(Key.LEFT) {
                if (control) {
                    println(if (getTile(world, person) == TileType.GROUND) 1 else 2)
                    person.playAnimation(
                        CharMoves.LEFT.animation,
                        startFrame = startFrame,
                        endFrame = startFrame
                    )
                    changeFrame()
                    dir = 3
                    cam.x += 32
                    person.x -= 32
                }
            }
            down(Key.RIGHT) {
                if (control) {
                    println(if (getTile(world, person) == TileType.GROUND) 1 else 2)
                    person.playAnimation(
                        CharMoves.RIGHT.animation,
                        startFrame = startFrame,
                        endFrame = startFrame
                    )
                    changeFrame()
                    dir = 2
                    cam.x -= 32
                    person.x += 32
                }
            }
            down(Key.X) {
                if (control) {
                    person.playAnimation(CharMoves.SIT.animation, 200.milliseconds, startFrame = dir, endFrame = dir)
                    person.stopAnimation()
                }
            }
            down(Key.ESCAPE) {
                if (!control) {
                    inventoryContainer.removeFromParent()
                    control = true
                }
            }
        }
    }
}

suspend fun init() {
    val bitmap = resourcesVfs["tilemap.png"].readBitmap()
    val ground = resourcesVfs["grass.png"].readBitmap()
    val water  = resourcesVfs["waterTextureMinimised.png"].readBitmap()

    val charSpriteMap = resourcesVfs["char.png"].readBitmap()

    val groundSlice: BitmapSlice<Bitmap> = ground.slice(RectangleInt(0 , 0, 32, 32))
    val waterSlice : BitmapSlice<Bitmap> = water.slice(RectangleInt(0, 0, 32, 32))

    TileType.GROUND.slice = groundSlice
    TileType.WATER.slice  = waterSlice

    TileType.WATER.isWall = true

    CharMoves.DOWN.animation = SpriteAnimation(
        spriteMap = charSpriteMap,
        spriteWidth = 100,
        spriteHeight = 100,
        marginLeft = 110,
        columns = 6,
        rows = 1
    )

    CharMoves.UP.animation = SpriteAnimation(
        spriteMap = charSpriteMap,
        spriteWidth = 100,
        spriteHeight = 100,
        marginTop = 100,
        marginLeft = 110,
        columns = 6,
        rows = 1
    )

    CharMoves.RIGHT.animation = SpriteAnimation(
        spriteMap = charSpriteMap,
        spriteWidth = 100,
        spriteHeight = 100,
        marginTop = 200,
        marginLeft = 110,
        columns = 6,
        rows = 1
    )

    CharMoves.LEFT.animation = SpriteAnimation(
        spriteMap = charSpriteMap,
        spriteWidth = 100,
        spriteHeight = 100,
        marginTop = 300,
        marginLeft = 110,
        columns = 6,
        rows = 1
    )

    CharMoves.SIT.animation = SpriteAnimation(
        spriteMap = charSpriteMap,
        spriteWidth = 100,
        spriteHeight = 100,
        marginLeft = 710,
        columns = 1,
        rows = 4
    )
}

fun changeFrame() {
    startFrame++
    if (startFrame >= 6) {
        startFrame = 0
    }
}

fun getTile(world: World, person: Sprite) : TileType {
    return world.array[(person.y / 32).toInt()][(person.x / 32).toInt()].tileType
}