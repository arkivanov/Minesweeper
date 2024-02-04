package com.arkivanov.minesweeper.game

internal data class Location(
    val x: Int,
    val y: Int,
)

internal infix fun Int.by(other: Int): Location =
    Location(x = this, y = other)

internal inline fun Location.forEachAdjacent(block: (Location) -> Unit) {
    forEachAround { location ->
        if (location != this) {
            block(location)
        }
    }
}

internal inline fun Location.forEachAround(block: (Location) -> Unit) {
    for (xx in x - 1..x + 1) {
        for (yy in y - 1..y + 1) {
            block(xx by yy)
        }
    }
}
