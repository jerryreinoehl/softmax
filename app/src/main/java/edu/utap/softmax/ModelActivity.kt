package edu.utap.softmax

import android.content.Context
import android.os.Bundle
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import edu.utap.softmax.databinding.ActivityModelBinding
import kotlinx.coroutines.*

class ModelActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val viewModel: MainViewModel by viewModels()
    private var updateInterval = 2000L

    companion object {
        private const val MODEL_ID_KEY = "edu.utap.softmax.ModelActivity.MODEL_ID_KEY"

        fun newIntent(context: Context, modelId: String): Intent {
            return Intent(context, ModelActivity::class.java).apply {
                putExtra(MODEL_ID_KEY, modelId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityModelBinding = ActivityModelBinding.inflate(layoutInflater)
        setContentView(activityModelBinding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager.commit {
            replace(R.id.top_fragment, GraphFragment.newInstance())
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        }

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val navigationFragment = NavigationFragment.newInstance().apply {
                onStatListClick = OnClickListener {
                    startStatListFragment()
                }
                onGraphEditClick = OnClickListener {
                    startGraphEditFragment()
                }
            }
            supportFragmentManager.commit {
                replace(R.id.middle_fragment, StatListFragment.newInstance())
                replace(R.id.bottom_fragment, navigationFragment)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            }
        } else {
            enableImmersiveMode()
        }

        val modelId = intent.extras?.getString(MODEL_ID_KEY) ?: ""

        val sharedPreferences = getSharedPreferences("edu.utap.softmax", Context.MODE_PRIVATE)
        val address = sharedPreferences.getString("serverAddress", "http://jerryr.us") ?: "http://jerryr.us"
        val port = sharedPreferences.getInt("serverPort", 23800)
        val updateSeconds = sharedPreferences.getInt("updateSeconds", 2)

        viewModel.setServerAddress(address)
        viewModel.setServerPort(port)
        viewModel.setUpdateSeconds(updateSeconds)
        viewModel.updateSoftmaxClient()
        updateInterval = updateSeconds * 1000L

        viewModel.observeModel().observe(this) { model ->
            supportActionBar?.title = model.name
        }

        refresh(modelId)
    }
    private fun hideSystemBars() {
        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView) ?: return
        // Configure the behavior of the hidden system bars
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        // Hide both the status bar and the navigation bar
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    private fun enableImmersiveMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // https://developer.android.com/develop/ui/views/layout/immersive
            val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView) ?: return
            // Configure the behavior of the hidden system bars
            windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            // Hide both the status bar and the navigation bar
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        } else {
            // https://stackoverflow.com/questions/63017524/how-can-i-hide-navigation-bar-without-hiding-the-status-bar
            window.decorView.apply {
                systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            }
        }
    }

    private fun startStatListFragment() {
        supportFragmentManager.commit {
            replace(R.id.middle_fragment, StatListFragment.newInstance())
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        }
    }

    private fun startGraphEditFragment() {
        supportFragmentManager.commit {
            replace(R.id.middle_fragment, GraphEditFragment.newInstance())
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        }
    }

    private fun refresh(modelId: String) {
        launch {
            val timerJob = async {
                while (true) {
                    viewModel.fetchModel(modelId)
                    delay(updateInterval)
                }
            }
        }
    }
}