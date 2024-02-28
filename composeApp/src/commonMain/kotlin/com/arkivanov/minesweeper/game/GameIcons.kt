package com.arkivanov.minesweeper.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.DrawableResource
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
    val digits: Map<Char, Painter>,
)

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun gameIcons(): GameIcons =
    GameIcons(
        cellClosed = painterResource(DrawableResource("cell_closed.png")),
        cellClosedFlag = painterResource(DrawableResource("cell_closed_flag.png")),
        cellOpen = painterResource(DrawableResource("cell_open.png")),
        cellOpenMine = painterResource(DrawableResource("cell_open_mine.png")),
        cellOpenNumbers = buildMap {
            for (i in 1..8) {
                put(i, painterResource(DrawableResource("cell_open_$i.png")))
            }
        },
        smileFailed = painterResource(DrawableResource("smile_failed.png")),
        smileNormal = painterResource(DrawableResource("smile_normal.png")),
        smilePressed = painterResource(DrawableResource("smile_pressed.png")),
        smileWin = painterResource(DrawableResource("smile_win.png")),
        smileTrying = painterResource(DrawableResource("smile_trying.png")),
        digits = buildMap {
            for (i in '0'..'9') {
                put(i, painterResource(DrawableResource("digit_$i.png")))
            }
            put('-', painterResource(DrawableResource("digit_minus.png")))
        },
    )

internal val LocalGameIcons: ProvidableCompositionLocal<GameIcons?> =
    compositionLocalOf { null }

internal val CompositionLocal<GameIcons?>.icons: GameIcons
    @Composable
    get() = requireNotNull(current)
