package scenes

import Inventory
import InventoryCell
import Thing
import character.CharMoves
import com.soywiz.klock.milliseconds
import com.soywiz.kmem.clamp
import com.soywiz.korev.Key
import com.soywiz.korev.TouchEvent
import com.soywiz.korge.baseview.BaseView
import com.soywiz.korge.component.TouchComponent
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.tiled.readTiledMap
import com.soywiz.korge.tiled.tiledMapView
import com.soywiz.korge.ui.UI_DEFAULT_WIDTH
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.slice
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.async.launchImmediately
import com.soywiz.korio.async.runBlockingNoJs
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.vector.circle
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.sign

const val inventoryCell = 61.5

var control = true
var startFrame = 0
var dir = 0

lateinit var ball: View

class GameScene() : Scene() {
    override suspend fun Container.sceneInit() {
        init()

        val tileMap = resourcesVfs["map.tmx"].readTiledMap()
        fixedSizeContainer(640, 360, clip = true) {
            val camera = camera {
                tiledMapView(tileMap)
                x -= tileMap.width * tileMap.tilewidth / 2 - width / 2
                y -= tileMap.height * tileMap.tileheight / 2 - height / 2
            }


            //персонаж
            val spriteMap = resourcesVfs["char.png"].readBitmap()
            val person = sprite(spriteMap.slice(RectangleInt(10, 0, 100, 100))) {
                scaledHeight = 32.0
                scaledWidth = 32.0
                centerOn(this@sceneInit)
            }
            var localX = (person.x / 32).toInt()
            var localY = (person.y / 32).toInt()


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

            //виртуальный контроллер
            val tet = text("0/0")
            container {
                alignBottomToBottomOf(this@sceneInit, 60)
                alignLeftToLeftOf(this@sceneInit, 60)
                graphics {
                    fill(Colors.BLACK) { circle(0, 0, 50) }
                    alpha(0.2)
                }
                ball = graphics {
                    fill(Colors.WHITE) { circle(0, 0, 25) }
                    alpha(0.2)
                }
            }
            this.addComponent(object : TouchComponent {
                override val view: BaseView = this@sceneInit

                var dragging = false
                val start = Point(0, 0)

                override fun onTouchEvent(views: Views, e: TouchEvent) {
                    val px = e.activeTouches.firstOrNull()?.x ?: 0.0
                    val py = e.activeTouches.firstOrNull()?.y ?: 0.0
                    tet.text = "$px/$py"
                    tet.centerXOn(camera)

                    when (e.type) {
                        TouchEvent.Type.START -> {
                            when {
                                //fix size
                                  px in views.virtualLeft+10..views.virtualLeft+285 && py in views.virtualBottom-285..views.virtualBottom-10-> {
                                    start.x = px
                                    start.y = py
                                    ball.alpha = 0.3
                                    dragging = true
                                }
                            }
                        }
                        TouchEvent.Type.END -> {
                            ball.position(0, 0)
                            ball.alpha = 0.2
                            dragging = false
                        }
                        TouchEvent.Type.MOVE -> {
                            if (dragging) {
                                val deltaX = px - start.x
                                val deltaY = py - start.y
                                val length = hypot(deltaX, deltaY)
                                val maxLength = 50.0
                                val lengthClamped = length.clamp(0.0, maxLength)
                                val angle = Angle.between(start.x, start.y, px, py)
                                val cx = cos(angle) * lengthClamped
                                val cy = sin(angle) * lengthClamped
                                ball.position(cx, cy)
                                camera.x -= cx * 0.05
                                camera.y -= cy * 0.05

                            }
                        }
                    }
                }
            })


            //FIX THIS
            //кнопка взятия предмета(меча)
            val buttonBitmap = resourcesVfs["button.png"].readBitmap()
            image(buttonBitmap) {
                scale = 0.7
                alignRightToRightOf(this@sceneInit, 20)
                alignBottomToBottomOf(this@sceneInit, 20)
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


            val inv = resourcesVfs["inventory.png"].readBitmap()
            val tools = container {
                val rect = solidRect(705, 95, Colors.LIGHTGREY) {
                    for (i in 0 until 8) {
                        roundRect(75.0, 75.0, 5.0).alignBottomToBottomOf(this, 10).x = this.x + 10 + i * 75
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
                            inventoryContainer.addTo(this@sceneInit)
                        }
                    }
                }
                scale = 0.5
                centerXOn(this@fixedSizeContainer)
                alignBottomToBottomOf(this@fixedSizeContainer)
            }


            person.onFrameChanged {
                person.stopAnimation()
            }


            val txt = text("0/0")


            this.keys.apply {
                down { key ->
                    when (key.key) {
//                        Key.RIGHT -> {
//                            txt.text = "${localX}/${localY}"
//                            if (control && tileMap.tileLayers[1][localX + 1, localY] == 0) {
//                                person.playAnimation(
//                                    CharMoves.RIGHT.animation,
//                                    startFrame = startFrame,
//                                    endFrame = startFrame
//                                )
//                                changeFrame()
//                                dir = 2
//                                camera.x -= 32
//                                localX++
//                            }
//                        }
//                        Key.LEFT -> {
//                            txt.text = "${localX}/${localY}"
//                            if (control && tileMap.tileLayers[1][localX - 1, localY] == 0) {
//                                person.playAnimation(
//                                    CharMoves.LEFT.animation,
//                                    startFrame = startFrame,
//                                    endFrame = startFrame
//                                )
//                                changeFrame()
//                                dir = 3
//                                camera.x += 32
//                                localX--
//                            }
//                        }
//                        Key.DOWN -> {
//                            txt.text = "${localX}/${localY}"
//                            if (control && tileMap.tileLayers[1][localX, localY + 1] == 0) {
//                                person.playAnimation(
//                                    CharMoves.DOWN.animation,
//                                    startFrame = startFrame,
//                                    endFrame = startFrame
//                                )
//                                changeFrame()
//                                dir = 0
//                                camera.y -= 32
//                                localY++
//                            }
//                        }
//                        Key.UP -> {
//                            txt.text = "${localX}/${localY}"
//                            if (control && tileMap.tileLayers[1][localX, localY - 1] == 0) {
//                                person.playAnimation(
//                                    CharMoves.UP.animation,
//                                    startFrame = startFrame,
//                                    endFrame = startFrame
//                                )
//                                changeFrame()
//                                dir = 1
//                                camera.y += 32
//                                localY--
//                            }
//                        }
                        Key.X -> {
                            if (control) {
                                person.playAnimation(CharMoves.SIT.animation, 200.milliseconds, startFrame = dir, endFrame = dir)
                                person.stopAnimation()
                            }
                        }
                        Key.ESCAPE -> {
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
}

suspend fun init() {
    val charSpriteMap = resourcesVfs["char.png"].readBitmap()

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