package com.arkivanov.minesweeper.root

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.minesweeper.game.GameContent
import com.arkivanov.minesweeper.settings.EditSettingsContent

@Composable
internal fun RootContent(component: RootComponent) {
    val gameComponent by component.gameComponent.subscribeAsState()
    val editSettingsComponentSlot by component.editSettingsComponent.subscribeAsState()

    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Minesweeper") },
                actions = {
                    IconButton(onClick = component::onEditSettingsClicked) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                        )
                    }
                },
            )

            GameContent(component = gameComponent, modifier = Modifier.fillMaxSize())
        }

        editSettingsComponentSlot.child?.instance?.also { editSettingsComponent ->
            EditSettingsContent(component = editSettingsComponent)
        }
    }
}
