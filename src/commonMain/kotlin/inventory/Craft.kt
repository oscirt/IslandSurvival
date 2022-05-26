package inventory

import com.soywiz.korge.view.Sprite
import com.soywiz.korge.view.addTo
import com.soywiz.korio.concurrent.atomic.incrementAndGet
import player_data.atomicInt
import scenes.boardBitmap
import scenes.inventory
import scenes.knifeBitmap

fun craft(thing1: Thing, thing2: Thing): Thing? {
    if(thing1.type == thing2.type && thing1.type == ThingType.WOOD){
        return Thing(Sprite(boardBitmap).addTo(inventory), ThingType.BOARD, atomicInt.incrementAndGet())
    }
    if((thing1.type == ThingType.STONE && thing2.type == ThingType.WOOD) || (thing2.type == ThingType.STONE && thing1.type == ThingType.WOOD))
        return Thing(Sprite(knifeBitmap).addTo(inventory), ThingType.KNIFE, atomicInt.incrementAndGet())
    return null
}
