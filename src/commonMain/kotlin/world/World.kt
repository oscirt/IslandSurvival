package world

import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korio.serialization.xml.Xml
import com.soywiz.korio.serialization.xml.readXml
import kotlin.random.Random

class World(var array: ArrayList<ArrayList<Cell>>) {
    //генерирование земли
    suspend fun generateWorld(width: Int, height: Int) {
        val map = resourcesVfs["mapp.xml"].readXml().text
        var index = 0

        for (i in 0 until height) {
            array.add(ArrayList())
            for (j in 0 until width) {
                array[i].add(Cell(32 * j * 1.0, 32 * i * 1.0, if (map[index++] == '1') TileType.GROUND else TileType.WATER))
            }
        }

        for (i in 0 until 10) {
            for (j in 0 until 10) {
                array[i].add(Cell(32 * j * 1.0, 32 * i * 1.0, TileType.WATER))
            }
        }

        // по хорошему надо воспользоваться средствами korge по работе с xml, а не костыли писать
        val sb = StringBuilder()
        sb.append("<?xml version='1.0' encoding='utf-8'?><usrconfig>")
        for (i in 0..59) {
            for (j in 0..29) {
                sb.append("<tile>" + (Random.nextInt() % 2 + 1) + "</tile>")
            }
        }
        sb.append("</usrconfig>")
        resourcesVfs["mapp.xml"].writeString(sb.toString())
    }
}