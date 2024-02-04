package com.arkivanov.minesweeper.game

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.minesweeper.asValue
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory

internal class DefaultGameComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    settings: GameSettings,
) : GameComponent, ComponentContext by componentContext {

    private val store =
        instanceKeeper.getStore {
            storeFactory.gameStore(
                newGameState(
                    width = settings.width,
                    height = settings.height,
                    maxMines = settings.maxMines,
                )
            )
        }

    override val state: Value<State> = store.asValue()

    override fun onCellTouchedPrimary(x: Int, y: Int) {
        store.accept(Intent.PressCell(x = x, y = y))
    }

    override fun onCellPressedSecondary(x: Int, y: Int) {
        store.accept(Intent.ToggleFlag(x = x, y = y))
    }

    override fun onCellTouchedTertiary(x: Int, y: Int) {
        store.accept(Intent.PressCells(x = x, y = y))
    }

    override fun onCellReleased(x: Int, y: Int) {
        store.accept(Intent.ReleaseCells(x = x, y = y))
    }

    override fun onRestartClicked() {
        store.accept(Intent.Restart)
    }
}
