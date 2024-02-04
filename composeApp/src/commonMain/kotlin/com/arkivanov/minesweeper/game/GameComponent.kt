package com.arkivanov.minesweeper.game

import com.arkivanov.decompose.value.Value

internal interface GameComponent {

    val state: Value<State>

    fun onCellTouchedPrimary(x: Int, y: Int)
    fun onCellPressedSecondary(x: Int, y: Int)
    fun onCellTouchedTertiary(x: Int, y: Int)
    fun onCellReleased(x: Int, y: Int)
    fun onRestartClicked()
}
