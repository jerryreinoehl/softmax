package edu.utap.softmax.graph

class LineGraphAdapter {

    private val plots: MutableList<Plot> = mutableListOf()
    var onSubmitPlotListener: OnSubmitPlotListener? = null

    fun submitPlot(plot: Plot) {
        plots.add(plot)
        onSubmitPlotListener?.onSubmitPlot(plot)
    }

    abstract class OnSubmitPlotListener {
        abstract fun onSubmitPlot(plot: Plot)
    }
}