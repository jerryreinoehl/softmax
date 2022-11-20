package edu.utap.softmax.graph

class Points {
    private val points: MutableList<Point> = mutableListOf()

    fun add(point: Point) {
        points.add(point)
    }

    fun add(x: Float, y: Float) {
        points.add(Point(x, y))
    }

    fun forEach(iterator: (Point) -> Unit) = points.forEach(iterator)

    operator fun get(i: Int): Point = points[i]
}