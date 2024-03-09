package com.arkivanov.minesweeper.game.stopwatch

interface TimestampProvider {
    fun getMilliseconds(): Long
}