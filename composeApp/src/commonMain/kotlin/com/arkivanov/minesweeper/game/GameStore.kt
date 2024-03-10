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
    data object TickTimer : Intent
    data object Restart : Intent
}

internal fun StoreFactory.gameStore(state: GameState): Store<Intent, GameState, Nothing> =
    create(
        name = "GameStore",
        initialState = state,
        reducer = { reduce(it) },
    )

internal fun newGameState(width: Int, height: Int, maxMines: Int): GameState =
    GameState(
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

private fun GameState.reduce(intent: Intent): GameState =
    when (intent) {
        is Intent.PressCell -> pressCellIntent(location = intent.x by intent.y)
        is Intent.PressCells -> pressCellsIntent(location = intent.x by intent.y)
        is Intent.ReleaseCells -> releaseCellsIntent(location = intent.x by intent.y)
        is Intent.ToggleFlag -> toggleFlagIntent(location = intent.x by intent.y)
        is Intent.TickTimer -> tick()
        is Intent.Restart -> newGameState(width = width, height = height, maxMines = maxMines)
    }.finishIfNeeded()

private fun GameState.finishIfNeeded(): GameState =
    if (grid.values.any { it.value.isMine && it.status.isOpen }) {
        copy(gameStatus = GameStatus.FAILED)
    } else if (grid.values.count { it.status.isClosed } == maxMines) {
        copy(gameStatus = GameStatus.WIN)
    } else {
        this
    }

private fun GameState.pressCellIntent(location: Location): GameState =
    runUnless(gameStatus.isOver) {
        releaseAllCells().pressCell(location = location)
    }

private fun GameState.pressCell(location: Location): GameState {
    var cell = grid.getValue(location)
    val status = cell.status as? CellStatus.Closed ?: return this
    cell = cell.copy(status = status.copy(isPressed = true))

    return copy(grid = grid + (location to cell), pressMode = PressMode.SINGLE)
}

private fun GameState.pressCellsIntent(location: Location): GameState =
    runUnless(gameStatus.isOver) {
        releaseAllCells().pressCells(location = location)
    }

private fun GameState.pressCells(location: Location): GameState {
    val cells = ArrayList<Pair<Location, Cell>>()
    location.forEachAround { loc ->
        val cell = grid[loc] ?: return@forEachAround
        val status = (cell.status as? CellStatus.Closed)?.takeUnless { it.isFlagged || it.isPressed } ?: return@forEachAround
        cells += loc to cell.copy(status = status.copy(isPressed = true))
    }

    return copy(grid = grid + cells, pressMode = PressMode.MULTIPLE)
}

private fun GameState.releaseCellsIntent(location: Location): GameState =
    runUnless(gameStatus.isOver) {
        releaseAllCells().releaseCells(location = location)
    }

private fun GameState.releaseCells(location: Location): GameState =
    when (pressMode) {
        PressMode.NONE -> this
        PressMode.SINGLE -> revealCell(location)
        PressMode.MULTIPLE -> revealCellsAround(location)
    }.copy(pressMode = PressMode.NONE)

private fun GameState.releaseAllCells(): GameState {
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

private fun GameState.revealCell(location: Location): GameState =
    when (gameStatus) {
        GameStatus.INITIALIZED -> start(clickLocation = location).revealCells(centerLocation = location)
        GameStatus.STARTED -> revealCells(centerLocation = location)
        GameStatus.WIN,
        GameStatus.FAILED -> this
    }

private fun GameState.start(clickLocation: Location): GameState =
    copy(
        grid = grid.planted(count = maxMines, clickLocation = clickLocation).numbered(),
        gameStatus = GameStatus.STARTED,
    )

private fun GameState.revealCells(centerLocation: Location): GameState {
    val cell = grid.getValue(centerLocation).takeUnless { it.status.isOpen } ?: return this
    val boardWithOpenCell = grid + (centerLocation to cell.open())

    return when (cell.value) {
        is CellValue.None -> copy(grid = boardWithOpenCell).revealAdjacentCells(location = centerLocation)
        is CellValue.Mine,
        is CellValue.Number -> copy(grid = boardWithOpenCell)
    }
}

private fun GameState.revealAdjacentCells(location: Location): GameState =
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

private fun GameState.revealCellsAround(location: Location): GameState {
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

private fun GameState.toggleFlagIntent(location: Location): GameState {
    if (gameStatus.isOver) {
        return this
    }

    val cell = grid.getValue(location)
    val status = cell.status as? CellStatus.Closed ?: return this

    return copy(grid = grid + (location to cell.copy(status = status.copy(isFlagged = !status.isFlagged))))
}

private fun GameState.tick(): GameState =
    if (timer < 999) copy(timer = timer + 1) else this
