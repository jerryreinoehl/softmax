package edu.utap.softmax.graph

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

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
        private val POINTER_COLOR = Color.rgb(0x60, 0x60, 0x60)
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        textAlign = Paint.Align.LEFT
        textSize = 32.0f
        typeface = Typeface.create("monospace", Typeface.NORMAL)
        isAntiAlias = true
    }

    var adapter: LineGraphAdapter = LineGraphAdapter()
    set(adapter) {
        field = adapter
        field.onSubmitPlotListener = onSubmitPlotListener
        field.onClearListener = onClearListener
    }

    private var paths: MutableList<ColorPath> = mutableListOf()
    private var snapPoints: MutableList<Float> = mutableListOf()
    private var pointerX = 0f

    private val onSubmitPlotListener = object : LineGraphAdapter.OnSubmitPlotListener() {
        override fun onSubmitPlot(plot: Plot) {
            addPlot(plot)
        }
    }

    private val onClearListener = object : LineGraphAdapter.OnClearListener() {
        override fun onClear() {
            paths = mutableListOf()
            snapPoints = mutableListOf()
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.strokeWidth = STROKE_WIDTH

        drawPlots(canvas)
        drawPointer(canvas)
    }

    private fun drawPlots(canvas: Canvas?) {
        paint.style = Paint.Style.STROKE

        paths.forEach { path ->
            paint.color = path.color
            paint.strokeWidth = path.thickness
            canvas?.drawPath(path.path, paint)
        }
    }

    private fun drawPointer(canvas: Canvas?) {
        paint.color = POINTER_COLOR
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = STROKE_WIDTH
        canvas?.drawLine(pointerX, 0f, pointerX, height.toFloat(), paint)
    }

    private fun drawLegend(canvas: Canvas?) {
        paint.color = POINTER_COLOR
        paint.style = Paint.Style.STROKE
        val legendX = pointerX + PADDING
        val legendY = height.toFloat() / 2
        val legendWidth = 200
        val legendHeight = 100

        canvas?.drawRect(
            legendX,
            legendY,
            legendX + legendWidth,
            legendY + legendHeight,
            paint
        )

        paint.style = Paint.Style.FILL
        canvas?.drawText("test", legendX, legendY, paint)
    }

    private fun addPlot(plot: Plot) {
        if (plot.size == 0)
            return
        if (plot[0].size == 0)
            return

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

        val width = width.toFloat() - PADDING * 2
        val height = height.toFloat() - PADDING * 2
        val rangeX = maxX - minX
        val rangeY = maxY - minY

        snapPoints = mutableListOf()

        plot.forEach { points ->
            val snaps: MutableList<Float> = mutableListOf()

            if (points.size < 2)
                return@forEach

            val path = Path()
            path.moveTo(points[0].x, points[0].y)
            path.moveTo(
                width * (points[0].x - minX) / rangeX + PADDING,
                height - height * (points[0].y - minY) / rangeY + PADDING
            )

            points.forEach { point ->
                val x = width * (point.x - minX) / rangeX + PADDING
                val y = height - height * (point.y - minY) / rangeY + PADDING
                path.lineTo(x, y)
                snaps.add(x)
            }

            paths.add(ColorPath(path, points.color, points.thickness))
            mergeSnapPoints(snapPoints, snaps)
        }

        invalidate()
    }

    private fun mergeSnapPoints(lhs: List<Float>, rhs: List<Float>) {
        var l = 0
        var r = 0
        var valueL: Float
        var valueR: Float
        val threshold = 0.01
        var newList: MutableList<Float> = mutableListOf()

        while (true) {
            if ((l >= lhs.size) and (r >= rhs.size)) {
                break
            } else if (l >= lhs.size) {
                newList.add(rhs[r])
                r++
                continue
            } else if (r >= rhs.size){
                newList.add(lhs[l])
                l++
                continue
            }

            valueL = lhs[l]
            valueR = rhs[r]

            if (abs(valueL - valueR) < threshold) {
                newList.add(valueL)
                l++
                r++
            } else if (valueL < valueR) {
                newList.add(valueL)
                l++
            } else {
                newList.add(valueR)
                r++
            }
        }

        snapPoints = newList
    }

    private fun getSnapX(x: Float): Float {
        var mid: Int
        var min = 0
        var max = snapPoints.size
        var distance = width.toFloat()
        var snapX: Float = snapPoints[0]
        var value: Float

        while (min < max) {
            mid = (max + min) / 2
            value = snapPoints[mid]
            if (abs(x - value) < distance) {
                snapX = value
                distance = abs(x - value)
            }

            if (x > value) {
                min = mid + 1
            } else {
                max = mid
            }
        }

        return snapX
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)

        if (snapPoints.size > 1) {
            pointerX = getSnapX(event?.x ?: 0f)
            invalidate()
        }

        return true
    }

    data class ColorPath(val path: Path, val color: Int, val thickness: Float)
}