package com.arkivanov.minesweeper.game

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.store.create

internal data class State(
    val grid: Grid,
    val width: Int = grid.keys.maxOf { it.x } + 1,
    val height: Int = grid.keys.maxOf { it.y } + 1,
    val maxMines: Int = grid.values.count { it.value.isMine },
    val gameStatus: GameStatus = GameStatus.INITIALIZED,
) {
    init {
        require(grid.size == width * height) { "Grid size must be equal to width * height" }
    }
}

internal enum class GameStatus {
    INITIALIZED,
    STARTED,
    FINISHED,
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
    data class Closed(val isFlagged: Boolean = false) : CellStatus
    data object Open : CellStatus
}

internal val CellValue.isNone: Boolean
    get() = this is CellValue.None

internal val CellValue.isMine: Boolean
    get() = this is CellValue.Mine

internal val CellValue.isNumber: Boolean
    get() = this is CellValue.Number

internal val CellStatus.isOpen: Boolean
    get() = this is CellStatus.Open

private fun Cell.open(): Cell =
    copy(status = CellStatus.Open)

internal sealed interface Intent {
    data class RevealCell(val x: Int, val y: Int) : Intent
    data class ToggleFlag(val x: Int, val y: Int) : Intent
}

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
        is Intent.RevealCell -> revealCell(location = intent.x by intent.y)
        is Intent.ToggleFlag -> toggleFlag(location = intent.x by intent.y)
    }

private fun State.revealCell(location: Location): State =
    when (gameStatus) {
        GameStatus.FINISHED -> this
        GameStatus.INITIALIZED -> start(clickLocation = location).revealCells(clickLocation = location)
        GameStatus.STARTED -> revealCells(clickLocation = location)
    }

private fun State.start(clickLocation: Location): State =
    copy(
        grid = grid.planted(count = maxMines, clickLocation = clickLocation).numbered(),
        gameStatus = GameStatus.STARTED,
    )

private fun State.revealCells(clickLocation: Location): State {
    val cell = grid.getValue(clickLocation).takeUnless { it.status.isOpen } ?: return this
    val boardWithOpenCell = grid + (clickLocation to cell.open())

    return when (cell.value) {
        is CellValue.None -> copy(grid = boardWithOpenCell.toMutableMap().apply { revealAdjacentCells(location = clickLocation) })
        is CellValue.Mine -> copy(grid = boardWithOpenCell, gameStatus = GameStatus.FINISHED)
        is CellValue.Number -> copy(grid = boardWithOpenCell)
    }
}

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

    val cell = get(location)?.takeUnless { it.value.isMine || it.status.isOpen } ?: return false
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

private fun Grid.countAdjacentMines(location: Location): Int {
    var count = 0

    location.forEachAdjacent { loc ->
        if (get(loc)?.value?.isMine == true) {
            count++
        }
    }

    return count
}

private fun State.toggleFlag(location: Location): State {
    val cell = grid.getValue(location)
    val status = cell.status as? CellStatus.Closed ?: return this
    return copy(grid = grid + (location to cell.copy(status = status.copy(isFlagged = true))))
}
