import com.soywiz.klock.milliseconds
import com.soywiz.korev.Key
import com.soywiz.korge.*
import com.soywiz.korge.input.*
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.RectangleInt

const val width = 512
const val height = 512
const val tileHeight = 32
const val tileWidth = 32

var startFrame = 0
var dir = 0

suspend fun main() = Korge(
	width = width,
	height = height
) {
	init()

	val map = ArrayList<Pic>()
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
	}

	val spriteMap = resourcesVfs["char.png"].readBitmap()
	val person = sprite(spriteMap.slice(RectangleInt(10, 0, 100, 100)))

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

	

	person.onFrameChanged {
		person.stopAnimation()
	}

	keys {
		down(Key.DOWN)  {
			person.y += 10
			person.playAnimation(
				CharMoves.DOWN.animation,
				startFrame = startFrame,
				endFrame = startFrame
			)
			changeFrame()
			dir = 0
		}
		down(Key.UP)    {
			person.y -= 10
			person.playAnimation(
				CharMoves.UP.animation,
				startFrame = startFrame,
				endFrame = startFrame
			)
			changeFrame()
			dir = 1
		}
		down(Key.LEFT)  {
			person.x -= 10
			person.playAnimation(
				CharMoves.LEFT.animation,
				startFrame = startFrame,
				endFrame = startFrame
			)
			changeFrame()
			dir = 3
		}
		down(Key.RIGHT) {
			person.x += 10
			person.playAnimation(
				CharMoves.RIGHT.animation,
				startFrame = startFrame,
				endFrame = startFrame
			)
			changeFrame()
			dir = 2
		}
		down(Key.X) {
			person.playAnimation(CharMoves.SIT.animation, 200.milliseconds, startFrame = dir, endFrame = dir)
			person.stopAnimation()
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