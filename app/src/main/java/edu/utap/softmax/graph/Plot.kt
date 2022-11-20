package edu.utap.softmax.graph

class Plot {
    private val plot: MutableList<Points> = mutableListOf()
    val size get() = plot.size


    fun add(points: Points) {
        plot.add(points)
    }

    fun forEach(iterator: (Points) -> Unit) = plot.forEach(iterator)

    operator fun get(i: Int): Points = plot[i]
}