package edu.utap.softmax

import android.content.Context
import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

class ModelActivity : AppCompatActivity() {

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
    }
}