package com.arkivanov.minesweeper.root

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import com.arkivanov.minesweeper.game.GameComponent
import com.arkivanov.minesweeper.settings.EditSettingsComponent

internal interface RootComponent {

    val gameComponent: Value<GameComponent>
    val editSettingsComponent: Value<ChildSlot<*, EditSettingsComponent>>

    fun onEditSettingsClicked()
}
