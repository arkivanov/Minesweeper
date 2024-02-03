package com.arkivanov.minesweeper

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.minesweeper.game.GameComponent
import com.arkivanov.minesweeper.game.GameContent

@Composable
internal fun App(component: GameComponent) {
    MaterialTheme {
        GameContent(component = component, modifier = Modifier.fillMaxSize())
    }
}
