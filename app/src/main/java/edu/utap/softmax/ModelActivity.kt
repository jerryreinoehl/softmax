package edu.utap.softmax

import android.content.Context
import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.activity.viewModels
import edu.utap.softmax.databinding.ActivityModelBinding

class ModelActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    companion object {
        private const val MODEL_ID_KEY = "edu.utap.softmax.MODEL_ID_KEY"

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
            add(R.id.middle_fragment, StatListFragment.newInstance())
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        }

        val modelId = intent.extras?.getString(MODEL_ID_KEY) ?: ""

        viewModel.fetchModel(modelId)
    }
}