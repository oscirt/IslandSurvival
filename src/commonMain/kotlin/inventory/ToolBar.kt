package inventory

import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onDown
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.color.Colors

class ToolBar(
    private val inventory: Inventory,
    private val container: Container,
    private val inventorySprite: Bitmap
) : Container() {
    private val tools = arrayListOf<InventoryCell>()
    private var selected = 1

    init {
        val rect = solidRect(705, 95, Colors.LIGHTGREY) {
            for (i in 0 until 8) {
                tools.add(InventoryCell())
                tools[i].rect = roundRect(75.0, 75.0, 5.0){
                    alignTopToTopOf(this, 10)
                    x += 10 + i * 75
                    onDown {
                        tools[selected].rect.alpha = 1.0
                        alpha = 0.5
                        selected = i
                    }
                }
            }
        }
        roundRect(75.0, 75.0, 5.0) {
            alignTopToTopOf(this, 10)
            alignRightToRightOf(rect, 10)
            image(inventorySprite) {
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
}