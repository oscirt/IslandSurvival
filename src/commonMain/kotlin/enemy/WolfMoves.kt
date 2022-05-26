package enemy

import com.soywiz.korge.view.SpriteAnimation
import com.soywiz.korim.bitmap.Bitmap

enum class WolfMoves {
    UP,
    DOWN,
    RIGHT,
    LEFT,
    STOP;

    lateinit var animation: SpriteAnimation
}

fun initWolfMoves(wolfSpriteMap: Bitmap) {
    WolfMoves.UP.animation = SpriteAnimation (
        spriteMap = wolfSpriteMap,
        spriteWidth = 48,
        spriteHeight = 48,
        marginLeft = 0,
        marginTop = 144,
        columns = 3,
        rows = 1
    )

    WolfMoves.DOWN.animation = SpriteAnimation(
        spriteMap = wolfSpriteMap,
        spriteWidth = 48,
        spriteHeight = 48,
        marginLeft = 0,
        marginTop = 0,
        columns = 3,
        rows = 1
    )

    WolfMoves.RIGHT.animation = SpriteAnimation(
        spriteMap = wolfSpriteMap,
        spriteWidth = 48,
        spriteHeight = 48,
        marginLeft = 0,
        marginTop = 96,
        columns = 3,
        rows = 1
    )

    WolfMoves.LEFT.animation = SpriteAnimation(
        spriteMap = wolfSpriteMap,
        spriteWidth = 48,
        spriteHeight = 48,
        marginLeft = 0,
        marginTop = 48,
        columns = 3,
        rows = 1
    )
}