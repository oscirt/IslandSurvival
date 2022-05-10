package action_ui

import character.CharMoves.*
import character.Character
import com.soywiz.klock.milliseconds
import com.soywiz.kmem.clamp
import com.soywiz.korev.TouchEvent
import com.soywiz.korge.baseview.BaseView
import com.soywiz.korge.component.TouchComponent
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.cos
import com.soywiz.korma.geom.sin
import com.soywiz.korma.geom.vector.circle
import scenes.tiledMapView
import kotlin.math.hypot

val padding = Point(21.75, 47.0)

var dx = 0.0
var dy = 0.0

const val speed = 1.0
const val radius = 70.0

var moveDirectory = DOWN

fun Container.addJoystick(
    character: Character
) {
    var dragging = false
    val view = this
    lateinit var ball: View
    lateinit var circle: View

    container {
        x += radius + 15
        y += view.height - 15 - radius
        circle = graphics {
            fill(Colors.BLACK) { circle(0.0, 0.0, radius) }
            alpha(0.2)
        }
        ball = graphics {
            fill(Colors.WHITE) { circle(0.0, 0.0, radius * 0.5) }
            alpha(0.2)
        }
    }

    this.addComponent(object : TouchComponent {
        override val view: BaseView = view
        val start = Point(0, 0)

        override fun onTouchEvent(views: Views, e: TouchEvent) {
            val px = e.activeTouches.firstOrNull()?.x ?: 0.0
            val py = e.activeTouches.firstOrNull()?.y ?: 0.0

            // TODO: 01.05.2022 add multiple touch handling

            when (e.type) {
                TouchEvent.Type.START -> {
                    when {
                        circle.hitTestAny(px, py) -> {
                            start.x = px
                            start.y = py
                            ball.alpha = 0.3
                            dragging = true
                        }
                    }
                }
                TouchEvent.Type.END -> {
                    ball.position(0, 0)
                    ball.alpha = 0.2
                    dragging = false
                    character.sprite.stopAnimation()
                    update(0.0, 0.0, view)
                }
                TouchEvent.Type.MOVE -> {
                    if (dragging) {
                        val deltaX = px - start.x
                        val deltaY = py - start.y
                        val length = hypot(deltaX, deltaY)
                        val maxLength = radius * 0.5
                        val lengthClamped = length.clamp(0.0, maxLength)
                        val angle = Angle.between(start.x, start.y, px, py)
                        ball.position(cos(angle) * lengthClamped, sin(angle) * lengthClamped)
                        if (cos(angle) in -0.7..0.7 && sin(angle) <= -0.7) {
                            character.sprite.playAnimationLooped(
                                UP.animation,
                                100.milliseconds
                            )
                            if (moveDirectory != UP) {
                                moveDirectory = UP
                                character.solid.centerXOn(character)
                                character.solid.alignTopToTopOf(character)
                            }
                        } else if (cos(angle) in -0.7..0.7 && sin(angle) >= 0.7) {
                            character.sprite.playAnimationLooped(
                                DOWN.animation,
                                100.milliseconds
                            )
                            if (moveDirectory != DOWN) {
                                moveDirectory = DOWN
                                character.solid.centerXOn(character)
                                character.solid.alignBottomToBottomOf(character)
                            }
                        } else if (sin(angle) in -0.7..0.7 && cos(angle) >= 0.7) {
                            character.sprite.playAnimationLooped(
                                RIGHT.animation,
                                100.milliseconds
                            )
                            if (moveDirectory != RIGHT) {
                                moveDirectory = RIGHT
                                character.solid.centerYOn(character)
                                character.solid.alignRightToRightOf(character)
                            }
                        } else if (sin(angle) in -0.7..0.7 && cos(angle) <= -0.7) {
                            character.sprite.playAnimationLooped(
                                LEFT.animation,
                                100.milliseconds
                            )
                            if (moveDirectory != LEFT) {
                                moveDirectory = LEFT
                                character.solid.centerYOn(character)
                                character.solid.alignLeftToLeftOf(character)
                            }
                        }
                        val lengthNormalized = lengthClamped / maxLength
                        update(cos(angle) * lengthNormalized, sin(angle) * lengthNormalized, view)
                    }
                }
                else -> TODO()
            }
        }
    })
}

fun update(x: Double, y: Double, view: View) {
    view.addUpdater {
        dx = x * 2.0 * (-1)
        dy = y * 2.0 * (-1)
    }
}

fun move(container: Container) {
    val scale = 0.42
    dx = dx.clamp(-10.0, +10.0)
    dy = dy.clamp(-10.0, +10.0)
    if (container is Character) {
        container.x += padding.x
        container.y += padding.y
        container.moveWithHitTestable(tiledMapView, -(dx * scale) * speed, -(dy * scale) * speed)
        container.x -= padding.x
        container.y -= padding.y
    } else {
        container.x += (dx * scale) * speed
        container.y += (dy * scale) * speed
    }
}