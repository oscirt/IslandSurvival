package inventory

import com.soywiz.korge.view.Sprite
import com.soywiz.korio.concurrent.atomic.KorAtomicInt
import com.soywiz.korio.concurrent.atomic.incrementAndGet

class Thing(
    val sprite: Sprite,
    val type: ThingType,
    var id: Int = 0
) {
}