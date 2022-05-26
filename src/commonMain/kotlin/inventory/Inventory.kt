package inventory

import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onDown
import com.soywiz.korge.input.onMouseDrag
import com.soywiz.korge.input.onUpAnywhere
import com.soywiz.korge.view.*
import com.soywiz.korim.color.RGBA
import scenes.exit_button
import scenes.inventory

const val rows = 6
const val cols = 5
var inventoryCell = 0.0

var control = true

class Inventory(
    container: Container
) : Container() {
    var items = arrayListOf<ArrayList<InventoryCell>>()
    var isPressed = false
    var dragging = false

    init {
        inventoryCell = (container.height - 20) / rows
        val inventoryBackground = solidRect(container.width, container.height).apply {
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
                    onDown {
                        alpha(0.5)
                        dragging = true
                    }
                    onUpAnywhere {
                        alpha(1)
                        dragging = false
                    }
                    onMouseDrag {
                        if (!dragging) {
                            items[i][j].thing?.sprite?.centerOn(this@roundRect)
                        } else if (items[i][j].thing != null) {
                            isPressed = true
                            items[i][j].thing?.sprite?.x = this.globalMouseX
                            items[i][j].thing?.sprite?.y = this.globalMouseY
                        }
                    }
                }))
            }
        }

        sprite(exit_button) {
            alignTopToTopOf(this@Inventory, 10)
            alignRightToRightOf(this@Inventory, 10)
            onClick {
                control = true
                inventory.removeFromParent()
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
        val index = getFreeCellIndex()
        val row = index / cols
        val col = index % cols
        items[row][col].thing = thing
        thing.sprite.addTo(this).centerOn(items[row][col].rect)
    }
}