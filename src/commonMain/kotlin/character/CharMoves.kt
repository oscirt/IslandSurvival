package character

import com.soywiz.korge.view.SpriteAnimation
import com.soywiz.korim.bitmap.Bitmap

enum class CharMoves {
    UP,
    DOWN,
    RIGHT,
    LEFT,
    STOP;

    lateinit var animation: SpriteAnimation
}

fun initCharMoves(charSpriteMap: Bitmap) {
    CharMoves.UP.animation = SpriteAnimation(
        spriteMap = charSpriteMap,
        spriteWidth = 64,
        spriteHeight = 64,
        marginLeft = 80,
        marginTop = 15,
        columns = 8,
        rows = 1
    )

    CharMoves.DOWN.animation = SpriteAnimation(
        spriteMap = charSpriteMap,
        spriteWidth = 64,
        spriteHeight = 64,
        marginLeft = 80,
        marginTop = 143,
        columns = 8,
        rows = 1
    )

    CharMoves.RIGHT.animation = SpriteAnimation(
        spriteMap = charSpriteMap,
        spriteWidth = 64,
        spriteHeight = 64,
        marginLeft = 16,
        marginTop = 207,
        columns = 9,
        rows = 1
    )

    CharMoves.LEFT.animation = SpriteAnimation(
        spriteMap = charSpriteMap,
        spriteWidth = 64,
        spriteHeight = 64,
        marginLeft = 16,
        marginTop = 79,
        columns = 9,
        rows = 1
    )
}

fun chooseAnimation(num: Int) : SpriteAnimation {
    return when(num) {
        0 -> CharMoves.UP.animation
        1 -> CharMoves.DOWN.animation
        2 -> CharMoves.RIGHT.animation
        3 -> CharMoves.LEFT.animation
        else -> CharMoves.UP.animation
    }
}