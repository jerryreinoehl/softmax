package edu.utap.softmax

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import edu.utap.softmax.databinding.FragmentGraphEditBinding

class GraphEditFragment : Fragment() {

    companion object {
        fun newInstance(): GraphEditFragment {
            return GraphEditFragment()
        }
    }

    private var _binding: FragmentGraphEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGraphEditBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = GraphEditRowAdapter { run, selected ->

        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.observeModel().observe(viewLifecycleOwner) { model ->
            adapter.submitList(model.runs)
        }
    }
}