package inventory

import com.soywiz.korge.view.Container
import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.centerOn
import com.soywiz.korge.view.roundRect
import com.soywiz.korim.color.RGBA

const val rows = 4
const val cols = 8
const val inventoryCell = 61.5

var control = true

class Inventory(
    container: Container
) : Container() {
    var items = arrayListOf<ArrayList<InventoryCell>>()

    init {
        roundRect(container.width, container.height, 0.0).apply {
            color = RGBA(0x00, 0x00, 0x00, 0x88)
        }
        val inventoryBackground = roundRect(container.width * 0.8, container.height * 0.8, 5.0).apply {
            color = RGBA(198, 198, 198)
            centerOn(container)
        }
        for (i in 0 until rows) {
            items.add(arrayListOf())
            for (j in 0 until cols) {
                items[i].add(InventoryCell(rect = roundRect(inventoryCell, inventoryCell, 15.0) {
                    color = RGBA(139, 139, 139, 255)
                    x += (j * inventoryCell) + inventoryBackground.x + 10
                    y += (i * inventoryCell) + inventoryBackground.y + 10
                }))
            }
        }
    }

    fun getFreeCellIndex() : Int {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                if (items[i][j].thing == null) {
                    println("$rows|$cols|$i|$j")
                    return i * cols + j
                }
            }
        }
        return -1
    }

    fun updateInventory(thing: Thing) {
        val row = getFreeCellIndex() / 8
        val col = getFreeCellIndex() % 8
        items[row][col].thing = thing
        thing.sprite.addTo(this).centerOn(items[row][col].rect)
    }
}