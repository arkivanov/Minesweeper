package com.arkivanov.minesweeper.game.stopwatch

// TODO: thought to think about:
//    We can use Formatter in stopwatch, to also exclude logic of pad out of game.
//  Think about stopwatch as self-ended component, it may does it, but in real,
//  stopwatch must count seconds in Int, not in Strings, so seems like Formatter doesn't needed.
internal class TimestampMillisecondsFormatter {

    fun format(timestamp: Long): String {
        val seconds = timestamp / 1000
        return seconds.pad(2)
    }

    private fun Long.pad(desiredLength: Int) =
        this.toString().padStart(desiredLength, '0')


    companion object {
        const val DEFAULT_TIME = "000"
    }
}