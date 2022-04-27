package scenes

import inventory.Inventory
import MyCamera
import inventory.ToolBar
import character.Character
import character.initCharMoves
import com.soywiz.klock.Stopwatch
import com.soywiz.korev.Key
import com.soywiz.korge.input.keys
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.tiled.TiledMap
import com.soywiz.korge.tiled.readTiledMap
import com.soywiz.korge.view.*
import com.soywiz.korge.view.camera.cameraContainer
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.bitmap.slice
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.RectangleInt
import inventory.control
import movement.addJoystick
import tiledMapView

class GameScene : Scene() {
    private lateinit var tileMap: TiledMap
    private lateinit var personSprite: Sprite
    private lateinit var inventorySprite: Bitmap
    private lateinit var character: Character

    override suspend fun Container.sceneInit() {
        val sw = Stopwatch().start()

        println("start resources loading...")

        initCharMoves(resourcesVfs["person.png"].readBitmap())
        tileMap = resourcesVfs["Island.tmx"].readTiledMap()
        personSprite = Sprite(resourcesVfs["person.png"]
            .readBitmap()
            .slice(RectangleInt(16, 14, 64, 64))
        )
        inventorySprite = resourcesVfs["inventory.png"].readBitmap()

        println("loaded resources in ${sw.elapsed}")
    }

    override suspend fun Container.sceneMain() {
        var camera = cameraContainer(640.0, 360.0, clip = true, block = { clampToBounds = true }) {
            //камера
            val camera = MyCamera(tileMap, this)

            //персонаж
            character = Character(personSprite, this)

            //инвентарь
            val inventoryContainer = Inventory(this)

            //тулбар
            val toolBar = ToolBar(inventoryContainer, this, inventorySprite)

            // joystick
            addJoystick(character.sprite)
            addUpdater {
//                character.moveWithHitTestable(tiledMapView, 1.0, 1.0)
                movement.move(character)
//                movement.move(this)
//                this.moveWithHitTestable(tiledMapView, -1.0, -1.0)
            }

//            //мечики
//            val sword = Thing(
//                Image(resourcesVfs["sword.png"].readBitmap())
//                    .position(tileMap.width * tileMap.tilewidth / 2 + 100, tileMap.height * tileMap.tileheight / 2 + 100)
//            )
//            sword.img.addTo(camera)
//            val sword2 = Thing(
//                Image(resourcesVfs["sword.png"].readBitmap())
//                    .position(tileMap.width * tileMap.tilewidth / 2 - 100, tileMap.height * tileMap.tileheight / 2 - 100)
//            )
//            sword2.img.addTo(camera)


//            //FIX THIS
//            //кнопка взятия предмета(меча)
//            val buttonBitmap = resourcesVfs["button.png"].readBitmap()
//            image(buttonBitmap) {
//                scale = 0.7
//                alignRightToRightOf(this@sceneMain, 20)
//                alignBottomToBottomOf(this@sceneMain, 20)
//                onDown {
//                    //не нравится куча выражений
//                    //жестко надо переработать
//                    if (inventory.getControl) {
//                        if ((abs(sword.img.globalX - person.x) < 100) && (abs(sword.img.globalY - person.y) < 100)) {
//                            sword.img.removeFromParent()
//                            val index = inventory.getFreeCellIndex()
//                            val i = index / 4
//                            val j = index % 8
//                            if (index != -1) {
//                                inventory.inventory[i][j].thing = sword
//                                inventoryContainer.addChild(
//                                    inventory.inventory[i][j].thing!!.img.xy(
//                                        (j * inventory.inventoryCell) + inventoryBackground.x + 24.25,
//                                        (i * inventory.inventoryCell) + inventoryBackground.y + 25.25
//                                    )
//                                )
//                            }
//                        }
//                        if ((abs(sword2.img.globalX - person.x) < 100) && (abs(sword2.img.globalY - person.y) < 100)) {
//                            sword2.img.removeFromParent()
//                            val index = inventory.getFreeCellIndex()
//                            val i = index / 4
//                            val j = index % 8
//                            if (index != -1) {
//                                inventory.inventory[i][j].thing = sword2
//                                inventoryContainer.addChild(
//                                    inventory.inventory[i][j].thing!!.img.xy(
//                                        (j * inventory.inventoryCell) + inventoryBackground.x + 24.25,
//                                        (i * inventory.inventoryCell) + inventoryBackground.y + 25.25
//                                    )
//                                )
//                            }
//                        }
//                    }
//                }
//            }


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
        camera.cameraViewportBounds.copyFrom(tiledMapView.getLocalBoundsOptimized())

        camera.follow(character, setImmediately = true)
    }
}