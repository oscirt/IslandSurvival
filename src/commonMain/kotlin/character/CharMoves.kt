package character

import com.soywiz.korge.view.SpriteAnimation

enum class CharMoves {
    DOWN,
    UP,
    LEFT,
    RIGHT,
    SIT;

    lateinit var animation: SpriteAnimation
}