package world

import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.bitmap.BitmapSlice

enum class TileType {
    GROUND,
    WATER;

    lateinit var slice: BitmapSlice<Bitmap>
    var isWall: Boolean = false
}