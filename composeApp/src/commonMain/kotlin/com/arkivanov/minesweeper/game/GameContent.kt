package com.arkivanov.minesweeper.game

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
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
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.isTertiaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

private val cellSize = 32.dp

@Composable
internal fun GameContent(component: GameComponent, modifier: Modifier = Modifier) {
    val state by component.state.subscribeAsState()

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Row(
            modifier = Modifier.touchHandler(
                gridWidth = state.width,
                gridHeight = state.height,
                onPrimaryTouched = component::onCellTouchedPrimary,
                onSecondaryPressed = component::onCellPressedSecondary,
                onTertiaryTouched = component::onCellTouchedTertiary,
                onReleased = component::onCellReleased,
            ),
        ) {
            repeat(state.width) { x ->
                Column(modifier = Modifier.width(cellSize)) {
                    repeat(state.height) { y ->
                        CellContent(
                            cell = state.grid.getValue(x by y),
                            modifier = Modifier.fillMaxWidth().height(cellSize),
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
            when (val status = cell.status) {
                is CellStatus.Closed -> if (status.isPressed) Modifier else Modifier.background(Color.LightGray)
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

private fun Modifier.touchHandler(
    gridWidth: Int,
    gridHeight: Int,
    onPrimaryTouched: (cellX: Int, cellY: Int) -> Unit,
    onSecondaryPressed: (cellX: Int, cellY: Int) -> Unit,
    onTertiaryTouched: (cellX: Int, cellY: Int) -> Unit,
    onReleased: (cellX: Int, cellY: Int) -> Unit,
): Modifier =
    pointerInput(gridWidth, gridHeight) {
        val cellWidth = size.width.toFloat() / gridWidth.toFloat()
        val cellHeight = size.height.toFloat() / gridHeight.toFloat()

        awaitEachGesture {
            while (true) {
                val event = awaitPointerEvent()
                val change = event.changes.last()
                val offset = change.position
                val cellX = (offset.x / cellWidth).toInt().coerceIn(0 until gridWidth)
                val cellY = (offset.y / cellHeight).toInt().coerceIn(0 until gridHeight)
                val isPrimaryPressed = event.buttons.isPrimaryPressed
                val isSecondaryPressed = event.buttons.isSecondaryPressed
                val isTertiaryPressed = event.buttons.isTertiaryPressed

                if (change.pressed) {
                    if (isPrimaryPressed && !isSecondaryPressed && !isTertiaryPressed) {
                        onPrimaryTouched(cellX, cellY)
                    } else if (!isPrimaryPressed && isSecondaryPressed && !isTertiaryPressed && change.changedToDown()) {
                        onSecondaryPressed(cellX, cellY)
                    } else if (!isPrimaryPressed && !isSecondaryPressed && isTertiaryPressed) {
                        onTertiaryTouched(cellX, cellY)
                    } else if (isPrimaryPressed && isSecondaryPressed && !isTertiaryPressed) {
                        onTertiaryTouched(cellX, cellY)
                    }
                } else {
                    onReleased(cellX, cellY)
                }
            }
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

    override fun onCellTouchedPrimary(x: Int, y: Int) {}
    override fun onCellPressedSecondary(x: Int, y: Int) {}
    override fun onCellTouchedTertiary(x: Int, y: Int) {}
    override fun onCellReleased(x: Int, y: Int) {}
}
