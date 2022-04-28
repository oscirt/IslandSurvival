package character

import com.soywiz.korge.view.*
import scenes.objects

class Character(
    val sprite: Sprite,
    scene: Container
) : FixedSizeContainer() {
    val localX: Int get() = (x / 32).toInt()
    val localY: Int get() = (y / 32).toInt()

    init {
        sprite.apply {
            scaledHeight = 32.0
            scaledWidth = 32.0
            addTo(this@Character)
            centerOn(this@Character)
        }
        x += 1600 + 320
        y += 1600 + 180
        scene.addChild(this)
        solidRect(width, height).alpha(0.3)
        addUpdater {
            println("$x/$y\t${sprite.x}/${sprite.y}")
//            if (collidesWith(objects[0].type.img)) println("$x/$y\n${objects[0].type.img.x}/${objects[0].type.img.y}")
        }
    }
}