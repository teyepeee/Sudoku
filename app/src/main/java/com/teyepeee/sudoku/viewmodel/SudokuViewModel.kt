package com.teyepeee.sudoku.viewmodel

import androidx.lifecycle.ViewModel
import com.teyepeee.sudoku.game.SudokuGame

class SudokuViewModel : ViewModel() {
    val sudokuGame = SudokuGame()
}