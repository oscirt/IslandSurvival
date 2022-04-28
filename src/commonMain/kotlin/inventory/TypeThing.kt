package inventory

import com.soywiz.korge.view.Image
import scenes.inventorySprite
import scenes.personSprite

enum class TypeThing(
    val img: Image
) {
    NPC(Image(personSprite.bitmap)),
    CHEST(Image(inventorySprite))
}