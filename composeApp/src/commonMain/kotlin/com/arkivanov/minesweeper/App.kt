package com.arkivanov.minesweeper

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.minesweeper.game.GameContent
import com.arkivanov.minesweeper.root.RootComponent

@Composable
internal fun App(component: RootComponent) {
    MaterialTheme {
        GameContent(component = component.gameComponent, modifier = Modifier.fillMaxSize())
    }
}
