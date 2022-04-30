package character

import com.soywiz.kds.iterators.fastForEach
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
            scaledHeight = 22.5
            scaledWidth = 14.5
            addTo(this@Character)
            centerOn(this@Character)
        }
        x += 1600 + 320
        y += 1600 + 180
        scene.addChild(this)
        solidRect(width, height).alpha(0.3)
        addUpdater {
            if (objects[0].type.img.collidesWith(this)) println("Hello")
        }
    }
}