package edu.utap.softmax

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager

import edu.utap.softmax.databinding.FragmentStatListBinding

class StatListFragment: Fragment() {

    private var _binding: FragmentStatListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    companion object {
        fun newInstance(): StatListFragment {
            return StatListFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = StatListAdapter()

        binding.recyclerview.layoutManager = LinearLayoutManager(context)
        binding.recyclerview.adapter = adapter

        viewModel.observeRun().observe(viewLifecycleOwner) { run ->
            adapter.submitList(run.log)
        }

        binding.recyclerview.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {
            override fun onSwipeLeft() {
                println("OnSwipeLeft")
                viewModel.prevRun()
            }

            override fun onSwipeRight() {
                println("OnSwipeRight")
                viewModel.nextRun()
            }
        })
    }
}