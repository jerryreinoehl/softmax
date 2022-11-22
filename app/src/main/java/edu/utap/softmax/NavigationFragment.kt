package edu.utap.softmax

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.utap.softmax.databinding.FragmentNavigationBinding

class NavigationFragment : Fragment() {

    companion object {
        fun newInstance(): NavigationFragment {
            return NavigationFragment()
        }
    }

    private var _binding: FragmentNavigationBinding? = null
    private val binding get() = _binding!!

    var onStatListClick: OnClickListener? = null
    set(onClickListener) {
        field = onClickListener
    }

    var onGraphEditClick: OnClickListener? = null
    set(onClickListener) {
        field = onClickListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNavigationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.statListBut.setOnClickListener(onStatListClick)
        binding.graphEditBut.setOnClickListener(onGraphEditClick)
    }
}