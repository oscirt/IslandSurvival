package scenes

import action_ui.addJoystick
import player_data.attachObjectsTo
import camera.createCamera
import character.Character
import character.initCharMoves
import com.soywiz.klock.Stopwatch
import com.soywiz.korge.component.docking.keepChildrenSortedByY
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.tiled.TiledMap
import com.soywiz.korge.tiled.TiledMapView
import com.soywiz.korge.tiled.readTiledMap
import com.soywiz.korge.view.*
import com.soywiz.korge.view.filter.IdentityFilter
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.bitmap.slice
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korio.serialization.xml.Xml
import com.soywiz.korio.serialization.xml.readXml
import com.soywiz.korma.geom.RectangleInt
import inventory.Inventory
import inventory.Thing
import inventory.ToolBar
import player_data.readObjects

lateinit var tiledMapView: TiledMapView
lateinit var tiledMap: TiledMap
lateinit var characterBitmap: Bitmap
lateinit var inventoryBitmap: Bitmap
lateinit var objects: ArrayList<Thing>
lateinit var xml: Xml
lateinit var charactersLayer: Container
lateinit var inventory: Inventory
lateinit var toolBar: ToolBar

class GameScene : Scene() {

    override suspend fun Container.sceneInit() {
        val sw = Stopwatch().start()

        println("start resources loading...")

        initCharMoves(resourcesVfs["person.png"].readBitmap())
        tiledMap = resourcesVfs["Island.tmx"].readTiledMap()
        characterBitmap = resourcesVfs["person.png"]
            .readBitmap()
            .slice(RectangleInt(17, 15, 29, 45)).extract()
        inventoryBitmap = resourcesVfs["inventory.png"].readBitmap()

        tiledMapView = TiledMapView(tiledMap, smoothing = false, showShapes = false)
        tiledMapView.filter = IdentityFilter(false)

        objects = arrayListOf()
        readObjects()

        charactersLayer = tiledMapView["characters"].first as Container
        charactersLayer.keepChildrenSortedByY()

        xml = resourcesVfs["Island.tmx"].readXml()

        println("loaded resources in ${sw.elapsed}")
    }

    override suspend fun Container.sceneMain() {
        keepChildrenSortedByY()
//        println("${xml.child("objectgroup")?.allNodeChildren?.filter{ it.attributes["name"] == "start" }?.first()?.attributes}")

        val character = Character()

        charactersLayer.addChild(character)

        val camera = createCamera(character, this)

        attachObjectsTo(charactersLayer)

        inventory = Inventory(this)

        toolBar = ToolBar(inventory, this)

        addJoystick(character)

        addUpdater {
            action_ui.move(character)
        }
    }
}