package com.arkivanov.minesweeper.game

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.isTertiaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

private val cellSize = 16.dp

@Composable
internal fun GameContent(component: GameComponent, modifier: Modifier = Modifier) {
    val state by component.state.subscribeAsState()
    val gameStatus by derivedStateOf { state.gameStatus }
    val pressMode by derivedStateOf { state.pressMode }
    val gridWidth by derivedStateOf { state.width }
    val gridHeight by derivedStateOf { state.height }
    val grid by derivedStateOf { state.grid }

    CompositionLocalProvider(LocalGameIcons provides gameIcons()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                RestartButton(
                    isWin = gameStatus == GameStatus.WIN,
                    isFailed = gameStatus == GameStatus.FAILED,
                    isTrying = pressMode != PressMode.NONE,
                    onClick = component::onRestartClicked,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.touchHandler(
                        gridWidth = gridWidth,
                        gridHeight = gridHeight,
                        onPrimaryTouched = component::onCellTouchedPrimary,
                        onSecondaryPressed = component::onCellPressedSecondary,
                        onTertiaryTouched = component::onCellTouchedTertiary,
                        onReleased = component::onCellReleased,
                    ),
                ) {
                    repeat(gridWidth) { x ->
                        Column(modifier = Modifier.width(cellSize)) {
                            repeat(gridHeight) { y ->
                                CellContent(
                                    cell = grid.getValue(x by y),
                                    modifier = Modifier.fillMaxWidth().height(cellSize),
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ControlsInfo()
            }
        }
    }
}

@Composable
private fun CellContent(cell: Cell, modifier: Modifier = Modifier) {
    Image(
        painter = cell.painter(),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.FillBounds,
    )
}

@Composable
private fun RestartButton(
    isWin: Boolean,
    isFailed: Boolean,
    isTrying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val smileInteractionSource = remember { MutableInteractionSource() }
    var isPressed by remember { mutableStateOf(false) }

    LaunchedEffect(smileInteractionSource) {
        smileInteractionSource.interactions.collect { interaction ->
            isPressed = interaction is PressInteraction.Press
        }
    }

    Image(
        painter = when {
            isWin -> LocalGameIcons.icons.smileWin
            isPressed -> LocalGameIcons.icons.smilePressed
            isFailed -> LocalGameIcons.icons.smileFailed
            isTrying -> LocalGameIcons.icons.smileTrying
            else -> LocalGameIcons.icons.smileNormal
        },
        contentDescription = "Restart",
        modifier = modifier
            .size(26.dp)
            .clickable(
                interactionSource = smileInteractionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            ),
    )
}

@Composable
private fun ControlsInfo(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Left click: ",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.body2,
            )

            Text(text = "dig a cell", style = MaterialTheme.typography.body2)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Right click (or Ctrl + Left click): ",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.body2,
            )

            Text(text = "flag a cell", style = MaterialTheme.typography.body2)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Middle click (or Left + Right click, or Shift + Left click): ",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.body2,
            )

            Text(text = "dig all adjacent cells", style = MaterialTheme.typography.body2)
        }
    }
}

@Composable
private fun Cell.painter(): Painter =
    when (status) {
        is CellStatus.Closed ->
            when {
                status.isFlagged -> LocalGameIcons.icons.cellClosedFlag
                status.isPressed -> LocalGameIcons.icons.cellOpen
                else -> LocalGameIcons.icons.cellClosed
            }

        is CellStatus.Open ->
            when (value) {
                is CellValue.None -> LocalGameIcons.icons.cellOpen
                is CellValue.Mine -> LocalGameIcons.icons.cellOpenMine
                is CellValue.Number -> LocalGameIcons.icons.cellOpenNumbers.getValue(value.number)
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
    composed {
        var isShiftPressed by remember { mutableStateOf(false) }
        var isCtrlPressed by remember { mutableStateOf(false) }
        val focusRequester = remember { FocusRequester() }

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
                    val isChangedToDown = change.changedToDown()
                    val isPrimaryPressed = event.buttons.isPrimaryPressed
                    val isSecondaryPressed = event.buttons.isSecondaryPressed
                    val isTertiaryPressed = event.buttons.isTertiaryPressed
                    val isPrimary = isPrimaryPressed && !isSecondaryPressed && !isTertiaryPressed
                    val isSecondary = !isPrimaryPressed && isSecondaryPressed && !isTertiaryPressed
                    val isTertiary = !isPrimaryPressed && !isSecondaryPressed && isTertiaryPressed
                    val isPrimaryAndSecondary = isPrimaryPressed && isSecondaryPressed && !isTertiaryPressed

                    focusRequester.requestFocus()

                    if (!change.pressed || (event.type == PointerEventType.Release)) {
                        onReleased(cellX, cellY)
                    } else if (isCtrlPressed) {
                        if ((isPrimary || isSecondary) && isChangedToDown) {
                            onSecondaryPressed(cellX, cellY)
                        }
                    } else if (isShiftPressed) {
                        if (isPrimary) {
                            onTertiaryTouched(cellX, cellY)
                        }
                    } else if (isPrimary) {
                        onPrimaryTouched(cellX, cellY)
                    } else if (isSecondary && isChangedToDown) {
                        onSecondaryPressed(cellX, cellY)
                    } else if (isTertiary || isPrimaryAndSecondary) {
                        onTertiaryTouched(cellX, cellY)
                    }
                }
            }
        }
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent {
                when (it.key) {
                    Key.ShiftLeft -> isShiftPressed = it.type == KeyEventType.KeyDown
                    Key.CtrlLeft -> isCtrlPressed = it.type == KeyEventType.KeyDown
                }
                false
            }
    }

@Preview
@Composable
internal fun GameContentPreview() {
    GameContent(component = PreviewGameComponent(), modifier = Modifier.fillMaxSize())
}

internal class PreviewGameComponent : GameComponent {
    override val state: Value<GameState> =
        MutableValue(
            GameState(
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
    override fun onRestartClicked() {}
}