package inventory

import com.soywiz.korge.view.RoundRect

class InventoryCell(
    var thing: Thing? = null,
) {
    lateinit var rect: RoundRect
}