package com.teyepeee.sudoku.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.teyepeee.sudoku.R
import com.teyepeee.sudoku.game.Cell
import com.teyepeee.sudoku.ui.custom.SudokuBoardView
import com.teyepeee.sudoku.utils.gone
import com.teyepeee.sudoku.utils.visible
import com.teyepeee.sudoku.viewmodel.SudokuViewModel
import kotlinx.android.synthetic.main.activity_sudoku.*

class SudokuActivity : AppCompatActivity(), SudokuBoardView.OnTouchListener {

    private lateinit var viewModel: SudokuViewModel
    private lateinit var inputs: List<TextView>
    private lateinit var countDownTimer: CountDownTimer
    private var isTimerRunning: Boolean = false
    private var startTimeInMillis = 900000L
    private var timeLeftInMillis = startTimeInMillis

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sudoku)

        sudokuBoardView.registerListener(this)

        viewModel = ViewModelProviders.of(this).get(SudokuViewModel::class.java)
        viewModel.sudokuGame.selectedCellLiveData.observe(
            this,
            Observer { updateSelectedCellUI(it) })
        viewModel.sudokuGame.cellsLiveData.observe(this, Observer { updateCells(it) })
        viewModel.sudokuGame.validBoardLiveData.observe(
            this,
            Observer { displayOutComeMessage(it) })

        inputs =
            listOf(
                textOne,
                textTwo,
                textThree,
                textFour,
                textFive,
                textSix,
                textSeven,
                textEight,
                textNine
            )

        inputs.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                viewModel.sudokuGame.handleInput(index + 1)
                viewModel.sudokuGame.checkWin()
            }
        }

        setInputClickable(false)
        buttonControl.gone()

        buttonNewGame.setOnClickListener {
            if (!isTimerRunning) {
                viewModel.sudokuGame.newGame()
                startTimer()
            } else {
                pauseTimer()
                showNewGameDialog()
            }
        }

        buttonControl.setOnClickListener {
            if (isTimerRunning) pauseTimer() else startTimer()
        }

        buttonSolveMe.setOnClickListener {
            if (isTimerRunning) viewModel.sudokuGame.solve()
        }

        updateCountDownText()
    }

    private fun displayOutComeMessage(isWin: Boolean) {
        if (isWin) {
            pauseTimer()
            buttonControl.gone()
            showCompletedDialog(true)
        } else {
            if (!isTimerRunning) {
                buttonControl.gone()
                showCompletedDialog(false)
            }
        }
    }

    private fun updateSelectedCellUI(cell: Pair<Int, Int>?) = cell?.let {
        sudokuBoardView.updateSelectedCellUI(cell.first, cell.second)
    }

    private fun updateCells(cells: List<Cell>?) = cells?.let {
        sudokuBoardView.updateCells(cells)
    }

    override fun onCellTouched(row: Int, col: Int) {
        if (isTimerRunning) {
            viewModel.sudokuGame.updateSelectedCell(row, col)
        } else {
            Log.d("Sudoku", "Disable cell touch")
        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                isTimerRunning = false
                setInputClickable(false)
                resetTimer()
                textTimeRemaining.text = getString(R.string.time)
                buttonControl.gone()
                viewModel.sudokuGame.checkWin()
            }
        }.start()

        isTimerRunning = true
        buttonControl.visible()
        buttonControl.setImageResource(R.drawable.ic_pause)
        setInputClickable(true)
    }

    private fun pauseTimer() {
        countDownTimer.cancel()
        isTimerRunning = false
        setInputClickable(false)
        updateButtons()
    }

    private fun setInputClickable(isClickable: Boolean) {
        inputs.forEach { it.isClickable = isClickable }
    }

    private fun resetTimer() {
        timeLeftInMillis = startTimeInMillis
        updateCountDownText()
        updateButtons()
    }

    @SuppressLint("SimpleDateFormat")
    private fun updateCountDownText() {
        val hours = timeLeftInMillis / 1000 / 3600
        val minutes = (timeLeftInMillis / 1000 / 60) % 60
        val seconds = (timeLeftInMillis / 1000) % 60

        val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)

        textTimeRemaining.text = timeString
    }

    private fun updateButtons() {
        if (isTimerRunning) {
            buttonControl.setImageResource(R.drawable.ic_pause)
        } else {
            buttonControl.setImageResource(R.drawable.ic_play)
            if (timeLeftInMillis < 1000) buttonControl.gone() else buttonControl.visible()
        }
    }

    private fun showNewGameDialog() {
        val builder = MaterialAlertDialogBuilder(this@SudokuActivity)
        builder.setCancelable(false)
        builder.setTitle(getString(R.string.start_new_game))
        builder.setMessage(getString(R.string.reset_game))
        builder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            startNewGame()
        }
        builder.setNegativeButton(getString(R.string.cancel)) { _, _ ->
            startTimer()
        }
        builder.show()
    }

    private fun showCompletedDialog(isWin: Boolean) {
        val builder = MaterialAlertDialogBuilder(this@SudokuActivity)
        builder.setCancelable(false)
        builder.setTitle(getString(if (isWin) R.string.congratulations else R.string.aw_snap))
        builder.setMessage(getString(if (isWin) R.string.completed_game else R.string.not_completed_game))
        builder.setPositiveButton(getString(R.string.new_game)) { _, _ ->
            startNewGame()
        }
        builder.setNegativeButton(getString(R.string.close), null)
        builder.show()
    }

    private fun startNewGame() {
        resetTimer()
        viewModel.sudokuGame.newGame()
        startTimer()
    }
}