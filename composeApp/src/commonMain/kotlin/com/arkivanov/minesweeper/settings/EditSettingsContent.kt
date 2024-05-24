package com.arkivanov.minesweeper.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import minesweeper.composeapp.generated.resources.Res
import minesweeper.composeapp.generated.resources.apply
import minesweeper.composeapp.generated.resources.cancel
import minesweeper.composeapp.generated.resources.height
import minesweeper.composeapp.generated.resources.mine_count
import minesweeper.composeapp.generated.resources.width
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun EditSettingsContent(component: EditSettingsComponent) {
    val model by component.model.subscribeAsState()

    Dialog(onDismissRequest = component::onDismissRequested) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(space = 16.dp),
            ) {
                TextField(
                    value = model.width,
                    onValueChange = component::onWidthChanged,
                    label = { Text(text = stringResource(Res.string.width)) },
                    singleLine = true,
                )

                TextField(
                    value = model.height,
                    onValueChange = component::onHeightChanged,
                    label = { Text(text = stringResource(Res.string.height)) },
                    singleLine = true,
                )

                TextField(
                    value = model.maxMines,
                    onValueChange = component::onMaxMinesChanged,
                    label = { Text(text = stringResource(Res.string.mine_count)) },
                    singleLine = true,
                )

                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Button(onClick = component::onDismissRequested) {
                        Text(text = stringResource(Res.string.cancel))
                    }

                    Button(onClick = component::onConfirmClicked) {
                        Text(text = stringResource(Res.string.apply))
                    }
                }
            }
        }
    }
}
