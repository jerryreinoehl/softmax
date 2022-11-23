package edu.utap.softmax

import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import android.os.Bundle
import android.view.Menu
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
            val modelActivity = ModelActivity.newIntent(this, model.modelId)
            startActivity(modelActivity)
        }

        activityMainBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        activityMainBinding.recyclerView.adapter = adapter
        initRecyclerViewDivider(activityMainBinding.recyclerView)

        activityMainBinding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchModels()
        }

        viewModel.observeModels().observe(this) { models ->
            adapter.submitList(models)
            activityMainBinding.swipeRefresh.isRefreshing = false
        }

        viewModel.fetchModels()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    private fun initRecyclerViewDivider(recyclerView: RecyclerView) {
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context, LinearLayoutManager.VERTICAL
        )
        recyclerView.addItemDecoration(dividerItemDecoration)
    }
}