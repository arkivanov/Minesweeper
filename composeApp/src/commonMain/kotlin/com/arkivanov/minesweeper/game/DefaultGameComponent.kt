package com.arkivanov.minesweeper.game

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.minesweeper.asValue
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory

internal class DefaultGameComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
) : GameComponent, ComponentContext by componentContext {

    private val store =
        instanceKeeper.getStore {
            storeFactory.gameStore(newGameState(width = 10, height = 10, maxMines = 10))
        }

    override val state: Value<State> = store.asValue()

    override fun onCellPrimaryAction(x: Int, y: Int) {
        store.accept(Intent.RevealCell(x = x, y = y))
    }

    override fun onCellSecondaryAction(x: Int, y: Int) {
        store.accept(Intent.ToggleFlag(x = x, y = y))
    }

    override fun onCellTertiaryAction(x: Int, y: Int) {
        store.accept(Intent.RevealCellsAround(x = x, y = y))
    }
}
