package player_data

import action_ui.padding
import com.soywiz.korge.tiled.objects
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.Sprite
import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.xy
import com.soywiz.korma.geom.Point
import inventory.Thing
import inventory.ThingType
import scenes.characterBitmap
import scenes.inventoryBitmap
import scenes.objects
import scenes.tiledMap

lateinit var startPosition: Point

fun readObjects() {
    for (j in tiledMap.data.objectLayers.objects) {
        for ((key, value) in j.objectsByType) {
            when (key) {
                "npc" -> {
                    for (i in value.indices) {
                        objects.add(
                            Thing(
                                Sprite(characterBitmap)
                                    .xy(
                                        value[i].x - padding.x,
                                        value[i].y - padding.y
                                    ),
                                ThingType.NPC
                            )
                        )
                    }
                }
                "chest" -> {
                    for (i in value.indices) {
                        objects.add(
                            Thing(
                                Sprite(inventoryBitmap)
                                    .xy(
                                        value[i].x - padding.x,
                                        value[i].y - padding.y
                                    ),
                                ThingType.CHEST
                            )
                        )
                    }
                }
                "start" -> {
                    startPosition = value.first().bounds.position
                    startPosition.x -= padding.x
                    startPosition.y -= padding.y
                }
            }
        }
    }
}

fun attachObjectsTo(container: Container) {
    objects.forEach {
        it.sprite.addTo(container)
    }
}