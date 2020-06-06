package com.teyepeee.sudoku.game

import android.util.Log
import androidx.lifecycle.MutableLiveData
import java.util.*

class SudokuGame {

    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var cellsLiveData = MutableLiveData<List<Cell>>()
    var validBoardLiveData = MutableLiveData<Boolean>()

    private var selectedRow = -1
    private var selectedCol = -1

    private val boardSize = 9

    private val board: Board

    init {
        val cells = List(boardSize * boardSize) { i ->
            Cell(i / boardSize, i % boardSize, 0)
        }
        board = Board(boardSize, cells)

        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        cellsLiveData.postValue(board.cells)
    }

    fun newGame() {
        board.resetCells()

        selectedRow = -1
        selectedCol = -1

        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        cellsLiveData.postValue(board.cells)
    }

    fun solve() {
        board.solve()

        selectedRow = -1
        selectedCol = -1

        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        cellsLiveData.postValue(board.cells)
        checkWin()
    }

    fun clear() {
        if (selectedRow == -1 || selectedCol == -1) return

        board.getCell(selectedRow, selectedCol).value = 0
        selectedRow = -1
        selectedCol = -1

        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        cellsLiveData.postValue(board.cells)
    }

    fun checkWin() {
        Log.d("Sudoku", "check is the board is correct")
        val correctBoard = checkRows() && checkCols() && checkBoxes()
        Log.d("Sudoku", "correctBoard $correctBoard")
        validBoardLiveData.postValue(correctBoard)
    }

    private fun checkRows(): Boolean {
        val set = HashSet<Int>(9)
        for (row in 0 until 9) {
            for (col in 0 until 9) {
                val curr = board.getCell(row, col).value
                if (set.contains(curr) || curr == 0) return false

                set.add(curr)
            }
            set.clear()
        }

        return true
    }

    private fun checkCols(): Boolean {
        val set = HashSet<Int>(9)
        for (col in 0 until 9) {
            for (row in 0 until 9) {
                val curr = board.getCell(row, col).value
                if (set.contains(curr) || curr == 0) return false

                set.add(curr)
            }
            set.clear()
        }

        return true
    }

    private fun checkBoxes(): Boolean {
        val set = HashSet<Int>(9)
        for (square in 0 until 9) {
            for (row in 0 until 3) {
                for (col in 0 until 3) {
                    val curr = board.getCell(3 * (square / 3) + row, 3 * (square % 3) + col).value
                    if (set.contains(curr) || curr == 0) return false

                    set.add(curr)
                }
            }
            set.clear()
        }

        return true
    }

    fun handleInput(number: Int) {
        if (selectedRow == -1 || selectedCol == -1) return
        if (board.getCell(selectedRow, selectedCol).isStartingCell) return

        board.getCell(selectedRow, selectedCol).value = number
        cellsLiveData.postValue(board.cells)
    }

    fun updateSelectedCell(row: Int, col: Int) {
        if (!board.getCell(row, col).isStartingCell) {
            selectedRow = row
            selectedCol = col
            selectedCellLiveData.postValue(Pair(row, col))
        }
    }
}