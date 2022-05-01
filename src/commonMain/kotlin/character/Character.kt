package character

import com.soywiz.korge.view.*

class Character(
    val sprite: Sprite
) : FixedSizeContainer() {
    init {
        sprite.apply {
            scaledHeight = 22.5
            scaledWidth = 14.5
            addTo(this@Character)
            centerOn(this@Character)
        }
        x += 1600 + 320
        y += 1600 + 180
        solidRect(width, height).alpha(0.3)
    }
}