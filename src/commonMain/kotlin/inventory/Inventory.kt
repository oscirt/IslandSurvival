package inventory

import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.font.readFont
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korio.async.async
import com.soywiz.korio.async.launch
import com.soywiz.korio.file.std.resourcesVfs
import kotlinx.coroutines.Job
import kotlinx.coroutines.job
import scenes.inventory
import scenes.myFont
import kotlin.coroutines.coroutineContext

const val rows = 6
const val cols = 5
var inventoryCell = 0.0

var control = true

class Inventory(
    container: Container
) : Container() {
    var items = arrayListOf<ArrayList<InventoryCell>>()
    var isPressed = false

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
//                    onDown {
//                        if (!isPressed) alpha(0.5)
//                    }
                    onUpAnywhere {
                        alpha(1)
                        items[i][j].thing?.sprite?.centerOn(this)
                    }
                    onMove {
                        if (items[i][j].thing != null) {
                            isPressed = true
                            println("${it.currentPosGlobal.x}/${it.currentPosGlobal.y}")
                            println("${it.currentPosStage.x}/${it.currentPosStage.y}")
                            items[i][j].thing?.sprite?.x = it.currentPosStage.x
                            items[i][j].thing?.sprite?.y = it.currentPosStage.y
                        }
                    }
                }))
            }
        }

        // TODO: 17.05.2022 add png exit button
        uiButton {
            text("EXIT")
            textFont = myFont
            textSize = textSize
            buttonTextAlignment = TextAlignment.MIDDLE_CENTER
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
//        println("*------------------------*")
//        println(thing)
//        println(items[row][col].thing)
//        println("$row | $col")
//        println("*_____|$row /// $col|_____*")
        items[row][col].thing = thing
//        println("*------------------------*")
//        for (i in 0 until rows) {
//            for (j in 0 until cols) {
//                print("${if (items[i][j].thing == null) 0 else 1} ")
//            }
//            println()
//        }
//        println("*------------------------*")
        thing.sprite.addTo(this).centerOn(items[row][col].rect)
    }
}