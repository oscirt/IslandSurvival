package models

import kotlinx.serialization.Serializable

@Serializable
data class JsonCredentials(
    var username: String,
    var password: String
)