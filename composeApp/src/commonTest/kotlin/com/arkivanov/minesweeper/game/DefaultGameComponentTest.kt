package com.arkivanov.minesweeper.game

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler

class DefaultGameComponentTest {
    private val lifecycle = LifecycleRegistry()
    private val componentContext = DefaultComponentContext(lifecycle = lifecycle)
    private val storeFactory = DefaultStoreFactory()

    private val coroutineScheduler = TestCoroutineScheduler()
    private val gameComponent = DefaultGameComponent(
        componentContext = componentContext,
        storeFactory = storeFactory,
        settings = GameSettings(),
        coroutineContext = StandardTestDispatcher(
            scheduler = coroutineScheduler
        )
    )

    // TODO: Use created properties, for write some integration tests
}