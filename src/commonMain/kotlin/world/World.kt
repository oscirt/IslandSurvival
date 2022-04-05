package world

import com.soywiz.korio.dynamic.dyn
import com.soywiz.korio.file.PathInfo
import com.soywiz.korio.file.Vfs
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korio.serialization.xml.Xml
import com.soywiz.korio.serialization.xml.isNode
import com.soywiz.korio.serialization.xml.readXml

class World(var array: ArrayList<ArrayList<Cell>>) {
    //генерирование земли
    suspend fun generateWorld(width: Int, height: Int) {
        val map = resourcesVfs["map.xml"].readXml().text
        var index = 0

        for (i in 0 until height) {
            array.add(ArrayList())
            for (j in 0 until width) {
                print(map[index])
                array[i].add(Cell(32 * j * 1.0, 32 * i * 1.0, if (map[index++] == '1') TileType.GROUND else TileType.WATER))
            }
        }

        for (i in 0 until 10) {
            for (j in 0 until 10) {
                array[i].add(Cell(32 * j * 1.0, 32 * i * 1.0, TileType.WATER))
            }
        }
    }
}