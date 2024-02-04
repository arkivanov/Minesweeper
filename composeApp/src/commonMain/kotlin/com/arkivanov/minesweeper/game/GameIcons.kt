package com.arkivanov.minesweeper.game

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
internal object GameIcons {

    val cellClosed: Painter
        @Composable
        get() = painterResource("cell_closed.png")

    val cellClosedFlag: Painter
        @Composable
        get() = painterResource("cell_closed_flag.png")

    val cellOpen: Painter
        @Composable
        get() = painterResource("cell_open.png")

    val cellOpenMine: Painter
        @Composable
        get() = painterResource("cell_open_mine.png")

    val smileFailed: Painter
        @Composable
        get() = painterResource("smile_failed.png")

    val smileNormal: Painter
        @Composable
        get() = painterResource("smile_normal.png")

    val smilePressed: Painter
        @Composable
        get() = painterResource("smile_pressed.png")

    val smileSuccess: Painter
        @Composable
        get() = painterResource("smile_success.png")

    val smileTrying: Painter
        @Composable
        get() = painterResource("smile_trying.png")

    @Composable
    fun cellOpen(number: Int): Painter =
        painterResource("cell_open_$number.png")
}
