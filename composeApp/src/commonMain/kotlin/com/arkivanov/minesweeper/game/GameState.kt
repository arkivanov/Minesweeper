package com.arkivanov.minesweeper.game

import kotlinx.serialization.Serializable

@Serializable
internal data class GameState(
    val grid: Grid,
    val width: Int = grid.keys.maxOf { it.x } + 1,
    val height: Int = grid.keys.maxOf { it.y } + 1,
    val maxMines: Int = grid.values.count { it.value.isMine },
    val gameStatus: GameStatus = GameStatus.INITIALIZED,
    val pressMode: PressMode = PressMode.NONE,
) {
    init {
        require(grid.size == width * height) {
            "Grid size must be equal to width * height"
        }
    }
}

internal enum class GameStatus {
    INITIALIZED,
    STARTED,
    WIN,
    FAILED,
}

internal enum class PressMode {
    NONE,
    SINGLE,
    MULTIPLE,
}

internal val GameStatus.isOver: Boolean
    get() =
        when (this) {
            GameStatus.INITIALIZED,
            GameStatus.STARTED -> false

            GameStatus.WIN,
            GameStatus.FAILED -> true
        }

internal typealias Grid = Map<Location, Cell>
internal typealias MutableGrid = MutableMap<Location, Cell>

@Serializable
internal data class Cell(
    val value: CellValue = CellValue.None,
    val status: CellStatus = CellStatus.Closed(),
)

@Serializable
sealed interface CellValue {

    @Serializable
    data object None : CellValue

    @Serializable
    data object Mine : CellValue

    @Serializable
    data class Number(val number: Int) : CellValue
}

@Serializable
sealed interface CellStatus {

    @Serializable
    data class Closed(
        val isFlagged: Boolean = false,
        val isPressed: Boolean = false,
    ) : CellStatus

    @Serializable
    data object Open : CellStatus
}

internal val CellValue.isNone: Boolean
    get() = this is CellValue.None

internal val CellValue.isMine: Boolean
    get() = this is CellValue.Mine

internal val CellValue.isNumber: Boolean
    get() = asNumber() != null

internal fun CellValue.asNumber(): CellValue.Number? =
    this as? CellValue.Number

internal val CellStatus.isClosed: Boolean
    get() = this is CellStatus.Closed

internal val CellStatus.isOpen: Boolean
    get() = this is CellStatus.Open

internal val CellStatus.isFlagged: Boolean
    get() = (this as? CellStatus.Closed)?.isFlagged == true

internal fun Cell.open(): Cell =
    copy(status = CellStatus.Open)
