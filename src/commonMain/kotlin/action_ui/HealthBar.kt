package action_ui

import com.soywiz.kmem.clamp
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors

lateinit var hpBlock: RoundRect
lateinit var hpBar: RoundRect
lateinit var hpBarBackground: RoundRect

const val maxHitPoints = 100.0
const val minHitPoints = 100.0

var scaleHitPoints: Double = 0.0
var hitPoints = 100.0

class HealthBar(
    val container: Container,
) : Container() {
    init {
        scaleHitPoints = (container.width / 8) / hitPoints

        hpBlock = roundRect(container.width / 8 + 10, container.height / 64 + 10, 5.0) {
            color = Colors.ANTIQUEWHITE
            alignLeftToLeftOf(container, 10)
            alignTopToTopOf(container, 10)
        }

        hpBarBackground = roundRect(container.width / 8, container.height / 64, 5.0) {
            color = Colors.DARKGREY
            centerOn(hpBlock)
        }

        hpBar = roundRect(container.width / 8, container.height / 64, 5.0) {
            color = Colors.ORANGERED
            centerOn(hpBlock)
        }

        this.addTo(container)
    }

    fun changeHp(dif: Double) {
        hitPoints += dif
        hitPoints.clamp(minHitPoints, maxHitPoints)
        hpBar.scaledWidth = hitPoints * scaleHitPoints
        if (hitPoints <= 0) {
            println("GAME OVER")
            hitPoints = 100.0
        }
    }
}