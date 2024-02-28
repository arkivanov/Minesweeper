package com.arkivanov.minesweeper

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics

fun Modifier.setContentDescription(description: String, role: Role): Modifier =
    this.then(
        Modifier.semantics {
            this.contentDescription = description
            this.role = role
        }
    )
