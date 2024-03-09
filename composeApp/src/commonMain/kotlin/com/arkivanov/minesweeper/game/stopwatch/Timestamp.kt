package com.arkivanov.minesweeper.game.stopwatch

import kotlinx.datetime.Clock

class Timestamp: TimestampProvider {
    private val clock = Clock.System
    override fun getMilliseconds(): Long {
        return clock.now().toEpochMilliseconds()
    }
}