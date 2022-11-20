package edu.utap.softmax

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
            graphAdapter.submitPlot(plotFromRun(model))
        }
    }

    private fun plotFromRun(model: SoftmaxClient.Model): Plot {
        val plot = Plot()

        model.runs.forEach { run ->
            val points = Points()

            run.log.forEach { logItem ->
                points.add(logItem.step.toFloat(), logItem.data.loss)
            }

            plot.add(points)
        }

        return plot
    }
}