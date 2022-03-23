import com.soywiz.korim.bitmap.Bitmap

data class Pic (val x: Double, val y: Double, val tileType: TileType) {
    fun getPic(): Bitmap = tileType.slice.bmp
}