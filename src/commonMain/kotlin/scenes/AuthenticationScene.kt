@file:OptIn(KorgeExperimental::class)

package scenes

import com.soywiz.korge.annotations.KorgeExperimental
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.UIButton
import com.soywiz.korge.ui.UITextInput
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.ui.uiTextInput
import com.soywiz.korge.view.*
import com.soywiz.krypto.sha256
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.Credentials

const val authPadding = 10.0

const val  url = "https://localhost:8080"

lateinit var username: UITextInput
lateinit var password: UITextInput
lateinit var loginButton: UIButton
lateinit var signUpButton: UIButton
lateinit var infoText: Text

lateinit var client: HttpClient

class AuthenticationScene() : Scene() {
    override suspend fun Container.sceneInit() {
        client = HttpClient()

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
        centerXOn(FixedSizeContainer(640.0, 360.0))
    }

    override suspend fun Container.sceneMain() {
        loginButton.onClick {
            val name = username.text
            val pass = password.text.toByteArray().sha256().base64
            val response = client.post(url) {
                setBody("""{"username":$name, "password": "$pass"}""")
            }
            println(response.status)
        }
    }
}
