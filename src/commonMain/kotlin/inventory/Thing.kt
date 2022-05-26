package inventory

import com.soywiz.korge.view.Sprite

open class Thing(
    val sprite: Sprite,
    val type: ThingType,
    var id: Int = 0
)