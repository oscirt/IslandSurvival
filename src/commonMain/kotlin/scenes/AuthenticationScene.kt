@file:OptIn(KorgeExperimental::class)

package scenes

import action_ui.padding
import character.Character
import character.chooseAnimation
import com.soywiz.kds.Stack
import com.soywiz.klock.milliseconds
import com.soywiz.korge.annotations.KorgeExperimental
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.tiled.objects
import com.soywiz.korge.tiled.readTiledMap
import com.soywiz.korge.ui.UIButton
import com.soywiz.korge.ui.UITextInput
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.ui.uiTextInput
import com.soywiz.korge.view.*
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.async.launch
import com.soywiz.korio.dynamic.KDynamic.Companion.toInt
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.krypto.sha256
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.core.*
import io.ktor.websocket.*
import model.Point
import player_data.startPosition
import serialization.Serialization

const val authPadding = 10.0

const val urlRegistration = "https://korge-server.herokuapp.com/register"
const val urlLogin = "https://korge-server.herokuapp.com/login"
//const val urlRegistration = "http://localhost:8080/register"
//const val urlLogin = "http://localhost:8080/login"

lateinit var username: UITextInput
lateinit var password: UITextInput
lateinit var loginButton: UIButton
lateinit var signUpButton: UIButton
lateinit var infoText: Text

var characterName = ""
var isOnline = false

lateinit var client: HttpClient
lateinit var session: DefaultWebSocketSession

val playersContainer = mutableMapOf<String, Character>()
val needToAttach = Stack<Point>()

class AuthenticationScene : Scene() {
    override suspend fun Container.sceneInit() {
        tiledMap = resourcesVfs["Island.tmx"].readTiledMap()
        //double reading tiledMapData
        tiledMap.data.objectLayers.objects.forEach{
            for ((key, value) in it.objectsByType) {
                if (key == "start") {
                    startPosition = value.first().bounds.position
                    startPosition.x -= padding.x
                    startPosition.y -= padding.y
                }
            }
        }
        val backgroundBitmap = resourcesVfs["backgroundOnlineMenu.jpg"].readBitmap()
        image(backgroundBitmap) {
            scaledWidth = 640.0
            scaledHeight = 360.0
        }

        client = HttpClient {
            install(WebSockets)
            install(ContentNegotiation) {
                json()
            }
        }

        val cont = container {
            username = uiTextInput(width = views.virtualWidthDouble / 3)
            password = uiTextInput(width = views.virtualWidthDouble / 3) {
                y = username.height + authPadding

            }
            loginButton = uiButton {
                text = "Login"
                alignTopToTopOf(password, username.height + authPadding)
            }
            signUpButton = uiButton {
                text = "Sign Up"
                alignRightToRightOf(password)
                alignTopToTopOf(password, password.height + authPadding)
            }
            infoText = text("") {
                alignTopToBottomOf(loginButton, authPadding)
                alignLeftToLeftOf(loginButton)
            }
        }
        cont.centerXOn(this)
    }

    override suspend fun Container.sceneMain() {
        loginButton.onClick {
            characterName = username.text
            val response = client.post(urlLogin) {
                setBody("""{"username":"${username.text}","password":"${password.text.toByteArray().sha256().base64}"}""")
            }
            when (response.status) {
                HttpStatusCode.OK -> {
                    client.ws(
                        method = HttpMethod.Get,
                        host = "korge-server.herokuapp.com",
                        path = "/game"
                    ) {
                        val incomingJob = launch {
                            try {
                                session = this
                                isOnline = true
                                sceneContainer.changeTo<GameScene>()
                                val thisPoint = Point(username.text, startPosition.x,
                                    startPosition.y, 4)
                                this.send(Serialization.getJsonFromPoint(thisPoint))
                                for (message in incoming) {
                                    when (message) {
                                        is Frame.Binary -> TODO()
                                        is Frame.Text -> {
                                            val text = message.readText()
                                            if (text[0] != '{') {
                                                println(text)
                                                val i = objects.find{ it.id == text.toInt()}!!
                                                println(i)
                                                i.sprite.removeFromParent()
                                                continue
                                            }
                                            val receivedPoint = Serialization.getPointFromJson(text)
                                            if (playersContainer.containsKey(receivedPoint.name)) {
                                                playersContainer[receivedPoint.name]!!.updateCharacter(receivedPoint)
                                            } else if (!needToAttach.contains(receivedPoint)) {
                                                println(needToAttach)
                                                needToAttach.push(receivedPoint)
                                            }
                                        }
                                        is Frame.Close -> TODO()
                                        is Frame.Ping -> TODO()
                                        is Frame.Pong -> TODO()
                                        else -> TODO()
                                    }
                                }
                            } catch (e: Error) {
                                println("Error while receiving message: $e")
                            } finally {
                                println("djfslkjsdflkjsdf")
                            }
                        }
                        incomingJob.join()
                    }
                }
                HttpStatusCode.NotFound -> {
                    infoText.text = "User not found"
                }
                else -> {
                    println(response.status)
                }
            }
        }
        signUpButton.onClick {
            val response = client.post(urlRegistration) {
                setBody("""{"username":"${username.text}","password":"${password.text.toByteArray().sha256().base64}"}""")
            }
            if (response.status == HttpStatusCode.OK) {
                infoText.text = "Created User\nPlease login"
            } else {
                println(response.status)
            }
        }
    }
}