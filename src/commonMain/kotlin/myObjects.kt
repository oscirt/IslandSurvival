import com.soywiz.korge.tiled.objects
import com.soywiz.korge.view.*
import inventory.Thing
import inventory.ThingType
import scenes.*

fun readObjects() {
    for ((i, j) in tiledMap.data.objectLayers.objects.first().objectsByType) {
        when (i) {
            "npc" -> {
                for (k in j.indices) {
                    objects.add(Thing(
                        Sprite(characterBitmap)
                            .xy(j[k].x, j[k].y),
                        ThingType.NPC))
                }
            }
            "chest" -> {
                for (k in j.indices) {
                    objects.add(Thing(
                        Sprite(inventoryBitmap)
                            .xy(j[k].x,  j[k].y),
                        ThingType.CHEST))
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