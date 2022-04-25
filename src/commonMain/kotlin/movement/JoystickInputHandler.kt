package movement

import character.CharMoves
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
import kotlin.math.hypot

var isRight: Boolean = false
fun Container.addJoystick(
    sprite: Sprite,
    radius: Double = 70.0,
    onStick: (x: Double, y: Double) -> Unit = { _, _, -> }
) {
    var dragging = false
    val view = this
    lateinit var ball: View

    container {
        x += radius + 15
        y += view.height - 15 - radius
        graphics {
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

            when (e.type) {
                TouchEvent.Type.START -> {
                    when {
                        px in views.virtualLeft+10..views.virtualLeft+285 && py in views.virtualBottom-285..views.virtualBottom-10-> {
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
                    sprite.stopAnimation()
                    onStick(0.0, 0.0)
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
                        if (cos(angle) < 0) {
                            sprite.playAnimationLooped(
                                CharMoves.RIGHT.animation,
                                100.milliseconds
                            )
                            isRight = false
                        } else {
                            sprite.playAnimationLooped(
                                CharMoves.LEFT.animation,
                                100.milliseconds
                            )
                            isRight = true
                        }
                        val lengthNormalized = lengthClamped / maxLength
                        onStick(cos(angle) * lengthNormalized, sin(angle) * lengthNormalized)
                    }
                }
            }
        }
    })
}