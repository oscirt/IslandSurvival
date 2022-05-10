package camera

import character.Character
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.camera.CameraContainer
import scenes.tiledMapView

fun createCamera(character: Character, container: Container) : CameraContainer {
    return CameraContainer(640.0, 360.0, clip = true, block = { clampToBounds = true }).apply {
        content.addChild(tiledMapView)
//        content.addChild(charactersLayer)
        cameraViewportBounds.copyFrom(tiledMapView.getLocalBoundsOptimized())
        follow(character.sprite, setImmediately = true)
    }.addTo(container)
}