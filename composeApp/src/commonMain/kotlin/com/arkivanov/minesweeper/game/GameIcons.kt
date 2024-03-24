package com.arkivanov.minesweeper.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.painter.Painter
import minesweeper.composeapp.generated.resources.Res
import minesweeper.composeapp.generated.resources.*
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
        cellClosed = painterResource(Res.drawable.cell_closed),
        cellClosedFlag = painterResource(Res.drawable.cell_closed_flag),
        cellOpen = painterResource(Res.drawable.cell_open),
        cellOpenMine = painterResource(Res.drawable.cell_open_mine),
        cellOpenNumbers = buildMap {
            for (i in 1..8) {
                put(i, painterResource(DrawableResource("cell_open_$i.png")))
            }
        },
        smileFailed = painterResource(Res.drawable.smile_failed),
        smileNormal = painterResource(Res.drawable.smile_normal),
        smilePressed = painterResource(Res.drawable.smile_pressed),
        smileWin = painterResource(Res.drawable.smile_win),
        smileTrying = painterResource(Res.drawable.smile_trying),
        digits = buildMap {
            for (i in '0'..'9') {
                put(i, painterResource(DrawableResource("digit_$i.png")))
            }
            put('-', painterResource(Res.drawable.digit_minus))
        },
    )

internal val LocalGameIcons: ProvidableCompositionLocal<GameIcons?> =
    compositionLocalOf { null }

internal val CompositionLocal<GameIcons?>.icons: GameIcons
    @Composable
    get() = requireNotNull(current)
