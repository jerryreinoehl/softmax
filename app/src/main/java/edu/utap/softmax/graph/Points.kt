package edu.utap.softmax.graph

import android.graphics.Color

class Points {
    private val points: MutableList<Point> = mutableListOf()
    val size get() = points.size
    var color: Int = Color.RED
    var thickness: Float = 4f

    fun add(point: Point) {
        points.add(point)
    }

    fun add(x: Float, y: Float) {
        points.add(Point(x, y))
    }

    fun forEach(iterator: (Point) -> Unit) = points.forEach(iterator)

    operator fun get(i: Int): Point = points[i]
}