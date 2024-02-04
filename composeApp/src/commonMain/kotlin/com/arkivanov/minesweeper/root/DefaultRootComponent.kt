package com.arkivanov.minesweeper.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.minesweeper.game.DefaultGameComponent
import com.arkivanov.minesweeper.game.GameComponent
import com.arkivanov.mvikotlin.core.store.StoreFactory

internal class DefaultRootComponent(
    componentContext: ComponentContext,
    gameComponentFactory: GameComponent.Factory,
) : RootComponent, ComponentContext by componentContext {

    override val gameComponent: GameComponent =
        gameComponentFactory(
            componentContext = childContext(key = "game"),
            width = 10,
            height = 10,
            maxMines = 10,
        )
}

internal fun DefaultRootComponent(componentContext: ComponentContext, storeFactory: StoreFactory): DefaultRootComponent =
    DefaultRootComponent(
        componentContext = componentContext,
        gameComponentFactory = { ctx, width, height, maxMines ->
            DefaultGameComponent(
                componentContext = ctx,
                storeFactory = storeFactory,
                width = width,
                height = height,
                maxMines = maxMines,
            )
        },
    )
