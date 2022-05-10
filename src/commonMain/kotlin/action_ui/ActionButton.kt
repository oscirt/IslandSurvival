package action_ui

import character.Character
import com.soywiz.korge.input.onDown
import com.soywiz.korge.input.onUpAnywhere
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import inventory.ThingType
import scenes.inventory
import scenes.objects
import scenes.toolBar

fun Container.addActionButton(character: Character) {
    val row = inventory.getFreeCellIndex() / 8
    val col = inventory.getFreeCellIndex() % 8
    circle(radius = 50.0) {
        color = Colors.BLACK
        alpha(0.4)
        alignBottomToBottomOf(this@addActionButton, 20)
        alignRightToRightOf(this@addActionButton, 20)
        onDown {
            for (i in objects.filter{it.type == ThingType.NPC}) {
                if (i.sprite.collidesWith(character.solid)) {
                    i.sprite.removeFromParent()
//                    objects.remove(i)
                    toolBar.updateToolbar(i)
                    break
                }
            }
            alpha(0.2)
        }
        onUpAnywhere {
            alpha(0.4)
        }
    }
}