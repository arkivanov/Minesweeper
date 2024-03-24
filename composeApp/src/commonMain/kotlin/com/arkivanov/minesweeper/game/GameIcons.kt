package com.arkivanov.minesweeper.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.painter.Painter
import minesweeper.composeapp.generated.resources.*
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
        cellOpenNumbers = mapOf(
            1 to painterResource(Res.drawable.cell_open_1),
            2 to painterResource(Res.drawable.cell_open_2),
            3 to painterResource(Res.drawable.cell_open_3),
            4 to painterResource(Res.drawable.cell_open_4),
            5 to painterResource(Res.drawable.cell_open_5),
            6 to painterResource(Res.drawable.cell_open_6),
            7 to painterResource(Res.drawable.cell_open_7),
            8 to painterResource(Res.drawable.cell_open_8),
        ),
        smileFailed = painterResource(Res.drawable.smile_failed),
        smileNormal = painterResource(Res.drawable.smile_normal),
        smilePressed = painterResource(Res.drawable.smile_pressed),
        smileWin = painterResource(Res.drawable.smile_win),
        smileTrying = painterResource(Res.drawable.smile_trying),
        digits = mapOf(
            '0' to painterResource(Res.drawable.digit_0),
            '1' to painterResource(Res.drawable.digit_1),
            '2' to painterResource(Res.drawable.digit_2),
            '3' to painterResource(Res.drawable.digit_3),
            '4' to painterResource(Res.drawable.digit_4),
            '5' to painterResource(Res.drawable.digit_5),
            '6' to painterResource(Res.drawable.digit_6),
            '7' to painterResource(Res.drawable.digit_7),
            '8' to painterResource(Res.drawable.digit_8),
            '9' to painterResource(Res.drawable.digit_9),
            '-' to painterResource(Res.drawable.digit_minus),
        ),
    )

internal val LocalGameIcons: ProvidableCompositionLocal<GameIcons?> =
    compositionLocalOf { null }

internal val CompositionLocal<GameIcons?>.icons: GameIcons
    @Composable
    get() = requireNotNull(current)
