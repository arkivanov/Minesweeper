package com.arkivanov.minesweeper

import com.arkivanov.essenty.statekeeper.SerializableContainer
import kotlinx.serialization.json.Json

private val json =
    Json {
        allowStructuredMapKeys = true
    }

internal fun SerializableContainer.encodeToString(): String =
    json.encodeToString(SerializableContainer.serializer(), this)

internal fun String.decodeSerializableContainer(): SerializableContainer? =
    try {
        json.decodeFromString(SerializableContainer.serializer(), this)
    } catch (e: Exception) {
        null
    }
