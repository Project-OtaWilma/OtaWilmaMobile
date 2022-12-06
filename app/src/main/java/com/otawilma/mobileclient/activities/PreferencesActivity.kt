package com.otawilma.mobileclient.activities

import android.os.Bundle
import android.widget.Button
import android.widget.Switch
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

        // TODO implement later
        val switchCache = findViewById<Switch>(R.id.switchPreferencesCache)

        switchAutoLogin.isChecked= sharedPreferences.autoLogin

        switchAutoLogin.setOnClickListener {
            sharedPreferences.autoLogin=switchAutoLogin.isChecked
        }

        buttonWipeEncryptedPreferenceStorage.setOnClickListener {
            encryptedPreferenceStorage.wipeEncryptedSharedPreferences()
        }

    }
}