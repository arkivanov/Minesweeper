package com.arkivanov.minesweeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.toArgb

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App()

            val primaryColor = MaterialTheme.colors.primary
            DisposableEffect(primaryColor) {
                window.statusBarColor = primaryColor.toArgb()
                onDispose {}
            }
        }
    }
}
