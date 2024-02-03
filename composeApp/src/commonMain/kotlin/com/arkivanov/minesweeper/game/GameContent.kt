package com.arkivanov.minesweeper.game

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

@Composable
internal fun GameContent(component: PreviewGameComponent, modifier: Modifier = Modifier) {
    Text(text = "Foo")
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
                        for (y in 0 until 20) {
                            val value =
                                when (x * y % 3) {
                                    0 -> CellValue.None
                                    1 -> CellValue.Mine
                                    else -> CellValue.Number(number = number % 10)
                                }

                            val status =
                                when (x * y % 2) {
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
