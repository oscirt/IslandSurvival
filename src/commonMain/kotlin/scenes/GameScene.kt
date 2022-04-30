package scenes

import character.Character
import character.initCharMoves
import com.soywiz.klock.Stopwatch
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.tiled.TiledMap
import com.soywiz.korge.tiled.TiledMapView
import com.soywiz.korge.tiled.readTiledMap
import com.soywiz.korge.view.*
import com.soywiz.korge.view.camera.cameraContainer
import com.soywiz.korge.view.filter.IdentityFilter
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.bitmap.slice
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.RectangleInt
import inventory.Inventory
import inventory.Thing
import inventory.ToolBar
import inventory.TypeThing
import movement.addJoystick

lateinit var tiledMapView: TiledMapView
lateinit var tileMap: TiledMap
lateinit var personSprite: Sprite
lateinit var inventorySprite: Bitmap
lateinit var character: Character
lateinit var objects: ArrayList<Thing>

class GameScene : Scene() {

    override suspend fun Container.sceneInit() {
        val sw = Stopwatch().start()

        println("start resources loading...")

        initCharMoves(resourcesVfs["person.png"].readBitmap())
        tileMap = resourcesVfs["Island.tmx"].readTiledMap()
        personSprite = Sprite(resourcesVfs["person.png"]
            .readBitmap()
            .slice(RectangleInt(17, 15, 29, 45))
        )
        inventorySprite = resourcesVfs["inventory.png"].readBitmap()

        tiledMapView = TiledMapView(tileMap, smoothing = false, showShapes = false)
        tiledMapView.filter = IdentityFilter(false)

        objects = arrayListOf()

        println("loaded resources in ${sw.elapsed}")
    }

    override suspend fun Container.sceneMain() {
        val camera = cameraContainer(640.0, 360.0, clip = true, block = { clampToBounds = true }) {
            addChild(tiledMapView)

            val thing = Thing(TypeThing.NPC)
            addChild(thing.type.img)
            thing.type.img.x = 1600.0
            thing.type.img.y = 1600.0
            objects.add(thing)
            //thing.type.img.width, thing.type.img.height
            solidRect(thing.type.img.width, thing.type.img.height).xy(thing.type.img.x, thing.type.img.y).alpha(0.3)

            character = Character(personSprite, this)

//            val newObj =  TiledMap.Object(1, 1, "chest", "npc",
//                Rectangle(2100, 1100, 0, 0), rotation = 0.0, visible = true)
//            tileMap.objectLayers[0].objects.add(newObj)
//            for (obj in tileMap.data.getObjectByType("npc")) {
//                objects.add(obj)
//                println(obj)
//                image(inventorySprite) {
//                    xy(obj.x, obj.y)
//                }
//            }
        }

        //инвентарь
        val inventoryContainer = Inventory(this)

        //тулбар
        val toolBar = ToolBar(inventoryContainer, this, inventorySprite)

        // joystick
        addJoystick(character.sprite)
        addUpdater {
            movement.move(character)
        }

        camera.cameraViewportBounds.copyFrom(tiledMapView.getLocalBoundsOptimized())
        camera.follow(character.sprite, setImmediately = true)
    }
}