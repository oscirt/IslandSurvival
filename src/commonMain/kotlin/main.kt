import com.soywiz.klock.milliseconds
import com.soywiz.korev.Key
import com.soywiz.korge.*
import com.soywiz.korge.input.*
import com.soywiz.korge.time.delay
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.RectangleInt
import com.soywiz.korma.geom.abs
import kotlin.math.abs

const val width = 512
const val height = 512
const val tileHeight = 32
const val tileWidth = 32

var control = true
var startFrame = 0
var dir = 0

suspend fun main() = Korge(
	width = 60 * tileWidth,
	height = 30 * tileHeight
) {
	init()

	/*val map = ArrayList<Pic>()
	for (i in 0..15) {
		for (j in 0..15) {
			map.add(Pic(tileHeight * j * 1.0, tileWidth * i * 1.0, TileType.GROUND))
		}
	}

	container {
		map.forEach {
			image(it.getPic()) {
				x = it.x
				y = it.y
			}
		}
	}*/

	val ground = container {
		for (i in 0..30) {
			for (j in 0..60) {
				val pic = Pic(tileHeight * j * 1.0, tileWidth * i * 1.0, TileType.GROUND)
				image(pic.getPic()) {
					x = pic.x
					y = pic.y
				}
			}
		}
	}

	val spriteMap = resourcesVfs["char.png"].readBitmap()
	val person = sprite(spriteMap.slice(RectangleInt(10, 0, 100, 100))).centerOn(this)

	val joystickBitmap = resourcesVfs["Controls.png"].readBitmap()
	image(joystickBitmap) {
		position(0.0, views.virtualHeightDouble - joystickBitmap.height + 1)
		onDown {
			val point = it.currentPosLocal
			when {
				point.x in 54.0..108.0 && point.y in 0.0..54.0 -> person.y -= 10
				point.x in 0.0..54.0 && point.y in 54.0..108.0 -> person.x -= 10
				point.x in 54.0..108.0 && point.y in 108.0..162.0 -> person.y += 10
				point.x in 108.0..162.0 && point.y in 54.0..108.0 -> person.x += 10
			}
		}
	}

	val buttonBitmap = resourcesVfs["button.png"].readBitmap()
	image(buttonBitmap) {
		position(
			views.virtualWidth  - buttonBitmap.width  * 1.5,
			views.virtualHeight - buttonBitmap.height * 1.5
		)
		onDown {
			person.playAnimation(CharMoves.SIT.animation, 200.milliseconds, startFrame = dir, endFrame = dir)
			person.stopAnimation()
		}
	}

	val tableMap = resourcesVfs["table.png"].readBitmap()

	val inventoryContainer = Container().xy(0, 0)
	inventoryContainer.addChild(
		roundRect(views.virtualWidth, views.virtualHeight, 0) {
			color = RGBA(0x00, 0x00, 0x00, 0x88)
		}
	)
//	inventoryContainer.addChild(
//		roundRect(100, 100, 0) {
//			color = RGBA(255, 0x00, 0x00, 0x88)
//			position(centerOn(inventoryContainer))
//		}
//	)
	inventoryContainer.addChild(image(tableMap) {
		scale(2.5, 2.2)
		centerXOn(this@Korge)
		centerYOn(this@Korge)
		text("")
	})
	for(i in 0..2){
		for(j in 0..6){
			inventoryContainer.addChild(
				roundRect(100, 100, 20) {
					color = RGBA(0x210, 0x210, 0x170, 0x88)
					position(260 + 110*j, 190 + 110*i)
				}
			)
		}
	}
	val groundContainer = Container()
	val swordBitmap = resourcesVfs["sword.png"].readBitmap()
	ground.addChild(image(swordBitmap) {
		position(
			views.virtualWidth/2 + 50,
			views.virtualHeight/2 + 50
		)
	})
	groundContainer.addTo(this@Korge)

	val Sword = Things(views.virtualWidth/2 + 50, views.virtualHeight/2 + 50, swordBitmap)

	val buttonGetBitmap = resourcesVfs["button.png"].readBitmap()
	image(buttonBitmap) {
		position(
			views.virtualWidth  - buttonBitmap.width  * 1.5,
			views.virtualHeight - buttonBitmap.height * 1.5 - 100
		)
		onDown {
			
			if((abs(Sword.xPos - person.x) < 100) && (abs(Sword.yPos - person.y) < 100)){
				//do
				removeChild(groundContainer)

				inventoryContainer.addChild(image(swordBitmap){
					position(centerOn(inventoryContainer))
				})


				//do
			}
		}
	}

//	inventoryContainer.addChild(uiProgressBar {//mega harosh
//		position(centerOn(inventoryContainer))
//		onDown {
//			for (i in 0..100) {
//				ratio = i * 0.01
//				this.updateState()
//				delay(50.milliseconds)
//			}
//			if (ratio == 1.0) {
//				println("true")
//				uiText("You are awesome!!!"){
//					xy(10, 10)
//					this.textColor = Colors.BLACK
//				}
//			}
//		}
//	})

//	inventoryContainer.addChild(uiCheckBox {
//		alignTopToTopOf(inventoryContainer, tableMap.height / 5 * 3)
//		centerXOn(inventoryContainer)
//		text = "You are awesome?"
//		textColor = Colors.DARKBLUE
//		onClick {
//			if (checked) {
//				println("Yea, you are!")
//			}
//		}
//	})


	uiButton(
		text = "Inventory"
	) {
		position(views.virtualWidth - UI_DEFAULT_WIDTH, 0.0)
		onDown {
			control = false
			inventoryContainer.addTo(this@Korge)
		}
	}

	person.onFrameChanged {
		person.stopAnimation()
	}

	keys {
		down(Key.DOWN)  {
			if (control) {
				person.playAnimation(
					CharMoves.DOWN.animation,
					startFrame = startFrame,
					endFrame = startFrame
				)
				changeFrame()
				dir = 0
//				person.y += 10
				ground.y -= 10
			}
		}
		down(Key.UP) {
			if (control) {
				person.playAnimation(
					CharMoves.UP.animation,
					startFrame = startFrame,
					endFrame = startFrame
				)
				changeFrame()
				dir = 1
//				person.y -= 10
				ground.y += 10
			}
		}
		down(Key.LEFT)  {
			if (control) {
				person.playAnimation(
					CharMoves.LEFT.animation,
					startFrame = startFrame,
					endFrame = startFrame
				)
				changeFrame()
				dir = 3
//				person.x -= 10
				ground.x += 10
			}
		}
		down(Key.RIGHT) {
			if (control) {
				person.playAnimation(
					CharMoves.RIGHT.animation,
					startFrame = startFrame,
					endFrame = startFrame
				)
				changeFrame()
				dir = 2
//				person.x += 10
				ground.x -= 10
			}
		}
		down(Key.X) {
			if (control) {
				person.playAnimation(CharMoves.SIT.animation, 200.milliseconds, startFrame = dir, endFrame = dir)
				person.stopAnimation()
			}
		}
		down(Key.ESCAPE) {
			if (!control) {
				inventoryContainer.removeFromParent()
				control = true
			}
		}
	}
}

suspend fun init() {
//	val bitmap = resourcesVfs["tilemap.png"].readBitmap()
	val ground = resourcesVfs["hehe.jpg"].readBitmap()

	val charSpriteMap = resourcesVfs["char.png"].readBitmap()

	val groundSlice: BitmapSlice<Bitmap> = ground.slice(RectangleInt(0 , 0, 32, 32))
//	val waterSlice : BitmapSlice<Bitmap> = bitmap.slice(RectangleInt(32, 0, 32, 32))

	TileType.GROUND.slice = groundSlice
//	TileType.WATER.slice  = waterSlice

	CharMoves.DOWN.animation = SpriteAnimation(
		spriteMap = charSpriteMap,
		spriteWidth = 100,
		spriteHeight = 100,
		marginLeft = 110,
		columns = 6,
		rows = 1
	)

	CharMoves.UP.animation = SpriteAnimation(
		spriteMap = charSpriteMap,
		spriteWidth = 100,
		spriteHeight = 100,
		marginTop = 100,
		marginLeft = 110,
		columns = 6,
		rows = 1
	)

	CharMoves.RIGHT.animation = SpriteAnimation(
		spriteMap = charSpriteMap,
		spriteWidth = 100,
		spriteHeight = 100,
		marginTop = 200,
		marginLeft = 110,
		columns = 6,
		rows = 1
	)

	CharMoves.LEFT.animation = SpriteAnimation(
		spriteMap = charSpriteMap,
		spriteWidth = 100,
		spriteHeight = 100,
		marginTop = 300,
		marginLeft = 110,
		columns = 6,
		rows = 1
	)

	CharMoves.SIT.animation = SpriteAnimation(
		spriteMap = charSpriteMap,
		spriteWidth = 100,
		spriteHeight = 100,
		marginLeft = 710,
		columns = 1,
		rows = 4
	)
}

fun changeFrame() {
	startFrame++
	if (startFrame >= 6) {
		startFrame = 0
	}
}