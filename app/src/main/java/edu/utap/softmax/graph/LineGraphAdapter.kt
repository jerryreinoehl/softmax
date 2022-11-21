package edu.utap.softmax.graph

class LineGraphAdapter {

    private val plots: MutableList<Plot> = mutableListOf()
    var onSubmitPlotListener: OnSubmitPlotListener? = null
    var onClearListener: OnClearListener? = null

    fun submitPlot(plot: Plot) {
        plots.add(plot)
        onSubmitPlotListener?.onSubmitPlot(plot)
    }

    fun clear() {
        onClearListener?.onClear()
    }

    abstract class OnSubmitPlotListener {
        abstract fun onSubmitPlot(plot: Plot)
    }

    abstract class OnClearListener {
        abstract fun onClear()
    }
}