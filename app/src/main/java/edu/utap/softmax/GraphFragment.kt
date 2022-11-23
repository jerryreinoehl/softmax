package edu.utap.softmax

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import edu.utap.softmax.databinding.FragmentGraphBinding
import edu.utap.softmax.graph.LineGraphAdapter
import edu.utap.softmax.graph.Plot
import edu.utap.softmax.graph.Points

class GraphFragment : Fragment() {

    private var _binding: FragmentGraphBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    companion object {
        fun newInstance(): GraphFragment {
            return GraphFragment()
        }

        private val COLORS = listOf(
            Color.rgb(0xcc, 0x00, 0xcc),
            Color.rgb(0xff, 0x66, 0x00),
            Color.rgb(0x00, 0x99, 0xcc),
            Color.rgb(0x33, 0x33, 0xff),
            Color.rgb(0x33, 0x99, 0x66),
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGraphBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val graphView = binding.graphView
        val graphAdapter = LineGraphAdapter()
        graphView.adapter = graphAdapter

        viewModel.observeModel().observe(viewLifecycleOwner) { model ->
            plot(model, graphAdapter)
        }
    }

    private fun plot(model: SoftmaxClient.Model, graphAdapter: LineGraphAdapter) {
        val lossPlot = Plot()
        val accuracyPlot = Plot()
        var colorIndex = 0

        model.runs.forEach { run ->
            if (!viewModel.isRunEnabled(run))
                return@forEach

            val lossPoints = Points()
            val accuracyPoints = Points()

            lossPoints.color = COLORS[colorIndex++.mod(COLORS.size)]
            accuracyPoints.color = COLORS[colorIndex++.mod(COLORS.size)]

            run.log.forEach { logItem ->
                lossPoints.add(logItem.step.toFloat(), logItem.data.loss)
                accuracyPoints.add(logItem.step.toFloat(), logItem.data.accuracy)
            }

            lossPlot.add(lossPoints)
            accuracyPlot.add(accuracyPoints)
        }

        graphAdapter.clear()

        if (viewModel.lossEnabled)
            graphAdapter.submitPlot(lossPlot)

        if (viewModel.accuracyEnabled)
            graphAdapter.submitPlot(accuracyPlot)
    }
}