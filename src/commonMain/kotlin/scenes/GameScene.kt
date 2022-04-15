package scenes

import Inventory
import InventoryCell
import Thing
import character.CharMoves
import character.Character
import com.soywiz.klock.milliseconds
import com.soywiz.kmem.clamp
import com.soywiz.korev.Key
import com.soywiz.korev.TouchEvent
import com.soywiz.korge.baseview.BaseView
import com.soywiz.korge.component.TouchComponent
import com.soywiz.korge.input.keys
import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onDown
import com.soywiz.korge.input.onUp
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.tiled.readTiledMap
import com.soywiz.korge.tiled.tiledMapView
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.slice
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.vector.circle
import kotlinx.coroutines.*
import kotlin.math.abs
import kotlin.math.hypot

const val inventoryCell = 61.5

var control = true
var startFrame = 0

lateinit var ball: View

class GameScene : Scene() {
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
            val spriteMap = resourcesVfs["person.png"].readBitmap()
            val person = Character(sprite(spriteMap.slice(RectangleInt(16, 14, 64, 64))) {
                scaledHeight = 32.0
                scaledWidth = 32.0
                centerOn(this@sceneInit)
            })


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
                        roundRect(75.0, 75.0, 5.0){
                            alignBottomToBottomOf(this@solidRect, 10)
                            x = this@solidRect.x + 10 + i * 75
                            onDown {
                                alpha = 0.5
                            }
                            onUp {
                                alpha = 1.0
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
                            inventoryContainer.addTo(this@sceneInit)
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

            //виртуальный контроллер
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


            //костыль
            this.addComponent(object : TouchComponent {
                override val view: BaseView = this@sceneInit

                var dragging = false
                val start = Point(0, 0)
                var jobs = arrayListOf<Job>()

                override fun onTouchEvent(views: Views, e: TouchEvent) {
                    val px = e.activeTouches.firstOrNull()?.x ?: 0.0
                    val py = e.activeTouches.firstOrNull()?.y ?: 0.0

                    println(e)

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
//                            println("start")
                        }
                        TouchEvent.Type.END -> {
                            ball.position(0, 0)
                            ball.alpha = 0.2
                            dragging = false
                            person.sprite.stopAnimation()
//                            println("end")
                        }
                        TouchEvent.Type.MOVE -> {
                            //частое создание корутин не оч хорошо сказывается на производительности
                            if (dragging) {
                                if (jobs.firstOrNull() != null) {
                                    jobs.first().cancel()
                                    jobs.remove(jobs.first())
                                }
                                val deltaX = px - start.x
                                val deltaY = py - start.y
                                val length = hypot(deltaX, deltaY)
                                val lengthClamped = length.clamp(0.0, 50.0)
                                val angle = Angle.between(start.x, start.y, px, py)
                                val cx = cos(angle) * lengthClamped
                                val cy = sin(angle) * lengthClamped
                                ball.position(cx, cy)
                                jobs.add(launch {
                                    while (dragging) {
                                        camera.x -= cx * 0.05
                                        camera.y -= cy * 0.05
                                        delay(1L)
                                    }
                                })
                                if (cos(angle) < 0) {
                                    person.sprite.playAnimationLooped(
                                        CharMoves.RIGHT.animation,
                                        150.milliseconds
                                    )
                                } else {
                                    person.sprite.playAnimation(
                                        CharMoves.LEFT.animation,
                                        150.milliseconds
                                    )
                                }
                            }
                            println("move")
                        }
                        TouchEvent.Type.HOVER -> {
                            println("Hello world!")
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

suspend fun init() {
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
}