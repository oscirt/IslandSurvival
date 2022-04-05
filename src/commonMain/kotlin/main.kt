import character.CharMoves
import com.soywiz.klock.milliseconds
import com.soywiz.korev.Key
import com.soywiz.korge.*
import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.RectangleInt
import world.TileType
import world.World
import kotlin.math.abs

const val inventoryCell = 100

var control = true
var startFrame = 0
var dir = 0

suspend fun main() = Korge(
	width = 60 * 32,
	height = 30 * 32
) {
	init()

	val ground = Container()
	addChild(ground)

	//земля
	//херня какая то
	val world = World(ArrayList())
	world.generateWorld(60, 30)
	world.array.forEach {
		it.forEach {
			it.image.addTo(ground)
		}
	}


	//персонаж
	val spriteMap = resourcesVfs["char.png"].readBitmap()
	val person = sprite(spriteMap.slice(RectangleInt(10, 0, 100, 100))).centerOn(this)


	//инвентарь
	val tableImg = Image(resourcesVfs["table.png"].readBitmap()).scale(2.5, 2.2).centerOn(this@Korge)
	val inventoryContainer = Container().xy(0, 0)
	inventoryContainer.addChild(
		roundRect(views.virtualWidth, views.virtualHeight, 0) {
			color = RGBA(0x00, 0x00, 0x00, 0x88)
		}
	)
	inventoryContainer.addChild(tableImg)


	//заполнение инвентаря
	//вынести в отдельный файл
	//вынес
	val inventory = Inventory(arrayListOf(), 3, 7)
	for(i in 0 until inventory.rows){
		inventory.array.add(arrayListOf())
		for(j in 0 until inventory.cols) {
			inventory.array[i].add(InventoryCell(cell = roundRect(100.0, 100.0, 20.0) {
				color = RGBA(0x210, 0x210, 0x170, 0x88)
				x += (j * (inventoryCell + 20)) + 540
				y += (i * (inventoryCell  +20)) + 290
			}))
			inventoryContainer.addChild(inventory.array[i][j].cell)
		}
	}


	//мечики
	val sword = Thing(
		Image(resourcesVfs["sword.png"].readBitmap())
			.position(views.virtualWidth/2 + 100, views.virtualHeight/2 + 100))
	ground.addChild(sword.img)
	val sword2 = Thing(
		Image(resourcesVfs["sword.png"].readBitmap())
			.position(views.virtualWidth/2 - 100, views.virtualHeight/2 - 100))
	ground.addChild(sword2.img)


	//сидеть
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


	//геймпад
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


	//кнопка взятия предмета(меча, который на самом деле КИРКА)
	resourcesVfs["button.png"].readBitmap()
	image(buttonBitmap) {
		position(
			views.virtualWidth  - buttonBitmap.width  * 1.5,
			views.virtualHeight - buttonBitmap.height * 1.5 - 100
		)
		onDown {
			//не нравится куча выражений
			//жестко надо переработать
			println("${sword.img.globalX}   ${sword.img.globalY}")
			println("${person.x}   ${person.y}")
			if((abs(sword.img.globalX - person.x) < 100) && (abs(sword.img.globalY - person.y) < 100)) {
				ground.removeChild(sword.img)
				val index = inventory.getFreeCellIndex()
				val i = index / 3
				val j = index % 7
				if (index != -1) {
					inventory.array[i][j].thing = sword
					inventoryContainer.addChild(
						inventory.array[i][j].thing!!.img.xy(
							(j * (inventoryCell + 20)) + 574,
							((i * (inventoryCell + 20)) + 324))
					)
				}
			}
			if ((abs(sword2.img.globalX - person.x) < 100) && (abs(sword2.img.globalY - person.y) < 100)) {
				ground.removeChild(sword2.img)
				val index = inventory.getFreeCellIndex()
				val i = index / 3
				val j = index % 7
				if (index != -1) {
					inventory.array[i][j].thing = sword2
					inventoryContainer.addChild(
						inventory.array[i][j].thing!!.img.xy(
							(j * (inventoryCell + 20)) + 574,
							((i * (inventoryCell + 20)) + 324)
						)
					)
				}
			}
		}
	}


	//кнопка инвентаря
	uiButton(
		text = "Inventory"
	) {
		position(views.virtualWidth - UI_DEFAULT_WIDTH, 0.0)
		onDown {
			control = false
			inventoryContainer.addTo(this@Korge)
		}
	}


	//чето связанное с анимацией хз че это но работает не трогай пж
	person.onFrameChanged {
		person.stopAnimation()
	}


	//привязка ко кнопкам клавиатуры
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
	val bitmap = resourcesVfs["tilemap.png"].readBitmap()
	val ground = resourcesVfs["groundTexture.jpg"].readBitmap()
	val water  = resourcesVfs["waterTextureMinimised.png"].readBitmap()

	val charSpriteMap = resourcesVfs["char.png"].readBitmap()

	val groundSlice: BitmapSlice<Bitmap> = ground.slice(RectangleInt(0 , 0, 32, 32))
	val waterSlice : BitmapSlice<Bitmap> = water.slice(RectangleInt(0, 0, 32, 32))

	TileType.GROUND.slice = groundSlice
	TileType.WATER.slice  = waterSlice

	TileType.WATER.isWall = true

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