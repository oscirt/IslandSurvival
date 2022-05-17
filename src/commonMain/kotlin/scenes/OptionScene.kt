package scenes

import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.textFont
import com.soywiz.korge.ui.textSize
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.view.*
import com.soywiz.korim.font.readFont
import com.soywiz.korio.file.std.resourcesVfs

class OptionScene() : Scene() {
    override suspend fun Container.sceneInit() {
        val txt = text("THERE MUST BE OPTIONS") {
            font = resourcesVfs["font.ttf"].readFont()
            textSize = txtSize
            centerOn(this@OptionScene.sceneContainer)
        }

        uiButton {
            text = "BACK"
            textFont = myFont
            textSize = txtSize
            centerXOn(txt)
            alignTopToBottomOf(txt, 20)
            onClick {
                sceneContainer.back()
            }
        }
    }
}
