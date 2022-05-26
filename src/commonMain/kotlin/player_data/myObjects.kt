package player_data

import action_ui.padding
import com.soywiz.korge.tiled.objects
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.Sprite
import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.xy
import com.soywiz.korio.concurrent.atomic.KorAtomicInt
import com.soywiz.korio.concurrent.atomic.incrementAndGet
import com.soywiz.korma.geom.Point
import enemy.Wolf
import inventory.Thing
import inventory.ThingType
import scenes.*

var startPosition: Point = Point(800, 800)
var atomicInt = KorAtomicInt(0)

fun readObjects() {
    /*
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
                                ThingType.NPC,
                                atomicInt.incrementAndGet()
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
                                ThingType.CHEST,
                                atomicInt.incrementAndGet()
                            )
                        )
                    }
                }
                "start" -> {
                    startPosition = value.first().bounds.position
                    startPosition.x -= padding.x
                    startPosition.y -= padding.y
                }
                "wood" -> {
                    startPosition = value.first().bounds.position
                    startPosition.x -= padding.x
                    startPosition.y -= padding.y

                }
            }
        }
    }

     */
    for (j in tiledMap.data.objectLayers.objects) {
        for ((key, value) in j.objectsByType) {
            when(key){
                "wood" -> {
                    for (i in value.indices) {
                        objects.add(
                            Thing(
                                Sprite(wood)
                                    .xy(
                                        value[i].x - padding.x,
                                        value[i].y - padding.y
                                    ),
                                ThingType.WOOD,
                                atomicInt.incrementAndGet()
                            )
                        )
                    }
                }
                "stone" -> {
                    for (i in value.indices) {
                        objects.add(
                            Thing(
                                Sprite(stone)
                                    .xy(
                                        value[i].x - padding.x,
                                        value[i].y - padding.y
                                    ),
                                ThingType.STONE,
                                atomicInt.incrementAndGet()
                            )
                        )
                    }
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