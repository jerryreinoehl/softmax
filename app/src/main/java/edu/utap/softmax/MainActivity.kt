package edu.utap.softmax

import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.fetchModels()
    }
}