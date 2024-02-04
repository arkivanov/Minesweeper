package com.arkivanov.minesweeper.game

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.minesweeper.onClick

private val cellSize = 32.dp

@Composable
internal fun GameContent(component: GameComponent, modifier: Modifier = Modifier) {
    val state by component.state.subscribeAsState()

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Row {
            repeat(state.width) { x ->
                Column(modifier = Modifier.width(cellSize)) {
                    repeat(state.height) { y ->
                        val cell = state.grid.getValue(x by y)
                        CellContent(
                            cell = cell,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(cellSize)
                                .onClick(
                                    onPrimaryClick = { component.onCellPrimaryAction(x = x, y = y) },
                                    onSecondaryClick = { component.onCellSecondaryAction(x = x, y = y) },
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CellContent(cell: Cell, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.border(width = 1.dp, color = Color.Gray).then(
            when (cell.status) {
                is CellStatus.Closed -> Modifier.background(Color.LightGray)
                is CellStatus.Open -> Modifier
            }
        ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = when (val status = cell.status) {
                is CellStatus.Closed -> if (status.isFlagged) "?" else ""

                is CellStatus.Open ->
                    when (val value = cell.value) {
                        is CellValue.None -> ""
                        is CellValue.Mine -> "X"
                        is CellValue.Number -> value.number.toString()
                    }
            },
        )
    }
}

@Preview
@Composable
internal fun GameContentPreview() {
    GameContent(component = PreviewGameComponent(), modifier = Modifier.fillMaxSize())
}

internal class PreviewGameComponent : GameComponent {
    override val state: Value<State> =
        MutableValue(
            State(
                grid = buildMap {
                    var number = 1
                    var isFlagged = false

                    for (x in 0 until 10) {
                        for (y in 0 until 10) {
                            val value =
                                when ((x + y) % 3) {
                                    0 -> CellValue.None
                                    1 -> CellValue.Mine
                                    else -> CellValue.Number(number = number % 10)
                                }

                            val status =
                                when ((x + y) % 2) {
                                    0 -> CellStatus.Closed(isFlagged = isFlagged)
                                    else -> CellStatus.Open
                                }

                            put(x by y, Cell(value = value, status = status))
                            number++
                            isFlagged = !isFlagged
                        }
                    }
                },
            )
        )

    override fun onCellPrimaryAction(x: Int, y: Int) {}
    override fun onCellSecondaryAction(x: Int, y: Int) {}
}
