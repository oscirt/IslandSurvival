package character

import com.soywiz.korge.view.*
import com.soywiz.korge.view.animation.ImageDataView

class Character(
    val sprite: Sprite,
    scene: Container
) : ImageDataView() {
    val localX: Int get() = (x / 32).toInt()
    val localY: Int get() = (y / 32).toInt()

    init {
        sprite.apply {
            scaledHeight = 32.0
            scaledWidth = 32.0
            addTo(this@Character)
        }
        println("${scene.x}/${scene.y}")
        x += (scene.x - 320) * -1
        y += (scene.y - 180) * -1
        scene.addChild(this)
    }
}