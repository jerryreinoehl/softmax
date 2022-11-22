package edu.utap.softmax

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
        initRecyclerViewDivider(binding.recyclerview)

        viewModel.observeRun().observe(viewLifecycleOwner) { run ->
            adapter.submitList(run.log)
            binding.runNumTv.text = viewModel.getRunNum().toString()
            binding.timestampTv.text = run.timestamp
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

    private fun initRecyclerViewDivider(recyclerView: RecyclerView) {
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context, LinearLayoutManager.VERTICAL
        )
        recyclerView.addItemDecoration(dividerItemDecoration)
    }
}