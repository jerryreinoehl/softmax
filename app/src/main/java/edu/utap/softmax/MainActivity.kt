package edu.utap.softmax

import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

import edu.utap.softmax.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val (address, port, updateSeconds) = SettingsActivity.getResult(result?.data)
            viewModel.setServerAddress(address)
            viewModel.setServerPort(port)
            viewModel.setUpdateSeconds(updateSeconds)
            viewModel.updateSoftmaxClient()
            viewModel.fetchModels()
        }
    }

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

        viewModel.observeNetworkError().observe(this) { networkError ->
            if (networkError) {
                activityMainBinding.swipeRefresh.isRefreshing = false
                Snackbar.make(activityMainBinding.root, "Network Error", Snackbar.LENGTH_SHORT).show()
            }
        }

        viewModel.fetchModels()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_settings -> actionSettings()
            else -> true
        }
    }

    private fun actionSettings(): Boolean {
        val address = viewModel.observeServerAddress().value ?: ""
        val port = viewModel.observeServerPort().value ?: 80
        val seconds = viewModel.observeUpdateSeconds().value ?: 2

        val intent = SettingsActivity.newIntent(this, address, port, seconds)
        resultLauncher.launch(intent)

        return false
    }

    private fun initRecyclerViewDivider(recyclerView: RecyclerView) {
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context, LinearLayoutManager.VERTICAL
        )
        recyclerView.addItemDecoration(dividerItemDecoration)
    }
}