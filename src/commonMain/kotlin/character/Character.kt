package character

import com.soywiz.korge.view.*

class Character(
    val sprite: Sprite,
    val scene: Container,
    val camera: Camera
) {
    val x: Double get() = (camera.x - camera.width / 2) * -1
    val y: Double get() = (camera.y - camera.height / 2) * -1
    val localX: Int get() = (x / 32).toInt()
    val localY: Int get() = (y / 32).toInt()

    init {
        sprite.apply {
            scaledHeight = 32.0
            scaledWidth = 32.0
            addTo(scene)
            centerOn(scene)
        }
    }
}