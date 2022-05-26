package inventory

import com.soywiz.korge.input.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.RGBA
import com.soywiz.korma.geom.Point
import scenes.exit_button
import scenes.inventory
import scenes.myFont
import scenes.txtSize

const val rows = 6
const val cols = 5
var inventoryCellSize = 0.0
var control = true

class Inventory(
    container: Container
) : Container() {
    var items = arrayListOf<ArrayList<InventoryCell>>()
    var craftLeftThing: Thing? = null
    var craftRightThing: Thing? = null
    var craftResultThing: Thing? = null
    var flag = false
    var isResult = false
    var isPressed = false
    var dragging = false
    var point: Point? = null

    init {
        inventoryCellSize = (container.height - 20) / rows
        val inventoryBackground = solidRect(container.width, container.height).apply {
            color = RGBA(100, 100, 100)
            centerOn(container)
        }
        for (i in 0 until rows) {
            items.add(arrayListOf())
            for (j in 0 until cols) {
                items[i].add(InventoryCell(rect = roundRect(inventoryCellSize, inventoryCellSize, 15.0) {
                    color = RGBA(139, 139, 139, 255)
                    x += (j * inventoryCellSize) + inventoryBackground.x + 10
                    y += (i * inventoryCellSize) + inventoryBackground.y + 10
                    onDown {
                        alpha(0.5)
                        dragging = true
                        point = Point(i, j)
                    }
                    onUpAnywhere {
                        alpha(1)
                        dragging = false
                    }
                    onMouseDrag {
                        flag = false
                        if (!dragging && point != null) {
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

        roundRect(inventoryCellSize, inventoryCellSize, 15.0) {
            color = RGBA(139, 139, 139)
            x = ((cols + 2) * ((inventoryCellSize+10) + 5)) + inventoryBackground.x
            y = (2 * (inventoryCellSize + 10)) + inventoryBackground.y - 10

            onUp {
                println("Right: $point/$craftLeftThing/$flag")
                if(point != null && craftLeftThing == null && !flag) {
                    craftLeftThing = items[point!!.x.toInt()][point!!.y.toInt()].thing
                    if (craftLeftThing != null) {
                        craftLeftThing!!.sprite.centerOn(this)
                        items[point!!.x.toInt()][point!!.y.toInt()].thing = null
                        flag = false
                    }
                }
            }

            onClick {
                if (craftLeftThing != null) {
                    updateInventory(craftLeftThing!!)
                    craftLeftThing = null
                    flag = true
                }
            }
        }
        roundRect(inventoryCellSize, inventoryCellSize, 15.0) {
            color = RGBA(139, 139, 139)
            x = (cols * ((inventoryCellSize+10) + 5)) + inventoryBackground.x
            y = (2 * (inventoryCellSize + 10)) + inventoryBackground.y - 10

            onUp {
                println("Left: $point/$craftRightThing/$flag")
                if(point != null && craftRightThing == null && !flag){
                    craftRightThing = items[point!!.x.toInt()][point!!.y.toInt()].thing
                    if (craftRightThing != null) {
                        craftRightThing!!.sprite.centerOn(this)
                        items[point!!.x.toInt()][point!!.y.toInt()].thing = null
                        flag = false
                    }
                }
            }

            onClick {
                if (craftRightThing != null) {
                    updateInventory(craftRightThing!!)
                    craftRightThing = null
                    flag = true
                }
            }
        }

        val craftRect = roundRect(inventoryCellSize, inventoryCellSize, 15.0) {
            color = RGBA(100, 150, 100, 255)
            x = ((cols + 1) * ((inventoryCellSize+10) + 5)) + inventoryBackground.x
            y = (3 * (inventoryCellSize + 10)) + inventoryBackground.y - 10

            addUpdater {
                if (craftLeftThing != null && craftRightThing != null && isResult) {
                    craftResultThing = craft(craftLeftThing!!, craftRightThing!!)
                    if (craftResultThing != null) {
                        craftResultThing!!.sprite.centerOn(this)
                    }
                    isResult = false
                } else if (craftLeftThing == null || craftRightThing == null) {
                    isResult = true
                    if (craftResultThing != null) {
                        craftResultThing!!.sprite.removeFromParent()
                        craftResultThing = null
                    }
                }
            }

            onClick {
                if (craftResultThing != null) {
                    updateInventory(craftResultThing!!)
                    craftResultThing = null
                    craftLeftThing!!.sprite.removeFromParent()
                    craftLeftThing = null
                    craftRightThing!!.sprite.removeFromParent()
                    craftRightThing = null
                }
            }

            onDown {
                alpha(0.5)
                dragging = true
            }
            onUpAnywhere {
                alpha(1)
                dragging = false
            }
        }
        text("CRAFT", txtSize, RGBA(50, 50, 50), myFont) {
            centerXOn(craftRect)
            alignBottomToTopOf(craftRect, inventoryCellSize * 1.5)
        }
        text("+", txtSize * 2, RGBA(50, 50, 50), myFont) {
            centerXOn(craftRect)
            alignBottomToTopOf(craftRect)
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
//                    println("$rows|$cols|$i|$j")
                    return i * cols + j
                }
            }
        }
        return -1
    }

    fun updateInventory(thing: Thing) : Boolean {
        val index = getFreeCellIndex()
        if (index == -1) return false
        val row = index / cols
        val col = index % cols
        items[row][col].thing = thing
        thing.sprite.addTo(this).centerOn(items[row][col].rect)
        return true
    }
}