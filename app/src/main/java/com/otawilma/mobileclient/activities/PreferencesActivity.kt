package com.otawilma.mobileclient.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.otawilma.mobileclient.R
import com.otawilma.mobileclient.encryptedPreferenceStorage
import com.otawilma.mobileclient.sharedPreferences

class PreferencesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        val switchAutoLogin = findViewById<Switch>(R.id.switchPreferencesAutoLogin)
        val buttonWipeEncryptedPreferenceStorage = findViewById<Button>(R.id.buttonWipeEncryptedStorage)
        val editTextHomePageDays = findViewById<EditText>(R.id.editTextNumberAmoutOfDays)

        // TODO implement later
        val switchCache = findViewById<Switch>(R.id.switchPreferencesCache)

        switchAutoLogin.isChecked= sharedPreferences.autoLogin
        editTextHomePageDays.hint= sharedPreferences.homePageDays.toString()

        switchAutoLogin.setOnClickListener {
            sharedPreferences.autoLogin=switchAutoLogin.isChecked
        }

        buttonWipeEncryptedPreferenceStorage.setOnClickListener {
            encryptedPreferenceStorage.wipeEncryptedSharedPreferences()
        }

        editTextHomePageDays.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                try {
                    sharedPreferences.homePageDays = p0.toString().toInt()
                    Toast.makeText(this@PreferencesActivity,"Added ${sharedPreferences.homePageDays} to sharedPreferences",Toast.LENGTH_SHORT).show()
                }catch (e: Exception){
                    when(e){
                        is NullPointerException, is NumberFormatException -> Toast.makeText(this@PreferencesActivity, "Not convertable", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        })
    }
}