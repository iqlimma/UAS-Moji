package com.moji.v1.ui.journal

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paths = mutableListOf<Pair<Path, Paint>>()
    private val undoStack = mutableListOf<Pair<Path, Paint>>()
    private var currentPath = Path()
    private var currentPaint = createPaint(Color.RED, 8f)

    private fun createPaint(color: Int, strokeWidth: Float): Paint {
        return Paint().apply {
            this.color = color
            this.strokeWidth = strokeWidth
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            isAntiAlias = true
        }
    }

    fun setColor(color: Int) {
        currentPaint = createPaint(color, currentPaint.strokeWidth)
    }

    fun setBrushSize(size: Float) {
        currentPaint = createPaint(currentPaint.color, size)
    }

    fun undo() {
        if (paths.isNotEmpty()) {
            undoStack.add(paths.removeLast())
            invalidate()
        }
    }

    fun redo() {
        if (undoStack.isNotEmpty()) {
            paths.add(undoStack.removeLast())
            invalidate()
        }
    }

    fun clear() {
        paths.clear()
        undoStack.clear()
        currentPath = Path()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for ((path, paint) in paths) {
            canvas.drawPath(path, paint)
        }
        canvas.drawPath(currentPath, currentPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPath = Path()
                currentPath.moveTo(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                currentPath.lineTo(event.x, event.y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                paths.add(Pair(currentPath, currentPaint))
                undoStack.clear()
                currentPath = Path()
            }
        }
        return true
    }
}