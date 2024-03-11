package com.arkivanov.minesweeper.game

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.minesweeper.asValue
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultGameComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    settings: GameSettings,
    coroutineContext: CoroutineContext
) : GameComponent, ComponentContext by componentContext {

    private val scope = coroutineScope(context = coroutineContext)

    private val store =
        instanceKeeper.getStore {
            storeFactory.gameStore(
                state = stateKeeper.consume(key = KEY_SAVED_STATE, strategy = GameState.serializer())
                    ?: newGameState(width = settings.width, height = settings.height, maxMines = settings.maxMines),
            )
        }

    override val state: Value<GameState> = store.asValue()

    init {
        stateKeeper.register(key = KEY_SAVED_STATE, strategy = GameState.serializer()) { store.state }
        scope.launch {
            store.stateFlow
                .map { it.gameStatus == GameStatus.STARTED }
                .distinctUntilChanged()
                .collectLatest { isStarted ->
                    if (isStarted) {
                        while (true) {
                            delay(1.seconds)
                            store.accept(Intent.TickTimer)
                        }
                    }
                }
        }
    }

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

    private companion object {
        private const val KEY_SAVED_STATE = "saved_state"
    }
}
