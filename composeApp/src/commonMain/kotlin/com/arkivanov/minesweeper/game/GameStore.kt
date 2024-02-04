package com.arkivanov.minesweeper.game

import com.arkivanov.minesweeper.runUnless
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.store.create

internal sealed interface Intent {
    data class PressCell(val x: Int, val y: Int) : Intent
    data class PressCells(val x: Int, val y: Int) : Intent
    data class ReleaseCells(val x: Int, val y: Int) : Intent
    data class ToggleFlag(val x: Int, val y: Int) : Intent
    data object Restart : Intent
}

internal data class State(
    val grid: Grid,
    val width: Int = grid.keys.maxOf { it.x } + 1,
    val height: Int = grid.keys.maxOf { it.y } + 1,
    val maxMines: Int = grid.values.count { it.value.isMine },
    val gameStatus: GameStatus = GameStatus.INITIALIZED,
    val pressMode: PressMode = PressMode.NONE,
) {
    init {
        require(grid.size == width * height) { "Grid size must be equal to width * height" }
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

private val GameStatus.isOver: Boolean
    get() =
        when (this) {
            GameStatus.INITIALIZED,
            GameStatus.STARTED -> false

            GameStatus.WIN,
            GameStatus.FAILED -> true
        }

internal typealias Grid = Map<Location, Cell>
internal typealias MutableGrid = MutableMap<Location, Cell>

internal data class Cell(
    val value: CellValue = CellValue.None,
    val status: CellStatus = CellStatus.Closed(),
)

sealed interface CellValue {
    data object None : CellValue
    data object Mine : CellValue
    data class Number(val number: Int) : CellValue
}

sealed interface CellStatus {
    data class Closed(
        val isFlagged: Boolean = false,
        val isPressed: Boolean = false,
    ) : CellStatus

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

private fun Cell.open(): Cell =
    copy(status = CellStatus.Open)

internal fun StoreFactory.gameStore(state: State): Store<Intent, State, Nothing> =
    create(
        name = "GameStore",
        initialState = state,
        reducer = { reduce(it) },
    )

internal fun newGameState(width: Int, height: Int, maxMines: Int): State =
    State(
        width = width,
        height = height,
        maxMines = maxMines,
        grid = newBoard(width = width, height = height),
    )

private fun newBoard(width: Int, height: Int): Grid =
    buildMap {
        repeat(width) { x ->
            repeat(height) { y ->
                put(x by y, Cell())
            }
        }
    }

private fun State.reduce(intent: Intent): State =
    when (intent) {
        is Intent.PressCell -> pressCellIntent(location = intent.x by intent.y)
        is Intent.PressCells -> pressCellsIntent(location = intent.x by intent.y)
        is Intent.ReleaseCells -> releaseCellsIntent(location = intent.x by intent.y)
        is Intent.ToggleFlag -> toggleFlagIntent(location = intent.x by intent.y)
        is Intent.Restart -> newGameState(width = width, height = height, maxMines = maxMines)
    }.finishIfNeeded()

private fun State.finishIfNeeded(): State =
    if (grid.values.any { it.value.isMine && it.status.isOpen }) {
        copy(gameStatus = GameStatus.FAILED)
    } else if (grid.values.count { it.status.isClosed } == maxMines) {
        copy(gameStatus = GameStatus.WIN)
    } else {
        this
    }

private fun State.pressCellIntent(location: Location): State =
    runUnless(gameStatus.isOver) {
        releaseAllCells().pressCell(location = location)
    }

private fun State.pressCell(location: Location): State {
    var cell = grid.getValue(location)
    val status = cell.status as? CellStatus.Closed ?: return this
    cell = cell.copy(status = status.copy(isPressed = true))

    return copy(grid = grid + (location to cell), pressMode = PressMode.SINGLE)
}

private fun State.pressCellsIntent(location: Location): State =
    runUnless(gameStatus.isOver) {
        releaseAllCells().pressCells(location = location)
    }

private fun State.pressCells(location: Location): State {
    val cells = ArrayList<Pair<Location, Cell>>()
    location.forEachAround { loc ->
        val cell = grid[loc] ?: return@forEachAround
        val status = (cell.status as? CellStatus.Closed)?.takeUnless { it.isFlagged || it.isPressed } ?: return@forEachAround
        cells += loc to cell.copy(status = status.copy(isPressed = true))
    }

    return copy(grid = grid + cells, pressMode = PressMode.MULTIPLE)
}

private fun State.releaseCellsIntent(location: Location): State =
    runUnless(gameStatus.isOver) {
        releaseAllCells().releaseCells(location = location)
    }

private fun State.releaseCells(location: Location): State =
    when (pressMode) {
        PressMode.NONE -> this
        PressMode.SINGLE -> revealCell(location)
        PressMode.MULTIPLE -> revealCellsAround(location)
    }.copy(pressMode = PressMode.NONE)

private fun State.releaseAllCells(): State {
    val releasedCells =
        grid.mapNotNull { (location, cell) ->
            val status = cell.status
            if ((status is CellStatus.Closed) && status.isPressed) {
                location to cell.copy(status = status.copy(isPressed = false))
            } else {
                null
            }
        }

    return copy(grid = grid + releasedCells)
}

private fun State.revealCell(location: Location): State =
    when (gameStatus) {
        GameStatus.INITIALIZED -> start(clickLocation = location).revealCells(centerLocation = location)
        GameStatus.STARTED -> revealCells(centerLocation = location)
        GameStatus.WIN,
        GameStatus.FAILED -> this
    }

private fun State.start(clickLocation: Location): State =
    copy(
        grid = grid.planted(count = maxMines, clickLocation = clickLocation).numbered(),
        gameStatus = GameStatus.STARTED,
    )

private fun State.revealCells(centerLocation: Location): State {
    val cell = grid.getValue(centerLocation).takeUnless { it.status.isOpen } ?: return this
    val boardWithOpenCell = grid + (centerLocation to cell.open())

    return when (cell.value) {
        is CellValue.None -> copy(grid = boardWithOpenCell).revealAdjacentCells(location = centerLocation)
        is CellValue.Mine,
        is CellValue.Number -> copy(grid = boardWithOpenCell)
    }
}

private fun State.revealAdjacentCells(location: Location): State =
    copy(grid = grid.toMutableMap().apply { revealAdjacentCells(location = location) })

private fun MutableGrid.revealAdjacentCells(location: Location, visited: MutableSet<Location> = HashSet()) {
    location.forEachAdjacent { loc ->
        if (revealCell(location = loc, visited = visited)) {
            revealAdjacentCells(location = loc, visited = visited)
        }
    }
}

private fun MutableGrid.revealCell(location: Location, visited: MutableSet<Location>): Boolean {
    if (!visited.add(location)) {
        return false
    }

    val cell = get(location)?.takeUnless { it.status.isOpen || it.status.isFlagged } ?: return false
    set(location, cell.open())
    return cell.value.isNone
}

private fun Grid.planted(count: Int, clickLocation: Location): Grid =
    plus(
        minus(clickLocation)
            .asSequence()
            .shuffled()
            .take(count)
            .map { (coords, cell) -> coords to cell.copy(value = CellValue.Mine) }
            .toList()
    )

private fun Grid.numbered(): Grid =
    mapValues { (location, cell) ->
        when (cell.value) {
            is CellValue.None -> {
                val mineCount = countAdjacentMines(location = location)
                if (mineCount > 0) {
                    cell.copy(value = CellValue.Number(number = mineCount))
                } else {
                    cell
                }
            }

            is CellValue.Mine,
            is CellValue.Number -> cell
        }
    }

private fun Grid.countAdjacentMines(location: Location): Int =
    location.countAdjacent { get(it)?.value?.isMine == true }

private fun State.revealCellsAround(location: Location): State {
    if (gameStatus != GameStatus.STARTED) {
        return this
    }

    val number = grid.getValue(location).takeIf { it.status.isOpen }?.value?.asNumber() ?: return this
    val flagCount = location.countAdjacent { grid[it]?.status?.isFlagged == true }

    return if (flagCount == number.number) {
        revealAdjacentCells(location = location)
    } else {
        this
    }
}

private fun State.toggleFlagIntent(location: Location): State {
    if (gameStatus.isOver) {
        return this
    }

    val cell = grid.getValue(location)
    val status = cell.status as? CellStatus.Closed ?: return this

    return copy(grid = grid + (location to cell.copy(status = status.copy(isFlagged = !status.isFlagged))))
}
