package world

import com.soywiz.korge.view.Image
import com.soywiz.korge.view.xy
import com.soywiz.korim.bitmap.Bitmap

class Cell(
    val x: Double,
    val y: Double,
    val tileType: TileType,
) {
    val image: Image = Image(getPic()).xy(x, y)

    private fun getPic(): Bitmap = tileType.slice.bmp
}