package com.otawilma.mobileclient.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.otawilma.mobileclient.MESSAGE_FILES_DIR_NAME
import com.otawilma.mobileclient.R
import com.otawilma.mobileclient.SCHOOLDAY_FILES_DIR_NAME
import com.otawilma.mobileclient.storage.EncryptedPreferenceStorage
import com.otawilma.mobileclient.storage.PreferenceStorage

class PreferencesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val encryptedPreferenceStorage = EncryptedPreferenceStorage(applicationContext)
        val sharedPreferences = PreferenceStorage(applicationContext)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        val switchAutoLogin = findViewById<SwitchCompat>(R.id.switchPreferencesAutoLogin)
        val buttonWipeEncryptedPreferenceStorage = findViewById<Button>(R.id.buttonWipeEncryptedStorage)
        val editTextHomePageDays = findViewById<EditText>(R.id.editTextNumberAmoutOfDays)
        val buttonReturn = findViewById<Button>(R.id.buttonPrefReturn)

        // TODO implement later
        val switchCache = findViewById<SwitchCompat>(R.id.switchPreferencesCache)
        val buttonWipeCaches = findViewById<Button>(R.id.buttonWipeCaches)

        switchAutoLogin.isChecked = sharedPreferences.autoLogin
        editTextHomePageDays.hint = sharedPreferences.homePageDays.toString()

        switchAutoLogin.setOnClickListener {
            sharedPreferences.autoLogin=switchAutoLogin.isChecked
        }

        buttonWipeEncryptedPreferenceStorage.setOnClickListener {
            encryptedPreferenceStorage.wipeEncryptedSharedPreferences()
        }
        buttonWipeCaches.setOnClickListener {
            val dirMessages = applicationContext.getDir(MESSAGE_FILES_DIR_NAME, MODE_PRIVATE)
            dirMessages.deleteRecursively()
            val dirSchedule = applicationContext.getDir(SCHOOLDAY_FILES_DIR_NAME, MODE_PRIVATE)
            dirSchedule.deleteRecursively()
            if (!dirMessages.exists() && !dirSchedule.exists()) Toast.makeText(applicationContext, "Caches deleted successfully", Toast.LENGTH_SHORT).show()
        }
        switchCache.isChecked = sharedPreferences.enableCache
        switchCache.setOnClickListener {
            sharedPreferences.enableCache = switchCache.isChecked
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