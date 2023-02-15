package com.otawilma.mobileclient.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.otawilma.mobileclient.*

class PreferencesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        val switchAutoLogin = findViewById<Switch>(R.id.switchPreferencesAutoLogin)
        val buttonWipeEncryptedPreferenceStorage = findViewById<Button>(R.id.buttonWipeEncryptedStorage)
        val editTextHomePageDays = findViewById<EditText>(R.id.editTextNumberAmoutOfDays)
        val buttonReturn = findViewById<Button>(R.id.buttonPrefReturn)

        // TODO implement later
        val switchCache = findViewById<Switch>(R.id.switchPreferencesCache)
        val buttonWipeCaches = findViewById<Button>(R.id.buttonWipeCaches)

        switchAutoLogin.isChecked= sharedPreferences.autoLogin
        editTextHomePageDays.hint= sharedPreferences.homePageDays.toString()

        switchAutoLogin.setOnClickListener {
            sharedPreferences.autoLogin=switchAutoLogin.isChecked
        }

        buttonWipeEncryptedPreferenceStorage.setOnClickListener {
            encryptedPreferenceStorage.wipeEncryptedSharedPreferences()
        }
        buttonWipeCaches.setOnClickListener {
            val dirMessages = applicationContext.getDir(MESSAGE_FILES_DIR_NAME, MODE_PRIVATE)
            dirMessages.delete()
            val dirSchedule = applicationContext.getDir(SCHOOLDAY_FILES_DIR_NAME, MODE_PRIVATE)
            dirSchedule.delete()

        }

        editTextHomePageDays.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                try {
                    sharedPreferences.homePageDays = p0.toString().toInt()
                }catch (e: Exception){
                    when(e){
                        is NullPointerException, is NumberFormatException -> Toast.makeText(this@PreferencesActivity, "Please type in a number", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        buttonReturn.setOnClickListener {
            finish()
        }

    }
}