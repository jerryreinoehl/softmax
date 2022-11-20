package edu.utap.softmax.graph

class Plot {
    private val plot: MutableList<Points> = mutableListOf()

    fun add(points: Points) {
        plot.add(points)
    }

    fun size(): Int {
        return plot.size
    }

    fun forEach(iterator: (Points) -> Unit) = plot.forEach(iterator)

    operator fun get(i: Int): Points = plot[i]
}