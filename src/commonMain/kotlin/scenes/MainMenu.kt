package scenes

import com.soywiz.klock.seconds
import com.soywiz.korev.MouseEvent
import com.soywiz.korge.input.EventsDslMarker
import com.soywiz.korge.input.MouseEvents
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.MaskTransition
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.scene.SceneContainer
import com.soywiz.korge.view.*
import com.soywiz.korge.view.filter.TransitionFilter
import com.soywiz.korim.font.readFont
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs

const val txtSize = 32.0
const val fontName = "font.ttf"

object BackgroundMainMenu {
    const val src = "backgroundMainMenu.jpg"
    object Dimension {
        const val width = 640.0
        const val height = 360.0
    }
}
interface MainMenuItem {
    val text: String
    val onClick: suspend (it: MouseEvents) -> Unit
}

object MainMenuLayout {
    const val padding = 20
    const val rowGap = 20
}

class MainMenu() : Scene() {

    override suspend fun Container.sceneInit() {
        val font = resourcesVfs[fontName].readFont()

        val backgroundImg = resourcesVfs[BackgroundMainMenu.src].readBitmap()
        image(backgroundImg) {
            scaledHeight = BackgroundMainMenu.Dimension.height
            scaledWidth = BackgroundMainMenu.Dimension.width
        }

        val mainMenuItems = buildList {
            add(object: MainMenuItem {
                override val text = "START GAME"
                override val onClick: @EventsDslMarker suspend (_: MouseEvents) -> Unit = {
                    sceneContainer.changeTo<GameScene>(
                        transition = MaskTransition(
                            transition = TransitionFilter.Transition.VERTICAL,
                            reversed = false,
                            smooth = true,
                            filtering = true
                        ),
                        time = 0.5.seconds
                    )
                }
            })

            add(object: MainMenuItem {
                override val text = "OPTIONS"
                override val onClick: @EventsDslMarker suspend (_: MouseEvents) -> Unit = {
                    sceneContainer.pushTo<OptionScene>()
                }
            })

            add(object: MainMenuItem {
                override val text = "EXIT"
                override val onClick: @EventsDslMarker suspend (_: MouseEvents) -> Unit ={
                    views.gameWindow.close()
                }
            })
        }

        mainMenuItems.reversed().forEachIndexed { idx, it ->
            text(text = it.text) {
                this.font = font
                textSize = txtSize
                onClick(it.onClick)
                alignBottomToBottomOf(this@MainMenu.sceneContainer,  MainMenuLayout.padding + (this.height + MainMenuLayout.rowGap) * idx)
                alignRightToRightOf(this@MainMenu.sceneContainer, MainMenuLayout.padding)
            }}
    }
}