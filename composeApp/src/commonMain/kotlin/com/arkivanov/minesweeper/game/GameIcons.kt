package com.arkivanov.minesweeper.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

internal data class GameIcons(
    val cellClosed: Painter,
    val cellClosedFlag: Painter,
    val cellOpen: Painter,
    val cellOpenMine: Painter,
    val cellOpenNumbers: Map<Int, Painter>,
    val smileFailed: Painter,
    val smileNormal: Painter,
    val smilePressed: Painter,
    val smileWin: Painter,
    val smileTrying: Painter,
)

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun gameIcons(): GameIcons =
    GameIcons(
        cellClosed = painterResource("cell_closed.png"),
        cellClosedFlag = painterResource("cell_closed_flag.png"),
        cellOpen = painterResource("cell_open.png"),
        cellOpenMine = painterResource("cell_open_mine.png"),
        cellOpenNumbers = buildMap {
            for (i in 1..8) {
                put(i, painterResource("cell_open_$i.png"))
            }
        },
        smileFailed = painterResource("smile_failed.png"),
        smileNormal = painterResource("smile_normal.png"),
        smilePressed = painterResource("smile_pressed.png"),
        smileWin = painterResource("smile_win.png"),
        smileTrying = painterResource("smile_trying.png"),
    )

internal val LocalGameIcons: ProvidableCompositionLocal<GameIcons?> =
    compositionLocalOf { null }

internal val CompositionLocal<GameIcons?>.icons: GameIcons
    @Composable
    get() = requireNotNull(current)
