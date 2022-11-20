package edu.utap.softmax.graph

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View

class LineGraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val PADDING = 10
        private const val STROKE_WIDTH = 4f
        private val COLORS = listOf(
            Color.rgb(0xcc, 0x00, 0xcc),
            Color.rgb(0xff, 0x66, 0x00),
            Color.rgb(0x00, 0x99, 0xcc),
            Color.rgb(0x33, 0x33, 0xff),
            Color.rgb(0x33, 0x99, 0x66),
        )
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
        isAntiAlias = true
    }

    var adapter: LineGraphAdapter = LineGraphAdapter()
    set(adapter) {
        field = adapter
        field.onSubmitPlotListener = onSubmitPlotListener
    }

    private var paths: MutableList<Path> = mutableListOf()

    private val onSubmitPlotListener = object : LineGraphAdapter.OnSubmitPlotListener() {
        override fun onSubmitPlot(plot: Plot) {
            addPlot(plot)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.strokeWidth = STROKE_WIDTH

        var colorIndex = 0

        paths.forEach { path ->
            paint.color = COLORS[colorIndex++.mod(COLORS.size)]
            canvas?.drawPath(path, paint)
        }
    }

    private fun addPlot(plot: Plot) {
        var minX = plot[0][0].x
        var minY = plot[0][0].y
        var maxX = minX
        var maxY = minY

        plot.forEach { points ->
            points.forEach { point ->
                if (point.x < minX)
                    minX = point.x
                else if (point.x > maxX)
                    maxX = point.x

                if (point.y < minY)
                    minY = point.y
                else if (point.y > maxY)
                    maxY = point.y
            }
        }

        paths = mutableListOf()
        val width = width.toFloat() - PADDING * 2
        val height = height.toFloat() - PADDING * 2

        plot.forEach { points ->
            val path = Path()
            path.moveTo(points[0].x, points[0].y)
            path.moveTo(
                width * points[0].x / (maxX - minX) + PADDING,
                height - height * points[0].y / (maxY - minY) + PADDING
            )

            points.forEach { point ->
                val x = width * point.x / (maxX - minX) + PADDING
                val y = height - height * point.y / (maxY - minY) + PADDING
                path.lineTo(x, y)
            }

            paths.add(path)
        }

        invalidate()
    }
}