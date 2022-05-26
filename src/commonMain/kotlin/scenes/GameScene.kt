package scenes

import action_ui.HealthBar
import action_ui.addJoystick
import camera.createCamera
import character.Character
import character.initCharMoves
import com.soywiz.klock.Stopwatch
import com.soywiz.korge.component.docking.keepChildrenSortedByY
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.tiled.TiledMap
import com.soywiz.korge.tiled.TiledMapView
import com.soywiz.korge.tiled.readTiledMap
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.addUpdater
import com.soywiz.korge.view.centerXOn
import com.soywiz.korge.view.filter.IdentityFilter
import com.soywiz.korge.view.get
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
import player_data.attachObjectsTo
import player_data.readObjects

lateinit var charactersLayer: Container
lateinit var inventory: Inventory
lateinit var toolBar: ToolBar
lateinit var healthBar: HealthBar

lateinit var tiledMapView: TiledMapView
lateinit var tiledMap: TiledMap

lateinit var characterBitmap: Bitmap
lateinit var inventoryBitmap: Bitmap
lateinit var exit_button: Bitmap
lateinit var wood: Bitmap
lateinit var stone: Bitmap
lateinit var board: Bitmap
lateinit var knife: Bitmap
lateinit var objects: ArrayList<Thing>
lateinit var xml: Xml

class GameScene : Scene() {

    override suspend fun Container.sceneInit() {
        val sw = Stopwatch().start()

        println("start resources loading...")

        exit_button = resourcesVfs["no_icon.png"].readBitmap()
        wood = resourcesVfs["wood.png"].readBitmap()
        stone = resourcesVfs["stone.png"].readBitmap()
        board = resourcesVfs["board.png"].readBitmap()
        knife = resourcesVfs["knife.png"].readBitmap()
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
//        println("${xml.child("objectgroup")?.allNodeChildren?.filter{ it.attributes["name"] == "start" }?.first()?.attributes}")

        val character = Character()

        charactersLayer.addChild(character)

        val camera = createCamera(character, this)

        healthBar = HealthBar(this)

        attachObjectsTo(charactersLayer)

        addJoystick(character)

        inventory = Inventory(this)

        toolBar = ToolBar(inventory, this)

        addUpdater {
            if (needToAttach.isNotEmpty() && isOnline) {
                val anotherCharacter = Character()
                val point = needToAttach.pop()
                println("Add updater: $point")
                playersContainer[point.name] = anotherCharacter
                anotherCharacter.txt.text = point.name
                anotherCharacter.txt.centerXOn(anotherCharacter.sprite)
                anotherCharacter.updateCharacter(point)
                charactersLayer.addChild(anotherCharacter)
            }
            action_ui.move(character)
        }
    }
}