package com.arkivanov.minesweeper.game

import kotlinx.serialization.Serializable

@Serializable
internal data class GameSettings(
    val width: Int = 20,
    val height: Int = 20,
    val maxMines: Int = 30,
)
