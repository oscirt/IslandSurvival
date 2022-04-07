package scenes

import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.textFont
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.ui.uiSkin
import com.soywiz.korge.view.*
import com.soywiz.korim.font.readFont
import com.soywiz.korim.font.readTtfFont
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs

const val txtSize = 56.0

class MainMenu() : Scene() {
    override suspend fun Container.sceneInit() {
        val myFont = resourcesVfs["font.ttf"].readFont()

        val backgroundImg = resourcesVfs["backgroundMainMenu.jpg"].readBitmap()
        image(backgroundImg) {
            scaledHeight = 720.0
            scaledWidth = 1280.0
        }

        text(text = "START GAME") {
            font = myFont
            textSize = txtSize
            onClick {
                sceneContainer.changeTo<GameScene>()
            }
            alignBottomToBottomOf(this@MainMenu.sceneContainer, 60 + this.height * 2)
            alignRightToRightOf(this@MainMenu.sceneContainer, 20)
        }

        text("OPTIONS") {
            font = myFont
            textSize = txtSize
            onClick {
                sceneContainer.pushTo<OptionScene>()
            }
            alignBottomToBottomOf(this@MainMenu.sceneContainer, 40 + this.height)
            alignRightToRightOf(this@MainMenu.sceneContainer, 20)
        }

        text("EXIT") {
            font = myFont
            textSize = txtSize
            onClick {
                views.gameWindow.close()
            }
            alignBottomToBottomOf(this@MainMenu.sceneContainer, 20)
            alignRightToRightOf(this@MainMenu.sceneContainer, 20)
        }
    }
}