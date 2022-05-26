package enemy

import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.Point

class Wolf(
    wolfBitmap: Bitmap,
    startX: Double,
    startY: Double
) : FixedSizeContainer(32.0, 32.0) {
    init {
        val sprite = sprite(wolfBitmap, 32.0, 32.0).addTo(this)
        solidRect(32, 2) {
            alignBottomToTopOf(sprite)
            color = Colors.ORANGERED
        }
        x = startX - 24
        y = startY - 24
    }
}