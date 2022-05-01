package character

import com.soywiz.korge.view.*

class Character(
    val sprite: Sprite
) : FixedSizeContainer(width = 43.5, height = 67.5) {
    var solid: SolidRect
    init {
        sprite.apply {
            scaledWidth = 14.5
            scaledHeight = 22.5
            addTo(this@Character)
            centerOn(this@Character)
        }
        solid = solidRect(sprite.scaledWidth, sprite.scaledHeight).alpha(0.3).centerXOn(this)
        x += 1600 + 320
        y += 1600 + 180
    }
}