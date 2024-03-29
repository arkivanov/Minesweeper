package com.arkivanov.minesweeper.game

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value

internal interface GameComponent {

    val state: Value<GameState>

    fun onCellTouchedPrimary(x: Int, y: Int)
    fun onCellPressedSecondary(x: Int, y: Int)
    fun onCellTouchedTertiary(x: Int, y: Int)
    fun onCellReleased(x: Int, y: Int)
    fun onRestartClicked()

    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext, settings: GameSettings): GameComponent
    }
}
