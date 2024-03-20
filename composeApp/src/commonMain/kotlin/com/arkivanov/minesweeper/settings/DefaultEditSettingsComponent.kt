package com.arkivanov.minesweeper.settings

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.minesweeper.game.GameSettings
import com.arkivanov.minesweeper.settings.EditSettingsComponent.Model

private const val MIN_SIZE = 2
private const val MAX_WIDTH = 100
private const val MAX_HEIGHT = 50

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

        val finalWidth = width.coerceIn(MIN_SIZE..MAX_WIDTH)
        val finalHeight = height.coerceIn(MIN_SIZE..MAX_HEIGHT)
        val finalMines = maxMines.coerceIn(1 until finalWidth * finalHeight - 1)

        onConfirmed(GameSettings(width = finalWidth, height = finalHeight, maxMines = finalMines))
    }

    override fun onDismissRequested() {
        onDismissed()
    }
}
