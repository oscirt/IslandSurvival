package scenes

import Inventory
import InventoryCell
import Thing
import character.CharMoves
import character.Character
import com.soywiz.klock.milliseconds
import com.soywiz.kmem.clamp
import com.soywiz.korev.Key
import com.soywiz.korge.input.keys
import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onDown
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.tiled.TiledMap
import com.soywiz.korge.tiled.TiledMapView
import com.soywiz.korge.tiled.readTiledMap
import com.soywiz.korge.tiled.tiledMapView
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.slice
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.dynamic.KDynamic.Companion.map
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.RectangleInt
import movement.addJoystick
import movement.isRight
import kotlin.math.abs
import kotlin.math.pow

const val inventoryCell = 61.5
const val tileMapName = "mapWithFreshTileSet.tmx"

class GameScene : Scene() {
    lateinit var tileMap: TiledMap
    var control = true

    override suspend fun Container.sceneInit() {
        val charSpriteMap = resourcesVfs["person.png"].readBitmap()

        CharMoves.RIGHT.animation = SpriteAnimation(
            spriteMap = charSpriteMap,
            spriteWidth = 64,
            spriteHeight = 64,
            marginLeft = 16,
            marginTop = 78,
            columns = 9,
            rows = 1
        )

        CharMoves.LEFT.animation = SpriteAnimation(
            spriteMap = charSpriteMap,
            spriteWidth = 64,
            spriteHeight = 64,
            marginLeft = 16,
            marginTop = 208,
            columns = 9,
            rows = 1
        )

        tileMap = resourcesVfs[tileMapName].readTiledMap()
    }

    override suspend fun Container.sceneMain() {
        val scene = this
        fixedSizeContainer(640, 360, clip = true) {
            val camera = camera {
                tiledMapView(tileMap)
                x -= tileMap.width * tileMap.tilewidth / 2
                y -= tileMap.height * tileMap.tileheight / 2
            }


            //персонаж
            val spriteMap = resourcesVfs["person.png"].readBitmap().slice(RectangleInt(16, 14, 64, 64))
            val person = Character(Sprite(spriteMap), scene, camera)


            //инвентарь
            val inventoryBackground = RoundRect(this.width * 0.8, this.height * 0.8, 5.0).centerOn(camera)
            inventoryBackground.color = RGBA(198, 198, 198)
            val inventoryContainer = Container()
            inventoryContainer.addChild(
                roundRect(camera.width, camera.height, 0.0) {
                    color = RGBA(0x00, 0x00, 0x00, 0x88)
                }
            )
            inventoryContainer.addChild(inventoryBackground)


            //создание ячеек инвентаря
            val inventory = Inventory(arrayListOf(), 4, 8)
            for (i in 0 until inventory.rows) {
                inventory.inventory.add(arrayListOf())
                for (j in 0 until inventory.cols) {
                    inventory.inventory[i].add(InventoryCell())
                    inventoryContainer.addChild(roundRect(inventoryCell, inventoryCell, 15.0) {
                        color = RGBA(139, 139, 139, 255)
                        x += (j * inventoryCell) + inventoryBackground.x + 10
                        y += (i * inventoryCell) + inventoryBackground.y + 10
                    })
                }
            }


            //tools
            val inv = resourcesVfs["inventory.png"].readBitmap()
            val tools = container {
                val rect = solidRect(705, 95, Colors.LIGHTGREY) {
                    for (i in 0 until 8) {
                        inventory.tools.add(InventoryCell())
                        inventory.tools[i].rect = roundRect(75.0, 75.0, 5.0){
                            alignBottomToBottomOf(this@solidRect, 10)
                            x = this@solidRect.x + 10 + i * 75
                            onDown {
                                inventory.tools[inventory.selected].rect.alpha = 1.0
                                alpha = 0.5
                                inventory.selected = i
                            }
                        }
                    }
                }
                roundRect(75.0, 75.0, 5.0) {
                    alignTopToTopOf(this, 10)
                    alignRightToRightOf(rect, 10)
                    image(inv) {
                        scale = 0.8
                        centerOn(this@roundRect)
                        onClick {
                            control = false
                            inventoryContainer.addTo(this@sceneMain)
                        }
                    }
                }
                scale = 0.5
                centerXOn(this@fixedSizeContainer)
                alignBottomToBottomOf(this@fixedSizeContainer)
            }


            //мечики
            val sword = Thing(
                Image(resourcesVfs["sword.png"].readBitmap())
                    .position(tileMap.width * tileMap.tilewidth / 2 + 100, tileMap.height * tileMap.tileheight / 2 + 100)
            )
            sword.img.addTo(camera)
            val sword2 = Thing(
                Image(resourcesVfs["sword.png"].readBitmap())
                    .position(tileMap.width * tileMap.tilewidth / 2 - 100, tileMap.height * tileMap.tileheight / 2 - 100)
            )
            sword2.img.addTo(camera)


            // joystick
            var dx = 0.0
            var dy = 0.0
            fun move(x: Double, y: Double, view: View) {
                view.addUpdater {
                    dx = x * 2.0 * (-1)
                    dy = y * 2.0 * (-1)
                }
            }
            var joystick = addJoystick(person.sprite) {
                    x, y -> move(x, y, camera)
            }
            addUpdater {
                val scale = if (it == 0.0.milliseconds) 0.0 else (it / 16.666666.milliseconds)
                dx = dx.clamp(-10.0, +10.0)
                dy = dy.clamp(-10.0, +10.0)
//                when {
//                    tileMap.tileLayers[1][person.localX + 1, person.localY] != 0 && isRight -> {camera.x -= (dx * scale) * speed}
//                    tileMap.tileLayers[1][person.localX - 1, person.localY] != 0 -> {}
//                    tileMap.tileLayers[1][person.localX, person.localY + 1] != 0 -> {}
//                    tileMap.tileLayers[1][person.localX, person.localY - 1] != 0 -> {}
//                }

                camera.x += (dx * scale) * speed
                camera.y += (dy * scale) * speed
                dx *= 0.9.pow(scale)
                dy *= 0.9.pow(scale)
            }


            //FIX THIS
            //кнопка взятия предмета(меча)
            val buttonBitmap = resourcesVfs["button.png"].readBitmap()
            image(buttonBitmap) {
                scale = 0.7
                alignRightToRightOf(this@sceneMain, 20)
                alignBottomToBottomOf(this@sceneMain, 20)
                onDown {
                    //не нравится куча выражений
                    //жестко надо переработать
                    if (control) {
                        if ((abs(sword.img.globalX - person.x) < 100) && (abs(sword.img.globalY - person.y) < 100)) {
                            sword.img.removeFromParent()
                            val index = inventory.getFreeCellIndex()
                            val i = index / 4
                            val j = index % 8
                            if (index != -1) {
                                inventory.inventory[i][j].thing = sword
                                inventoryContainer.addChild(
                                    inventory.inventory[i][j].thing!!.img.xy(
                                        (j * inventoryCell) + inventoryBackground.x + 24.25,
                                        (i * inventoryCell) + inventoryBackground.y + 25.25
                                    )
                                )
                            }
                        }
                        if ((abs(sword2.img.globalX - person.x) < 100) && (abs(sword2.img.globalY - person.y) < 100)) {
                            sword2.img.removeFromParent()
                            val index = inventory.getFreeCellIndex()
                            val i = index / 4
                            val j = index % 8
                            if (index != -1) {
                                inventory.inventory[i][j].thing = sword2
                                inventoryContainer.addChild(
                                    inventory.inventory[i][j].thing!!.img.xy(
                                        (j * inventoryCell) + inventoryBackground.x + 24.25,
                                        (i * inventoryCell) + inventoryBackground.y + 25.25
                                    )
                                )
                            }
                        }
                    }
                }
            }


            this.keys.apply {
                down { key ->
                    if (key.key == Key.ESCAPE) {
                        if (!control) {
                            inventoryContainer.removeFromParent()
                            control = true
                        }
                    }
                }
            }
        }
    }
}