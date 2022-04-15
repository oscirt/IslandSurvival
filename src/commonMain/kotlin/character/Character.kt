package character

import com.soywiz.korge.view.Sprite

class Character(
    val sprite: Sprite,
) {
    val x: Double get() = sprite.x
    val y: Double get() = sprite.y
    val localX: Int get() = (sprite.x / 32).toInt()
    val localY: Int get() = (sprite.y / 32).toInt()
}