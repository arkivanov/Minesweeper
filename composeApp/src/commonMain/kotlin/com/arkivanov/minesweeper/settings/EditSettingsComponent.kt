package com.arkivanov.minesweeper.settings

import com.arkivanov.decompose.value.Value
import com.arkivanov.minesweeper.game.GameSettings

internal interface EditSettingsComponent {

    val model: Value<Model>

    fun onWidthChanged(text: String)
    fun onHeightChanged(text: String)
    fun onMaxMinesChanged(text: String)
    fun onConfirmClicked()
    fun onDismissRequested()

    data class Model(
        val width: String,
        val height: String,
        val maxMines: String,
    )

    fun interface Factory {
        operator fun invoke(
            settings: GameSettings,
            onConfirmed: (GameSettings) -> Unit,
            onCancelled: () -> Unit,
        ): EditSettingsComponent
    }
}
