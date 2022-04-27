package character

import com.soywiz.korge.view.SpriteAnimation
import com.soywiz.korim.bitmap.Bitmap

enum class CharMoves {
    LEFT,
    RIGHT;

    lateinit var animation: SpriteAnimation
}

fun initCharMoves(charSpriteMap: Bitmap) {
    CharMoves.RIGHT.animation = SpriteAnimation(
        spriteMap = charSpriteMap,
        spriteWidth = 64,
        spriteHeight = 64,
        marginLeft = 16,
        marginTop = 78,
        columns = 9,
        rows = 1
    )

    CharMoves.LEFT.animation = SpriteAnimation(
        spriteMap = charSpriteMap,
        spriteWidth = 64,
        spriteHeight = 64,
        marginLeft = 16,
        marginTop = 208,
        columns = 9,
        rows = 1
    )
}