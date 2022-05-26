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
import com.soywiz.korio.async.launch
import com.soywiz.korio.async.runBlockingNoJs
import com.soywiz.korio.concurrent.atomic.incrementAndGet
import com.soywiz.korio.dynamic.KDynamic.Companion.toInt
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.cos
import com.soywiz.korma.geom.sin
import com.soywiz.korma.geom.vector.circle
import inventory.ThingType
import inventory.control
import io.ktor.websocket.*
import scenes.*
import serialization.Serialization
import kotlin.math.hypot

val padding = Point(21.75, 47.0)

var dx = 0.0
var dy = 0.0

const val speed = 1.0
const val radius = 70.0

//var moveDirection = DOWN

fun Container.addJoystick(
    character: Character
) {
    var dragging = false
    val view = this
    lateinit var ball: View
    lateinit var circle: View
    lateinit var actionButton: View

    var pxLeft: Double
    var pyLeft: Double
    var pxRight: Double
    var pyRight: Double

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
        actionButton = graphics {
            fill(Colors.BLACK) { circle(0.0, 0.0, 50.0) }
            alpha(0.4)
            alignBottomToBottomOf(this@addJoystick, 20)
            alignRightToRightOf(this@addJoystick, 20)
        }
    }

    this.addComponent(object : TouchComponent {
        override val view: BaseView = view
        val start = Point(0, 0)
        var flag = false

        override fun onTouchEvent(views: Views, e: TouchEvent) {
            if (control) {
                flag = false
                pxLeft = 0.0
                pyLeft = 0.0
                pxRight = 0.0
                pyRight = 0.0
                if (e.activeTouches.size == 1) {
                    if (e.activeTouches.first().x >= (views.nativeWidth / 2)) {
                        pxRight = e.activeTouches.first().x
                        pyRight = e.activeTouches.first().y
                    } else {
                        pxLeft = e.activeTouches.first().x
                        pyLeft = e.activeTouches.first().y
                    }
                } else if (e.activeTouches.size == 2) {
                    if (e.activeTouches.first().x >= (views.nativeWidth / 2)) {
                        pxRight = e.activeTouches.first().x
                        pyRight = e.activeTouches.first().y
                        pxLeft = e.activeTouches[1].x
                        pyLeft = e.activeTouches[1].y
                    } else {
                        pxLeft = e.activeTouches.first().x
                        pyLeft = e.activeTouches.first().y
                        pxRight = e.activeTouches[1].x
                        pyRight = e.activeTouches[1].y
                    }
                }

                if (actionButton.hitTestAny(pxRight, pyRight)) {
                    actionButton.alpha(0.2)
                    for (i in objects.filter { it.type != ThingType.NPC }) {
                        if (i.sprite.collidesWith(character.solid)) {
                            i.sprite.removeFromParent()
                            if (isOnline) runBlockingNoJs { session.send(i.id.toString()) }
//                            objects.remove(i)
                            toolBar.updateToolbar(i)
                            break
                        }
                    }
                }
                if (pxLeft == 0.0 && pyLeft == 0.0) flag = true

                when (e.type) {
                    TouchEvent.Type.START -> {
                        when {
                            circle.hitTestAny(pxLeft, pyLeft) -> {
                                if (!dragging) {
                                    start.x = pxLeft
                                    start.y = pyLeft
                                    ball.alpha = 0.3
                                    dragging = true
                                }
                            }

                        }
                    }
                    TouchEvent.Type.END -> {
                        if (flag) {
                            ball.position(0, 0)
                            ball.alpha = 0.2
                            dragging = false
                            character.moveDirection = STOP
                            character.sprite.stopAnimation()
                            update(0.0, 0.0, view)
                        }
                        actionButton.alpha(0.4)
                    }
                    TouchEvent.Type.MOVE -> {
                        if (dragging) {
                            val deltaX = (pxLeft - start.x) * 0.5
                            val deltaY = (pyLeft - start.y) * 0.5
                            val length = hypot(deltaX, deltaY)
                            val maxLength = radius * 0.5
                            val lengthClamped = length.clamp(0.0, maxLength)
                            val angle = Angle.between(start.x, start.y, pxLeft, pyLeft)
                            ball.position(cos(angle) * lengthClamped, sin(angle) * lengthClamped)
                            if (cos(angle) in -0.7..0.7 && sin(angle) <= -0.7) {
                                character.sprite.playAnimationLooped(
                                    UP.animation,
                                    100.milliseconds
                                )
                                if (character.moveDirection != UP) {
                                    character.moveDirection = UP
                                    character.solid.centerXOn(character)
                                    character.solid.alignTopToTopOf(character)
                                }
                            } else if (cos(angle) in -0.7..0.7 && sin(angle) >= 0.7) {
                                character.sprite.playAnimationLooped(
                                    DOWN.animation,
                                    100.milliseconds
                                )
                                if (character.moveDirection != DOWN) {
                                    character.moveDirection = DOWN
                                    character.solid.centerXOn(character)
                                    character.solid.alignBottomToBottomOf(character)
                                }
                            } else if (sin(angle) in -0.7..0.7 && cos(angle) >= 0.7) {
                                character.sprite.playAnimationLooped(
                                    RIGHT.animation,
                                    100.milliseconds
                                )
                                if (character.moveDirection != RIGHT) {
                                    character.moveDirection = RIGHT
                                    character.solid.centerYOn(character)
                                    character.solid.alignRightToRightOf(character)
                                }
                            } else if (sin(angle) in -0.7..0.7 && cos(angle) <= -0.7) {
                                character.sprite.playAnimationLooped(
                                    LEFT.animation,
                                    100.milliseconds
                                )
                                if (character.moveDirection != LEFT) {
                                    character.moveDirection = LEFT
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
        if (isOnline) {
            runBlockingNoJs {
                session.send(
                    Serialization.getJsonFromPoint(
                        model.Point(characterName, container.x, container.y, container.moveDirection.ordinal)
                    )
                )
            }
        }
    } else {
        container.x += (dx * scale) * speed
        container.y += (dy * scale) * speed
    }
}