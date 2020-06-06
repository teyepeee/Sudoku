package com.teyepeee.sudoku.game

class GenerateBoard {

    companion object {
        fun rearrangeBoard(cells: List<Cell>): List<Cell> {
            rearrangeRows(cells)
            rearrangeColumns(cells)
            rearrangeNumbers(cells)
            rearrangeRowBlock(cells)
            rearrangeColBlock(cells)
            assignStartingCells(cells)
            return cells
        }

        private fun rearrangeRows(cells: List<Cell>) {
            for (i in 0 until 3) {
                val permMapper: HashMap<Int, Int> = HashMap()
                val rowFactor = i * 3
                for (mapIndex in (rowFactor + 0) until (rowFactor + 3)) {
                    permMapper[mapIndex] = ((rowFactor + 0)..(rowFactor + 2)).random()
                }

                for (col in 0 until 9) {
                    for (row in 0 until 3) {
                        // enumerate rows within same three rows
                        run {
                            val temp = cells[(rowFactor + row) * 9 + col].value
                            cells[(rowFactor + row) * 9 + col].value =
                                cells[permMapper[rowFactor + row]!! * 9 + col].value
                            cells[permMapper[rowFactor + row]!! * 9 + col].value = temp
                        }
                    }
                }
            }
        }

        private fun rearrangeColumns(cells: List<Cell>) {
            for (i in 0 until 3) {
                val permMapper: HashMap<Int, Int> = HashMap()
                val colFactor = i * 3
                for (mapIndex in (colFactor + 0) until (colFactor + 3)) {
                    permMapper[mapIndex] = ((colFactor + 0)..(colFactor + 2)).random()
                }

                for (row in 0 until 9) {
                    for (col in 0 until 3) {
                        // enumerate columns within same three rows
                        run {
                            val temp = cells[row * 9 + colFactor + col].value
                            cells[row * 9 + colFactor + col].value =
                                cells[row * 9 + permMapper[colFactor + col]!!].value
                            cells[row * 9 + permMapper[colFactor + col]!!].value = temp
                        }
                    }
                }
            }
        }

        private fun rearrangeNumbers(cells: List<Cell>) {
            val swapMapper: HashMap<Int, Int> = HashMap()
            val numSet: MutableList<Int> = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9).toMutableList()

            for (i in 1..9) {
                val rand = (numSet.indices).random()
                swapMapper[i] = numSet[rand]
                numSet.removeAt(rand)
            }

            for (row in 0 until 9) {
                for (col in 0 until 9) {
                    if (cells[9 * row + col].value != 0) {
                        cells[9 * row + col].value = swapMapper[cells[row * 9 + col].value]!!
                    }
                }
            }
        }

        private fun rearrangeRowBlock(cells: List<Cell>) {
            val permMapper: HashMap<Int, Int> = HashMap()
            for (mapIndexBase in (0..2)) {
                val rand = (0..2).random()
                for (mapSum in (0..2)) {
                    permMapper[3 * mapIndexBase + mapSum] = 3 * rand + mapSum
                }
            }

            for (col in 0 until 9) {
                for (row in 0 until 9) {
                    // enumerate rows within same three rows
                    run {
                        val temp = cells[row * 9 + col].value
                        cells[row * 9 + col].value = cells[permMapper[row]!! * 9 + col].value
                        cells[permMapper[row]!! * 9 + col].value = temp
                    }
                }
            }
        }

        private fun rearrangeColBlock(cells: List<Cell>) {
            val permMapper: HashMap<Int, Int> = HashMap()
            for (mapIndexBase in (0..2)) {
                val rand = (0..2).random()
                for (mapSum in (0..2)) {
                    permMapper[3 * mapIndexBase + mapSum] = 3 * rand + mapSum
                }
            }

            for (col in 0 until 9) {
                for (row in 0 until 9) {
                    // enumerate rows within same three rows
                    run {
                        val temp = cells[row * 9 + col].value
                        cells[row * 9 + col].value = cells[permMapper[col]!! + 9 * row].value
                        cells[permMapper[col]!! + 9 * row].value = temp
                    }
                }
            }
        }

        private fun assignStartingCells(cells: List<Cell>) {
            for (i in 0 until 81) {
                if (cells[i].value != 0) {
                    cells[i].isStartingCell = true
                }
            }
        }
    }
}