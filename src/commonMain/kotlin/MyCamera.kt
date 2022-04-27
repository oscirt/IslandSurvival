import com.soywiz.korge.tiled.TiledMap
import com.soywiz.korge.tiled.TiledMapView
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.addTo

lateinit var tiledMapView: TiledMapView

class MyCamera(
    tileMap: TiledMap,
    scene: Container
) : Container() {
    init {
        tiledMapView = TiledMapView(tileMap)
        addChild(tiledMapView)
//        x -= tileMap.width * tileMap.tilewidth / 2
//        y -= tileMap.height * tileMap.tileheight / 2
        addTo(scene)
    }
}