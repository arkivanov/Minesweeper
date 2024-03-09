package com.arkivanov.minesweeper.game.stopwatch.utils

import com.arkivanov.minesweeper.game.stopwatch.Timestamp
import com.arkivanov.minesweeper.game.stopwatch.ElapsedTimeCalculator
import com.arkivanov.minesweeper.game.stopwatch.StopwatchStateCalculator
import com.arkivanov.minesweeper.game.stopwatch.StopwatchStateHolder

// TODO: Deprecate it later
//  Reason: Provide with function, not so really powerful and how must be done, but its stub for a time,
//  because I'm stuck & overwhelmed with thing of convenient and `correct` usage the Stopwatch in a project
internal fun provideStopwatchStateHolder(): StopwatchStateHolder {
    val timestamp = Timestamp()
    val elapsedTimeCalculator = ElapsedTimeCalculator(timestampProvider = timestamp)
    val stopwatchStateCalculator = StopwatchStateCalculator(
        timestampProvider = timestamp,
        elapsedTimeCalculator = elapsedTimeCalculator,
    )
    return StopwatchStateHolder(
        stopwatchStateCalculator = stopwatchStateCalculator,
        elapsedTimeCalculator = elapsedTimeCalculator,
    )
}