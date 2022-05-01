package action_ui

import character.CharMoves.*
import character.Character
import com.soywiz.korge.input.onDown
import com.soywiz.korge.input.onUp
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.Rectangle
import inventory.ThingType
import scenes.objects

fun Container.addActionButton(character: Character) {
    circle(radius = 50.0) {
        color = Colors.BLACK
        alpha(0.4)
        alignBottomToBottomOf(this@addActionButton, 20)
        alignRightToRightOf(this@addActionButton, 20)
        onDown {
            when (moveDirectory) {
                UP -> {
                    character.solid.centerXOn(character)
                    character.solid.alignTopToTopOf(character)
                }
                DOWN -> {
                    character.solid.centerXOn(character)
                    character.solid.alignBottomToBottomOf(character)
                }
                RIGHT -> {
                    character.solid.centerYOn(character)
                    character.solid.alignRightToRightOf(character)
                }
                LEFT -> {
                    character.solid.centerYOn(character)
                    character.solid.alignLeftToLeftOf(character)
                }
            }
            for (i in objects.filter{it.type == ThingType.NPC}) {
                if (i.sprite.collidesWith(character.solid)) {
                    i.sprite.removeFromParent()
                    objects.remove(i)
                    break
                }
            }
            alpha(0.2)
        }
        onUp {
            alpha(0.4)
        }
    }
}