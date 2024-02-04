package com.arkivanov.minesweeper

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.onClick
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerButton

@OptIn(ExperimentalFoundationApi::class)
internal actual fun Modifier.onClick(
    onPrimaryClick: () -> Unit,
    onSecondaryClick: () -> Unit,
    onTertiaryClick: () -> Unit,
): Modifier =
    onClick(onClick = onPrimaryClick)
        .onClick(
            matcher = PointerMatcher.mouse(PointerButton.Secondary),
            onClick = onSecondaryClick,
        )
        .onClick(
            matcher = PointerMatcher.mouse(PointerButton.Tertiary),
            onClick = onTertiaryClick,
        )

