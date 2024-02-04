package com.arkivanov.minesweeper.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.minesweeper.game.DefaultGameComponent
import com.arkivanov.minesweeper.game.GameComponent
import com.arkivanov.minesweeper.game.GameSettings
import com.arkivanov.mvikotlin.core.store.StoreFactory

internal class DefaultRootComponent(
    componentContext: ComponentContext,
    gameComponentFactory: GameComponent.Factory,
) : RootComponent, ComponentContext by componentContext {

    override val gameComponent: GameComponent =
        gameComponentFactory(
            componentContext = childContext(key = "game"),
            settings = GameSettings(width = 20, height = 20, maxMines = 30),
        )
}

internal fun DefaultRootComponent(componentContext: ComponentContext, storeFactory: StoreFactory): DefaultRootComponent =
    DefaultRootComponent(
        componentContext = componentContext,
        gameComponentFactory = { ctx, settings ->
            DefaultGameComponent(componentContext = ctx, storeFactory = storeFactory, settings = settings)
        },
    )
