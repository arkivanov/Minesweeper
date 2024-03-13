package com.arkivanov.minesweeper.game

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

@Suppress("TestFunctionName")
class DefaultGameComponentTest {
    private val lifecycle = LifecycleRegistry()
    private val coroutineScheduler = TestCoroutineScheduler()
    private val gameComponent = DefaultGameComponent(
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
        storeFactory = DefaultStoreFactory(),
        settings = GameSettings(width = 20, height = 20, maxMines = 200),
        mainCoroutineContext = StandardTestDispatcher(scheduler = coroutineScheduler)
    )

    private val state: GameState
        get() = gameComponent.state.value

    @BeforeTest
    fun before() {
        lifecycle.resume()
    }

    @Test
    fun GIVEN_created_WHEN_cell_clicked_THEN_status_STARTED() {
        clickCellPrimary(0 by 0)

        assertEquals(GameStatus.STARTED, state.gameStatus)
    }

    @Test
    fun GIVEN_created_WHEN_cell_clicked_THEN_timer_shows_0() {
        clickCellPrimary(0 by 0)

        assertEquals(0, state.timer)
    }

    @Test
    fun GIVEN_game_started_WHEN_one_second_passed_THEN_timer_shows_1() {
        clickCellPrimary(0 by 0)

        coroutineScheduler.advanceTimeBy(1.seconds)
        coroutineScheduler.runCurrent()

        assertEquals(1, state.timer)
    }

    @Test
    fun GIVEN_game_started_WHEN_click_on_mine_cell_THEN_timer_STOPS() {
        clickCellPrimary(0 by 0)

        coroutineScheduler.advanceTimeBy(10.seconds)
        coroutineScheduler.runCurrent()

        val (mineLocation) = state.grid.entries.first { (_, cell) -> cell.value.isMine }
        clickCellPrimary(mineLocation)

        coroutineScheduler.advanceTimeBy(20.seconds)
        coroutineScheduler.runCurrent()

        assertEquals(10, state.timer)
    }

    @Test
    fun GIVEN_game_started_WHEN_win_THEN_timer_STOPS() {
        clickCellPrimary(0 by 0)

        coroutineScheduler.advanceTimeBy(25.seconds)
        coroutineScheduler.runCurrent()

        state.grid.filterValues { it.status.isClosed && !it.value.isMine }.forEach { (location, _) ->
            clickCellPrimary(location)
        }

        coroutineScheduler.advanceTimeBy(50.seconds)
        coroutineScheduler.runCurrent()

        assertEquals(25, state.timer)
    }

    private fun clickCellPrimary(location: Location) {
        gameComponent.onCellTouchedPrimary(x = location.x, y = location.y)
        gameComponent.onCellReleased(x = location.x, y = location.y)
    }
}
