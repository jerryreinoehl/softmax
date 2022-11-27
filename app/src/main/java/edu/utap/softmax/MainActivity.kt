package edu.utap.softmax

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

import edu.utap.softmax.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        val sharedPreferences = getSharedPreferences("edu.utap.softmax", Context.MODE_PRIVATE)
        val address = sharedPreferences.getString("serverAddress", "http://jerryr.us") ?: "http://jerryr.us"
        val port = sharedPreferences.getInt("serverPort", 23800)
        val updateSeconds = sharedPreferences.getInt("updateSeconds", 2)
        viewModel.setServerAddress(address)
        viewModel.setServerPort(port)
        viewModel.setUpdateSeconds(updateSeconds)
        viewModel.updateSoftmaxClient()

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
        val intent = SettingsActivity.newIntent(this)
        startActivity(intent)

        return false
    }

    private fun initRecyclerViewDivider(recyclerView: RecyclerView) {
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context, LinearLayoutManager.VERTICAL
        )
        recyclerView.addItemDecoration(dividerItemDecoration)
    }
}