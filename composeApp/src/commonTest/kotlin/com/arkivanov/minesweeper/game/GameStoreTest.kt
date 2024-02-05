package com.arkivanov.minesweeper.game

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Suppress("TestFunctionName")
class GameStoreTest {

    private val storeFactory = DefaultStoreFactory()

    @Test
    fun WHEN_created_THEN_state_initialized() {
        val store = storeFactory.gameStore(newGameState(width = 10, height = 20, maxMines = 5))

        assertEquals(10, store.state.width)
        assertEquals(20, store.state.height)
        assertEquals(5, store.state.maxMines)
        assertEquals(GameStatus.INITIALIZED, store.state.gameStatus)
        val grid = store.state.grid
        assertEquals(200, grid.size)
        assertTrue(grid.values.none { it.status.isOpen })
        assertEquals(200, grid.values.filter { it.value.isNone }.size)
        assertEquals(0, grid.values.filter { it.value.isMine }.size)
        assertEquals(0, grid.values.filter { it.value.isNumber }.size)
    }

    @Test
    fun GIVEN_created_WHEN_first_click_on_cell_THEN_state_started() {
        val store = storeFactory.gameStore(newGameState(width = 10, height = 20, maxMines = 5))

        store.click(x = 5, y = 5)

        assertEquals(GameStatus.STARTED, store.state.gameStatus)
        val grid = store.state.grid
        assertEquals(200, grid.size)
        assertTrue(grid.values.any { it.status.isOpen })
        val noneCount = grid.values.count { it.value.isNone }
        assertTrue(noneCount <= 195)
        assertEquals(5, grid.values.count { it.value.isMine })
        val numberCount = grid.values.count { it.value.isNumber }
        assertTrue(numberCount <= 195)
        assertEquals(195, noneCount + numberCount)
    }

    @Test
    fun WHEN_click_on_number_cell_THEN_cell_revealed() {
        val store =
            storeFactory.gameStore(
                GameState(
                    grid = grid(
                        listOf(mineClosed(), numberOpen(number = 1), noneClosed()),
                        listOf(numberClosed(number = 1), numberOpen(number = 1), noneClosed()),
                        listOf(noneClosed(), noneClosed(), noneClosed()),
                    ),
                    gameStatus = GameStatus.STARTED,
                )
            )

        store.click(x = 1, y = 1)

        assertEquals(
            grid(
                listOf(mineClosed(), numberOpen(number = 1), noneClosed()),
                listOf(numberClosed(number = 1), numberOpen(number = 1), noneClosed()),
                listOf(noneClosed(), noneClosed(), noneClosed()),
            ),
            store.state.grid,
        )
    }

    @Test
    fun WHEN_click_on_bottom_right_empty_cell_THEN_adjacent_cells_revealed() {
        val store =
            storeFactory.gameStore(
                GameState(
                    grid = grid(
                        listOf(mineClosed(), numberOpen(number = 1), noneClosed()),
                        listOf(numberClosed(number = 1), numberClosed(number = 1), noneClosed()),
                        listOf(noneClosed(), noneClosed(), noneClosed()),
                    ),
                    gameStatus = GameStatus.STARTED,
                )
            )

        store.click(x = 2, y = 2)

        assertEquals(
            grid(
                listOf(mineClosed(), numberOpen(number = 1), noneOpen()),
                listOf(numberOpen(number = 1), numberOpen(number = 1), noneOpen()),
                listOf(noneOpen(), noneOpen(), noneOpen()),
            ),
            store.state.grid,
        )
    }

    @Test
    fun WHEN_click_on_top_left_empty_cell_THEN_adjacent_cells_revealed() {
        val store =
            storeFactory.gameStore(
                GameState(
                    grid = grid(
                        listOf(noneClosed(), noneClosed(), noneClosed()),
                        listOf(noneClosed(), numberClosed(number = 1), numberClosed(number = 1)),
                        listOf(noneClosed(), numberOpen(number = 1), mineClosed()),
                    ),
                    gameStatus = GameStatus.STARTED,
                )
            )

        store.click(x = 0, y = 0)

        assertEquals(
            grid(
                listOf(noneOpen(), noneOpen(), noneOpen()),
                listOf(noneOpen(), numberOpen(number = 1), numberOpen(number = 1)),
                listOf(noneOpen(), numberOpen(number = 1), mineClosed()),
            ),
            store.state.grid,
        )
    }

    @Test
    fun WHEN_click_on_mine_cell_THEN_cell_revealed() {
        val store =
            storeFactory.gameStore(
                GameState(
                    grid = grid(
                        listOf(mineClosed(), numberOpen(number = 1), noneClosed()),
                        listOf(numberClosed(number = 1), numberOpen(number = 1), noneClosed()),
                        listOf(noneClosed(), noneClosed(), noneClosed()),
                    ),
                    gameStatus = GameStatus.STARTED,
                )
            )

        store.click(x = 0, y = 0)

        assertEquals(
            grid(
                listOf(mineOpen(), numberOpen(number = 1), noneClosed()),
                listOf(numberClosed(number = 1), numberOpen(number = 1), noneClosed()),
                listOf(noneClosed(), noneClosed(), noneClosed()),
            ),
            store.state.grid,
        )
    }

    @Test
    fun WHEN_click_on_mine_cell_THEN_status_finished() {
        val store =
            storeFactory.gameStore(
                GameState(
                    grid = grid(
                        listOf(mineClosed(), numberOpen(number = 1), noneClosed()),
                        listOf(numberClosed(number = 1), numberOpen(number = 1), noneClosed()),
                        listOf(noneClosed(), noneClosed(), noneClosed()),
                    ),
                    gameStatus = GameStatus.STARTED,
                )
            )

        store.click(x = 0, y = 0)

        assertEquals(GameStatus.FAILED, store.state.gameStatus)
    }

    @Test
    fun WHEN_ToggleFlag_on_closed_cell_THEN_cell_flagged() {
        val store =
            storeFactory.gameStore(
                GameState(
                    grid = grid(
                        listOf(mineClosed(), numberClosed(number = 1), noneClosed()),
                        listOf(mineClosed(), numberClosed(number = 1), noneClosed()),
                        listOf(mineClosed(), numberClosed(number = 1), noneClosed()),
                    ),
                )
            )

        store.accept(Intent.ToggleFlag(x = 0, y = 0))
        store.accept(Intent.ToggleFlag(x = 1, y = 1))
        store.accept(Intent.ToggleFlag(x = 2, y = 2))

        assertEquals(
            grid(
                listOf(mineClosed(isFlagged = true), numberClosed(number = 1), noneClosed()),
                listOf(mineClosed(), numberClosed(number = 1, isFlagged = true), noneClosed()),
                listOf(mineClosed(), numberClosed(number = 1), noneClosed(isFlagged = true)),
            ),
            store.state.grid,
        )
    }

    @Test
    fun WHEN_ToggleFlag_on_open_cell_THEN_noop() {
        val store =
            storeFactory.gameStore(
                GameState(
                    grid = grid(
                        listOf(mineOpen(), numberOpen(number = 1), noneOpen()),
                        listOf(mineOpen(), numberOpen(number = 1), noneOpen()),
                        listOf(mineOpen(), numberOpen(number = 1), noneOpen()),
                    ),
                )
            )

        store.accept(Intent.ToggleFlag(x = 0, y = 0))
        store.accept(Intent.ToggleFlag(x = 1, y = 1))
        store.accept(Intent.ToggleFlag(x = 2, y = 2))

        assertEquals(
            grid(
                listOf(mineOpen(), numberOpen(number = 1), noneOpen()),
                listOf(mineOpen(), numberOpen(number = 1), noneOpen()),
                listOf(mineOpen(), numberOpen(number = 1), noneOpen()),
            ),
            store.state.grid,
        )
    }

    private fun Store<Intent, *, *>.click(x: Int, y: Int) {
        accept(Intent.PressCell(x = x, y = y))
        accept(Intent.ReleaseCells(x = x, y = y))
    }

    private fun grid(vararg columns: Collection<Cell>): Grid =
        buildMap {
            columns.forEachIndexed { x, column ->
                column.forEachIndexed { y, cell ->
                    put(x by y, cell)
                }
            }
        }

    private fun mineClosed(isFlagged: Boolean = false): Cell =
        Cell(value = CellValue.Mine, status = CellStatus.Closed(isFlagged = isFlagged))

    private fun mineOpen(): Cell =
        Cell(value = CellValue.Mine, status = CellStatus.Open)

    private fun numberClosed(number: Int, isFlagged: Boolean = false): Cell =
        Cell(value = CellValue.Number(number = number), status = CellStatus.Closed(isFlagged = isFlagged))

    private fun numberOpen(number: Int): Cell =
        Cell(value = CellValue.Number(number = number), status = CellStatus.Open)

    private fun noneClosed(isFlagged: Boolean = false): Cell =
        Cell(value = CellValue.None, status = CellStatus.Closed(isFlagged = isFlagged))

    private fun noneOpen(): Cell =
        Cell(value = CellValue.None, status = CellStatus.Open)
}
