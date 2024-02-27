package com.arkivanov.minesweeper.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.children.SimpleNavigation
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.Value
import com.arkivanov.minesweeper.child
import com.arkivanov.minesweeper.game.DefaultGameComponent
import com.arkivanov.minesweeper.game.GameComponent
import com.arkivanov.minesweeper.game.GameSettings
import com.arkivanov.minesweeper.settings.DefaultEditSettingsComponent
import com.arkivanov.minesweeper.settings.EditSettingsComponent
import com.arkivanov.mvikotlin.core.store.StoreFactory

internal class DefaultRootComponent(
    componentContext: ComponentContext,
    gameComponentFactory: GameComponent.Factory,
    editSettingsComponentFactory: EditSettingsComponent.Factory,
) : RootComponent, ComponentContext by componentContext {

    private var settings = GameSettings()

    private val gameNav = SimpleNavigation<GameSettings>()
    override val gameComponent: Value<GameComponent> =
        child(
            source = gameNav,
            serializer = GameSettings.serializer(),
            initialConfiguration = { settings },
            childFactory = { settings, ctx ->
                gameComponentFactory(
                    componentContext = ctx,
                    settings = settings
                )
            },
        )

    private val editSettingsNav = SlotNavigation<GameSettings>()
    override val editSettingsComponent: Value<ChildSlot<*, EditSettingsComponent>> =
        childSlot(
            source = editSettingsNav,
            serializer = null,
            childFactory = { settings, _ ->
                editSettingsComponentFactory(
                    settings = settings,
                    onConfirmed = {
                        this.settings = it
                        editSettingsNav.dismiss()
                        gameNav.navigate(it)
                    },
                    onCancelled = editSettingsNav::dismiss,
                )
            },
        )

    override fun onEditSettingsClicked() {
        editSettingsNav.activate(settings)
    }
}

internal fun DefaultRootComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory
): DefaultRootComponent = DefaultRootComponent(
    componentContext = componentContext,
    gameComponentFactory = { ctx, settings ->
        DefaultGameComponent(
            componentContext = ctx,
            storeFactory = storeFactory,
            settings = settings
        )
    },
    editSettingsComponentFactory = ::DefaultEditSettingsComponent,
)
