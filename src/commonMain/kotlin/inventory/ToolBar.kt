package inventory

import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onDown
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import scenes.inventoryBitmap

class ToolBar(
    private val inventory: Inventory,
    private val container: Container
) : Container() {
    private var length = 8
    var tools = arrayListOf<InventoryCell>()
    var selected = 0
    var current = 0

    init {
        val rect = solidRect(705, 95, Colors.LIGHTGREY) {
            for (i in 0 until length) {
                tools.add(InventoryCell(rect = roundRect(75.0, 75.0, 5.0){
                    alignTopToTopOf(this, 10)
                    x += 10 + i * 75
                    onDown {
                        tools[selected].rect.alpha = 1.0
                        alpha = 0.5
                        selected = i
                    }
                }))
            }
        }
        roundRect(75.0, 75.0, 5.0) {
            alignTopToTopOf(this, 10)
            alignRightToRightOf(rect, 10)
            sprite(inventoryBitmap).apply {
                scale = 0.8
                centerOn(this@roundRect)
                onClick {
                    control = false
                    inventory.addTo(container)
                }
            }
        }
        scale = 0.5
        centerXOn(container)
        alignBottomToBottomOf(container)
        addTo(container)
    }


    fun updateToolbar(thing: Thing) {
        if (current == 8) {
            inventory.updateInventory(thing)
            return
        }
        thing.sprite.addTo(this).centerOn(tools[current].rect)
        current++
    }
}