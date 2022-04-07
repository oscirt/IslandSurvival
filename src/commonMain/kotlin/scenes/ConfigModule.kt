package scenes

import com.soywiz.korge.scene.Module
import com.soywiz.korge.scene.Scene
import com.soywiz.korinject.AsyncInjector
import com.soywiz.korma.geom.SizeInt
import kotlin.reflect.KClass

object ConfigModule : Module() {
    override val size = SizeInt(1280, 720)
    override val clipBorders = false
    override val mainScene: KClass<out Scene> = MainMenu::class

    override suspend fun AsyncInjector.configure() {
        mapPrototype { MainMenu() }
        mapPrototype { GameScene() }
        mapPrototype { OptionScene() }
    }
}