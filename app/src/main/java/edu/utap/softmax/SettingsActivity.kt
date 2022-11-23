package edu.utap.softmax

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import edu.utap.softmax.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    companion object {
        private const val ADDRESS_KEY = "edu.utap.softmax.SettingsActivity.ADDRESS_KEY"
        private const val PORT_KEY = "edu.utap.softmax.SettingsActivity.PORT_KEY"
        private const val UPDATE_SECONDS_KEY = "edu.utap.softmax.SettingsActivity.UPDATE_SECONDS_KEY"

        fun newIntent(context: Context, address: String, port: Int, seconds: Int): Intent {
            return Intent(context, SettingsActivity::class.java).apply {
                putExtra(ADDRESS_KEY, address)
                putExtra(PORT_KEY, port)
                putExtra(UPDATE_SECONDS_KEY, seconds)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activitySettingsBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(activitySettingsBinding.root)

        val serverAddress = intent.extras?.getString(ADDRESS_KEY, "http://jerryr.us")
        val serverPort = intent.extras?.getInt(PORT_KEY, 80)
        val updateSeconds = intent.extras?.getInt(UPDATE_SECONDS_KEY, 2)

        activitySettingsBinding.addressTv.text = serverAddress
        activitySettingsBinding.portTv.text = serverPort.toString()
        activitySettingsBinding.secondsTv.text = "$updateSeconds seconds"

        val editText = EditText(this)

        val dialog = AlertDialog.Builder(this)
            .setTitle("title")
            .setMessage("Set server address")
            .setView(editText)
            .setPositiveButton("Add") { dialog, which ->
                println(editText.text)
            }
            .setNegativeButton("Cancel", null)
            .create()
        //dialog.show()
    }
}