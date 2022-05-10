package character

import com.soywiz.korge.view.*
import scenes.characterBitmap
import player_data.startPosition

class Character : FixedSizeContainer(width = 43.5, height = 67.5) {
    var sprite = Sprite(characterBitmap).apply {
        scaledWidth = 14.5
        scaledHeight = 22.5
        addTo(this@Character)
        centerOn(this@Character)
    }
    var solid = solidRect(sprite.scaledWidth, sprite.scaledHeight)
        .alpha(0.3)
        .centerXOn(this)
        .addTo(this)
    init {
        xy(startPosition)
    }
}