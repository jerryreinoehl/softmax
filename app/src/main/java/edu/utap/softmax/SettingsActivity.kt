package edu.utap.softmax

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.view.MenuItem
import android.view.View
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

        fun getResult(intent: Intent?): SettingsActivityResult {
            val address = intent?.extras?.getString(ADDRESS_KEY) ?: ""
            val port = intent?.extras?.getInt(PORT_KEY) ?: 443
            val updateSeconds = intent?.extras?.getInt(UPDATE_SECONDS_KEY) ?: 2
            return SettingsActivityResult(address, port, updateSeconds)
        }
    }

    data class SettingsActivityResult(val address: String, val port: Int, val updateSeconds: Int)

    private lateinit var activitySettingsBinding: ActivitySettingsBinding

    private var serverAddress = "http://jerryr.us"
    private var serverPort = 80
    private var updateSeconds = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySettingsBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(activitySettingsBinding.root)

        serverAddress = intent.extras?.getString(ADDRESS_KEY, "http://jerryr.us") ?: "http://jerryr.us"
        serverPort = intent.extras?.getInt(PORT_KEY, 80) ?: 80
        updateSeconds = intent.extras?.getInt(UPDATE_SECONDS_KEY, 2) ?: 2

        activitySettingsBinding.addressTv.text = serverAddress
        activitySettingsBinding.portTv.text = serverPort.toString()
        activitySettingsBinding.secondsTv.text = "$updateSeconds seconds"

        activitySettingsBinding.addressLayout.setOnClickListener(::addressOnClick)
        activitySettingsBinding.portLayout.setOnClickListener(::portOnClick)
        activitySettingsBinding.secondsLayout.setOnClickListener(::secondsOnClick)
    }

    private fun addressOnClick(view: View) {
        doDialog("Server Address", serverAddress) {
            serverAddress = it
            activitySettingsBinding.addressTv.text = serverAddress
        }
    }

    private fun portOnClick(view: View) {
        doDialog("Server Port", serverPort.toString(), InputType.TYPE_CLASS_NUMBER) {
            serverPort = it.toInt()
            activitySettingsBinding.portTv.text = serverPort.toString()
        }
    }

    private fun secondsOnClick(view: View) {
        doDialog("Update Interval", updateSeconds.toString(), InputType.TYPE_CLASS_NUMBER) {
            updateSeconds = it.toInt()
            activitySettingsBinding.secondsTv.text = "${updateSeconds.toString()} seconds"
        }
    }

    private fun doDialog(
        title: String,
        initialText: String,
        inputType: Int = InputType.TYPE_CLASS_TEXT,
        onOk: (String) -> Unit
    ) {
        val editText = EditText(this).apply {
            setText(initialText)
            textAlignment = EditText.TEXT_ALIGNMENT_CENTER
            this.inputType = inputType
        }

        // https://alvinalexander.com/source-code/android-mockup-prototype-dialog-text-field/
        val dialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setView(editText)
            .setPositiveButton("Ok") { dialog, which ->
                onOk(editText.text.toString())
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun doFinish() {
        val returnIntent = Intent().apply {
            putExtra(ADDRESS_KEY, serverAddress)
            putExtra(PORT_KEY, serverPort)
            putExtra(UPDATE_SECONDS_KEY, updateSeconds)
        }
        setResult(RESULT_OK, returnIntent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { doFinish(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }
}