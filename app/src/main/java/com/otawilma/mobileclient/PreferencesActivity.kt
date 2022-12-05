package com.otawilma.mobileclient

import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class PreferencesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        val switchAutoLogin = findViewById<Switch>(R.id.switchPreferencesAutoLogin)

        // TODO implement later
        val switchCache = findViewById<Switch>(R.id.switchPreferencesCache)

        switchAutoLogin.isChecked= sharedPreferences.autoLogin

        switchAutoLogin.setOnClickListener {
            sharedPreferences.autoLogin=switchAutoLogin.isChecked
        }
    }
}