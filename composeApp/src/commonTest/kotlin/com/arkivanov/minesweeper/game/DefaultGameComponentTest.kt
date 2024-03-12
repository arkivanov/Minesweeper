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
        settings = GameSettings(width = 20, height = 20, maxMines = 398),
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
        clickCellPrimary(x = 0, y = 0)

        assertEquals(GameStatus.STARTED, state.gameStatus)
    }

    @Test
    fun GIVEN_created_WHEN_cell_clicked_THEN_timer_shows_0() {
        clickCellPrimary(x = 0, y = 0)

        assertEquals(0, state.timer)
    }

    @Test
    fun GIVEN_game_started_WHEN_one_second_passed_THEN_timer_shows_1() {
        clickCellPrimary(x = 0, y = 0)

        coroutineScheduler.advanceTimeBy(1.seconds)
        coroutineScheduler.runCurrent()

        assertEquals(1, state.timer)
    }

    @Test
    fun GIVEN_game_started_WHEN_click_on_mine_cell_THEN_timer_STOPS() {
        clickCellPrimary(x = 0, y = 0)

        coroutineScheduler.advanceTimeBy(10.seconds)
        coroutineScheduler.runCurrent()

        clickCellPrimary(x = 10, y = 10)

        coroutineScheduler.advanceTimeBy(10.seconds)
        coroutineScheduler.runCurrent()

        assertEquals(10, state.timer)
    }

    private fun clickCellPrimary(x: Int, y: Int) {
        gameComponent.onCellTouchedPrimary(x = x, y = y)
        gameComponent.onCellReleased(x = y, y = y)
    }
}
