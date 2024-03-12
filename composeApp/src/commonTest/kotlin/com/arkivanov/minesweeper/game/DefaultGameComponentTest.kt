package com.arkivanov.minesweeper.game

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("TestFunctionName")
class DefaultGameComponentTest {
    private val lifecycle = LifecycleRegistry()
    private val coroutineScheduler = TestCoroutineScheduler()
    private val gameComponent = DefaultGameComponent(
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
        storeFactory = DefaultStoreFactory(),
        settings = GameSettings(),
        mainCoroutineContext = StandardTestDispatcher(scheduler = coroutineScheduler)
    )

    @BeforeTest
    fun before() {
        lifecycle.resume()
    }

    @Test
    fun WHEN_created_THEN_stopwatch_on_START() {
        val gameState = gameComponent.state.value
        assertEquals(0, gameState.timer)
    }

    // TODO: Write more tests (need a bit dive in to work with TestCoroutineScheduler
}