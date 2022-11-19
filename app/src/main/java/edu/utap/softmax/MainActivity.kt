package edu.utap.softmax

import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import edu.utap.softmax.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        val adapter = ModelRowAdapter() { model ->
            viewModel.fetchModel(model.modelId)
        }

        activityMainBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        activityMainBinding.recyclerView.adapter = adapter
        initRecyclerViewDivider(activityMainBinding.recyclerView)

        viewModel.observeModels().observe(this) { models ->
            adapter.submitList(models)
        }

        viewModel.fetchModels()
    }

    private fun initRecyclerViewDivider(recyclerView: RecyclerView) {
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context, LinearLayoutManager.VERTICAL
        )
        recyclerView.addItemDecoration(dividerItemDecoration)
    }
}