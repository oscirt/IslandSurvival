package inventory

import com.soywiz.korge.view.Sprite
import com.soywiz.korio.concurrent.atomic.incrementAndGet
import player_data.atomicInt
import scenes.board
import scenes.knife
//enum class Craft(
//    first: ThingType,
//    second: ThingType
//) {
//    AXE(ThingType.WOOD, ThingType.STONE)
//
//}

fun craft(thing1: Thing, thing2: Thing): Thing? {
    if(thing1.type == thing2.type && thing1.type == ThingType.WOOD){
        return Thing(Sprite(board), ThingType.BOARD, atomicInt.incrementAndGet())
    }
    if((thing1.type == ThingType.STONE && thing2.type == ThingType.WOOD) || (thing2.type == ThingType.STONE && thing1.type == ThingType.WOOD))
        return Thing(Sprite(knife), ThingType.KNIFE, atomicInt.incrementAndGet())
    return null
}
