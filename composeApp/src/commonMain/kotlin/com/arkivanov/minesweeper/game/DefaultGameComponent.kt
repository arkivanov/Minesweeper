package com.arkivanov.minesweeper.game

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.minesweeper.asValue
import com.arkivanov.minesweeper.game.stopwatch.StopwatchViewModel
import com.arkivanov.minesweeper.game.stopwatch.utils.provideStopwatchStateHolder
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

internal class DefaultGameComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    settings: GameSettings,
) : GameComponent, ComponentContext by componentContext {

    private val store =
        instanceKeeper.getStore {
            storeFactory.gameStore(
                state = stateKeeper.consume(key = KEY_SAVED_STATE, strategy = GameState.serializer())
                    ?: newGameState(width = settings.width, height = settings.height, maxMines = settings.maxMines),
            )
        }

    override val state: Value<GameState> = store.asValue()

    // TODO: Stuck & Overwhelmed: Should to hoist scope / stopwatchViewModel ?
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    override val stopwatchViewModel =
        StopwatchViewModel(stopwatchStateHolder = provideStopwatchStateHolder(), scope = scope)

    init {
        stateKeeper.register(key = KEY_SAVED_STATE, strategy = GameState.serializer()) { store.state }
    }

    override fun onCellTouchedPrimary(x: Int, y: Int) {
        store.accept(Intent.PressCell(x = x, y = y)).also {
            stopwatchViewModel.start()
        }
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
        store.accept(Intent.Restart).also {
            stopwatchViewModel.stop()
        }
    }

    private companion object {
        private const val KEY_SAVED_STATE = "saved_state"
    }
}
