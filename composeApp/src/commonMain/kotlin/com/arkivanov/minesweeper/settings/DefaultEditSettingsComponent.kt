package com.arkivanov.minesweeper.settings

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.minesweeper.game.GameSettings
import com.arkivanov.minesweeper.settings.EditSettingsComponent.Model
import kotlin.math.min

internal class DefaultEditSettingsComponent(
    settings: GameSettings,
    private val onConfirmed: (GameSettings) -> Unit,
    private val onDismissed: () -> Unit,
) : EditSettingsComponent {

    private val _model =
        MutableValue(
            Model(
                width = settings.width.toString(),
                height = settings.height.toString(),
                maxMines = settings.maxMines.toString(),
            )
        )

    override val model: Value<Model> = _model

    override fun onWidthChanged(text: String) {
        _model.update { it.copy(width = text) }
    }

    override fun onHeightChanged(text: String) {
        _model.update { it.copy(height = text) }
    }

    override fun onMaxMinesChanged(text: String) {
        _model.update { it.copy(maxMines = text) }
    }

    override fun onConfirmClicked() {
        val width = _model.value.width.toIntOrNull() ?: return
        val height = _model.value.height.toIntOrNull() ?: return
        val maxMines = _model.value.maxMines.toIntOrNull() ?: return

        onConfirmed(
            GameSettings(
                width = width,
                height = height,
                maxMines = min(maxMines, width * height),
            )
        )
    }

    override fun onDismissRequested() {
        onDismissed()
    }
}
