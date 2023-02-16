package com.otawilma.mobileclient.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.otawilma.mobileclient.OtawilmaNetworking
import com.otawilma.mobileclient.R
import com.otawilma.mobileclient.storage.EncryptedPreferenceStorage
import com.otawilma.mobileclient.storage.PreferenceStorage
import com.otawilma.mobileclient.tokenGlobal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity(), OtawilmaNetworking {

    private lateinit var progressBarLoginStatus : ProgressBar
    private val scopeIO = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences = PreferenceStorage(applicationContext)
        val encryptedPreferenceStorage = EncryptedPreferenceStorage(applicationContext)

        // TODO kovin legacya
        super.onCreate(savedInstanceState)
        val intent = intent
        val loggedOut = intent.getBooleanExtra("loggedOut",false)

        //load the login screen
        setContentView(R.layout.activity_login)
        val buttonLogin = findViewById<Button>(R.id.buttonLoginLogin)
        val userNameField = findViewById<EditText>(R.id.editTextTextLoginUsername)
        val passwordField = findViewById<EditText>(R.id.editTextLoginPassword)
        progressBarLoginStatus = findViewById<ProgressBar>(R.id.progressBarLoginStatus)

         // Login logic
        val autoLogin = sharedPreferences.autoLogin

        //guard for logging in
        if (autoLogin && !loggedOut){
            progressBarLoginStatus.visibility = View.VISIBLE
            val storedToken :String? = encryptedPreferenceStorage.otaWilmaToken
            val storedUserName : String? = encryptedPreferenceStorage.userName
            val storedPassword : String? = encryptedPreferenceStorage.passWord

            scopeIO.launch {

                // Try to login with token
                if (storedToken != null) {
                    if (testToken(storedToken)) {
                        tokenGlobal = storedToken
                        CoroutineScope(Dispatchers.Main).launch {
                            goToMain()
                        }
                        return@launch
                    }
                }

                // Try to login with credentials
                if (storedUserName != null  && storedPassword!=null) {
                    val loginStatus = login(storedUserName,storedPassword)
                    if (loginStatus != null){
                        tokenGlobal = loginStatus
                        encryptedPreferenceStorage.otaWilmaToken = tokenGlobal
                        CoroutineScope(Dispatchers.Main).launch {
                            goToMain()
                        }
                    } else{
                        runOnUiThread {
                            Toast.makeText(this@LoginActivity,"Auto login unsuccessful", Toast.LENGTH_LONG).show()
                            progressBarLoginStatus.visibility = View.INVISIBLE
                        }

                    }
                }
                progressBarLoginStatus.visibility = View.INVISIBLE
            }
        }



        buttonLogin.setOnClickListener{

            //get the login credentials
            val userName = userNameField.text.toString()
            val password = passwordField.text.toString()

            //attempt to login
            if (userName!=""&&password!=""){

                progressBarLoginStatus.visibility = View.VISIBLE
                 scopeIO.launch {

                     val result = login(userName,password)

                    // first = did it succeed?
                    // second = token
                    if (result != null) {
                        Log.d("Networking", "token is: $result")
                        tokenGlobal = result

                        // Store credentials if wanted to
                        if (autoLogin) {
                            encryptedPreferenceStorage.otaWilmaToken = tokenGlobal
                            encryptedPreferenceStorage.userName = userName
                            encryptedPreferenceStorage.passWord = password
                        }
                        CoroutineScope(Dispatchers.Main).launch {
                            goToMain()
                        }
                    }else{
                        runOnUiThread {
                            Toast.makeText(this@LoginActivity, R.string.checkCredentials, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
            //if one the field are empty
            else Toast.makeText(
                this,
                R.string.checkEmptyCredentials,
                Toast.LENGTH_SHORT
            ).show()
        }

        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbarLogin)
        setSupportActionBar(toolbar)
    }

    override fun onDestroy() {
        super.onDestroy()
        // To confirm that we don't leak memory
        scopeIO.cancel()
    }

    private fun goToMain(){
        progressBarLoginStatus.visibility = View.INVISIBLE
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_login_settings,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_preferences ->{
                startActivity(Intent(this, PreferencesActivity::class.java))
            }
            R.id.action_info ->{
                // TODO info screen
            }
        }
        return true
    }
}