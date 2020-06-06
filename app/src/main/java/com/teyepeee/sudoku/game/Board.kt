package com.teyepeee.sudoku.game

import kotlin.math.sqrt

class Board(private val size: Int, var cells: List<Cell>) {

    private val boardArrays: Array<IntArray> = arrayOf(
        intArrayOf(
            0, 0, 0, 2, 6, 0, 7, 0, 1,
            6, 8, 0, 0, 7, 0, 0, 9, 0,
            1, 9, 0, 0, 0, 4, 5, 0, 0,
            8, 2, 0, 1, 0, 0, 0, 4, 0,
            0, 0, 4, 6, 0, 2, 9, 0, 0,
            0, 5, 0, 0, 0, 3, 0, 2, 8,
            0, 0, 9, 3, 0, 0, 0, 7, 4,
            0, 4, 0, 0, 5, 0, 0, 3, 6,
            7, 0, 3, 0, 1, 8, 0, 0, 0
        ),
        intArrayOf(
            1, 0, 0, 4, 8, 9, 0, 0, 6,
            7, 3, 0, 0, 5, 0, 0, 4, 0,
            4, 6, 0, 0, 0, 1, 2, 9, 5,
            3, 8, 7, 1, 2, 0, 6, 0, 0,
            5, 0, 1, 7, 0, 3, 0, 0, 8,
            0, 4, 6, 0, 9, 5, 7, 1, 0,
            9, 1, 4, 6, 0, 0, 0, 8, 0,
            0, 2, 0, 0, 4, 0, 0, 3, 7,
            8, 0, 3, 5, 1, 2, 0, 0, 4
        ),
        intArrayOf(
            1, 2, 0, 6, 0, 8, 0, 0, 0,
            5, 8, 4, 2, 3, 9, 7, 0, 1,
            0, 6, 0, 1, 4, 0, 0, 0, 0,
            3, 7, 0, 0, 6, 1, 5, 8, 0,
            6, 9, 1, 0, 8, 0, 2, 7, 4,
            4, 5, 8, 7, 0, 2, 0, 1, 3,
            0, 3, 0, 0, 2, 4, 1, 5, 0,
            2, 0, 9, 8, 5, 0, 4, 3, 6,
            0, 0, 0, 3, 0, 6, 0, 9, 2
        ),
        intArrayOf(
            0, 0, 0, 6, 0, 0, 4, 0, 0,
            7, 0, 0, 0, 0, 3, 6, 0, 0,
            0, 0, 0, 0, 9, 1, 0, 8, 2,
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 5, 0, 1, 8, 0, 0, 0, 3,
            0, 0, 0, 3, 0, 6, 0, 4, 5,
            0, 4, 0, 2, 0, 0, 0, 6, 0,
            9, 0, 3, 0, 0, 0, 0, 0, 0,
            0, 2, 0, 0, 0, 0, 1, 0, 0
        ),
        intArrayOf(
            2, 0, 0, 3, 0, 0, 0, 0, 0,
            8, 0, 4, 0, 6, 2, 0, 0, 3,
            0, 1, 3, 8, 0, 0, 2, 0, 0,
            0, 0, 0, 0, 2, 0, 3, 9, 0,
            5, 0, 7, 0, 0, 0, 6, 2, 1,
            0, 3, 2, 0, 0, 6, 0, 0, 0,
            0, 2, 0, 0, 0, 9, 1, 4, 0,
            6, 0, 1, 2, 5, 0, 8, 0, 9,
            0, 0, 0, 0, 0, 1, 0, 0, 2
        )
    )

    fun getCell(row: Int, col: Int) = cells[row * size + col]

    fun resetCells() {
        val boardArray = boardArrays[boardArrays.indices.random()]
        val cells = List(size * size) { i ->
            Cell(i / size, i % size, boardArray[i])
        }
        Board(size, GenerateBoard.rearrangeBoard(cells))
        this.cells = cells
    }

    fun solve(): Boolean {
        for (i in 0 until size) {
            for (j in 0 until size) {
                if (getCell(i, j).value == 0) {
                    for (k in 1..size) {
                        getCell(i, j).value = k
                        if (isValid(i, j) && solve()) {
                            return true
                        }
                        getCell(i, j).value = 0
                    }
                    return false
                }
            }
        }
        return true
    }

    private fun isValid(row: Int, col: Int): Boolean {
        return rowConstraint(row) && columnConstraint(col) && subSectionConstraint(row, col)
    }

    private fun rowConstraint(row: Int): Boolean {
        val constraint = BooleanArray(size)
        return (0 until size).all { col -> checkConstraint(row, constraint, col) }
    }

    private fun columnConstraint(col: Int): Boolean {
        val constraint = BooleanArray(size)
        return (0 until size).all { row -> checkConstraint(row, constraint, col) }
    }

    private fun subSectionConstraint(row: Int, col: Int): Boolean {
        val constraint = BooleanArray(size)
        val subSectionSize = sqrt(size.toDouble()).toInt()

        val subSectionRowStart = row / subSectionSize * subSectionSize
        val subSectionRowEnd = subSectionRowStart + subSectionSize

        val subSectionColStart = col / subSectionSize * subSectionSize
        val subSectionColEnd = subSectionColStart + subSectionSize

        for (r in subSectionRowStart until subSectionRowEnd) {
            for (c in subSectionColStart until subSectionColEnd) {
                if (!checkConstraint(r, constraint, c)) return false
            }
        }
        return true
    }

    private fun checkConstraint(row: Int, constraint: BooleanArray, col: Int): Boolean {
        if (getCell(row, col).value != 0) {
            if (!constraint[getCell(row, col).value - 1]) {
                constraint[getCell(row, col).value - 1] = true
            } else {
                return false
            }
        }
        return true
    }
}