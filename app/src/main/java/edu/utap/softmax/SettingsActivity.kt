package edu.utap.softmax

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import edu.utap.softmax.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }

    private lateinit var activitySettingsBinding: ActivitySettingsBinding

    private var serverAddress = "http://jerryr.us"
    private var serverPort = 80
    private var updateSeconds = 2
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySettingsBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(activitySettingsBinding.root)

        sharedPreferences = getSharedPreferences("edu.utap.softmax", Context.MODE_PRIVATE)
        serverAddress = sharedPreferences.getString("serverAddress", "http://jerryr.us") ?: "http://jerryr.us"
        serverPort = sharedPreferences.getInt("serverPort", 23800)
        updateSeconds = sharedPreferences.getInt("updateSeconds", 2)

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
            sharedPreferences.edit {
                putString("serverAddress", serverAddress)
            }
        }
    }

    private fun portOnClick(view: View) {
        doDialog("Server Port", serverPort.toString(), InputType.TYPE_CLASS_NUMBER) {
            serverPort = it.toInt()
            activitySettingsBinding.portTv.text = serverPort.toString()
            sharedPreferences.edit {
                putInt("serverPort", serverPort)
            }
        }
    }

    private fun secondsOnClick(view: View) {
        doDialog("Update Interval", updateSeconds.toString(), InputType.TYPE_CLASS_NUMBER) {
            updateSeconds = it.toInt()
            activitySettingsBinding.secondsTv.text = "${updateSeconds.toString()} seconds"
            sharedPreferences.edit {
                putInt("updateSeconds", updateSeconds)
            }
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
}