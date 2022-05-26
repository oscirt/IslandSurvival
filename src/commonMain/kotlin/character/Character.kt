package character

import com.soywiz.klock.milliseconds
import com.soywiz.korge.view.*
import model.Point
import scenes.characterBitmap
import player_data.startPosition
import scenes.characterName
import scenes.playersContainer

class Character : FixedSizeContainer(width = 43.5, height = 67.5) {
    val sprite = Sprite(characterBitmap).apply {
        scaledWidth = 14.5
        scaledHeight = 22.5
        addTo(this@Character)
        centerOn(this@Character)
    }
    val solid = solidRect(sprite.scaledWidth, sprite.scaledHeight)
        .alpha(0)
        .centerXOn(this)
        .addTo(this)
    val txt = text(characterName).apply {
        alignTopToTopOf(sprite, -14.5)
        centerXOn(sprite)
    }

    fun updateCharacter(point: Point) {
        x = point.x
        y = point.y
        if (point.direction == 4) {
            sprite.stopAnimation()
        } else {
            sprite.playAnimationLooped(
                chooseAnimation(point.direction),
                100.milliseconds
            )
        }
    }

    var moveDirection = CharMoves.DOWN
    
    init {
        xy(startPosition)
    }
}