package com.teyepeee.sudoku.ui.custom

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.teyepeee.sudoku.R
import com.teyepeee.sudoku.game.Cell
import kotlin.math.min

class SudokuBoardView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var sqrtSize = 3
    private var size = 9
    private var cellSizePixels = 0F

    private var selectedRow = 1
    private var selectedCol = 1

    private var listener: OnTouchListener? = null

    private var cells: List<Cell>? = null

    private val borderPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.colorAccent)
        strokeWidth = 2F * Resources.getSystem().displayMetrics.density
    }

    private val blueDarkPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ContextCompat.getColor(context, R.color.cellDark)
    }

    private val blueLightPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = ContextCompat.getColor(context, R.color.cellLight)
    }

    private val selectedCellPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.colorAccent)
        strokeWidth = 2F * Resources.getSystem().displayMetrics.density
    }

    private val textPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.WHITE
        textSize = 20F * Resources.getSystem().displayMetrics.density
        val customTypeface: Typeface
        customTypeface = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val montserratRegular = resources.getFont(R.font.montserrat_bold)
            Typeface.create(montserratRegular, 700, false)
        } else {
            ResourcesCompat.getFont(context, R.font.montserrat_bold)!!
        }
        typeface = customTypeface
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizePixels = min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(sizePixels, sizePixels)
    }

    override fun onDraw(canvas: Canvas) {
        cellSizePixels = (width / size).toFloat()
        fillCells(canvas)
        drawBorders(canvas)
        drawText(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                handleTouchEvent(event.x, event.y)
                performClick()
                true
            }
            else -> false
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun fillCells(canvas: Canvas) {
        cells?.forEach {
            val r = it.row
            val c = it.col

            if (r % 2 == 0) {
                if (c % 2 == 0) {
                    fillCell(canvas, r, c, blueDarkPaint)
                } else {
                    fillCell(canvas, r, c, blueLightPaint)
                }
            } else {
                if (c % 2 == 0) {
                    fillCell(canvas, r, c, blueLightPaint)
                } else {
                    fillCell(canvas, r, c, blueDarkPaint)
                }
            }

            if (selectedRow == r && selectedCol == c) {
                fillBorderCell(canvas, r, c, selectedCellPaint)
            }
        }
    }

    private fun fillCell(canvas: Canvas, r: Int, c: Int, paint: Paint) {
        canvas.drawRect(
            c * cellSizePixels,
            r * cellSizePixels,
            (c + 1) * cellSizePixels,
            (r + 1) * cellSizePixels,
            paint
        )
    }

    private fun fillBorderCell(canvas: Canvas, r: Int, c: Int, paint: Paint) {
        val rectF = RectF(
            c * cellSizePixels,
            r * cellSizePixels,
            (c + 1) * cellSizePixels,
            (r + 1) * cellSizePixels
        )
        val cornerRadius = 4F * Resources.getSystem().displayMetrics.density
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint)
    }

    private fun drawBorders(canvas: Canvas) {
        val rectF = RectF(0F, 0F, width.toFloat(), height.toFloat())
        val cornerRadius = 8F * Resources.getSystem().displayMetrics.density
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, borderPaint)

        for (i in 1 until size) {
            if (i % sqrtSize == 0) {
                canvas.drawLine(
                    i * cellSizePixels,
                    0F,
                    i * cellSizePixels,
                    height.toFloat(),
                    borderPaint
                )

                canvas.drawLine(
                    0F,
                    i * cellSizePixels,
                    width.toFloat(),
                    i * cellSizePixels,
                    borderPaint
                )
            }
        }
    }

    private fun drawText(canvas: Canvas) {
        cells?.forEach {
            val value = it.value

            if (value != 0) {
                val row = it.row
                val col = it.col
                val valueString = it.value.toString()

                val textBounds = Rect()
                textPaint.getTextBounds(valueString, 0, valueString.length, textBounds)
                val textWidth = textPaint.measureText(valueString)
                val textHeight = textBounds.height()

                canvas.drawText(
                    valueString,
                    (col * cellSizePixels) + cellSizePixels / 2 - textWidth / 2,
                    (row * cellSizePixels) + cellSizePixels / 2 + textHeight / 2,
                    textPaint
                )
            }
        }
    }

    private fun handleTouchEvent(x: Float, y: Float) {
        val possibleSelectedRow = (y / cellSizePixels).toInt()
        val possibleSelectedCol = (x / cellSizePixels).toInt()
        listener?.onCellTouched(possibleSelectedRow, possibleSelectedCol)
    }

    fun updateSelectedCellUI(row: Int, col: Int) {
        selectedRow = row
        selectedCol = col
        invalidate()
    }

    fun updateCells(cells: List<Cell>) {
        this.cells = cells
        invalidate()
    }

    fun registerListener(listener: OnTouchListener) {
        this.listener = listener
    }

    interface OnTouchListener {
        fun onCellTouched(row: Int, col: Int)
    }
}