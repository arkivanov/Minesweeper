package com.arkivanov.minesweeper.game

import com.arkivanov.decompose.value.Value

internal interface GameComponent {

    val state: Value<State>

    fun onCellPrimaryAction(x: Int, y: Int)
    fun onCellSecondaryAction(x: Int, y: Int)
}
