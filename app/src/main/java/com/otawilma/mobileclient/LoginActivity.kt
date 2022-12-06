package com.otawilma.mobileclient

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity(), OtawilmaNetworking {

    private val scopeIO = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = PreferenceStorage(this)
        encryptedPreferenceStorage = EncryptedPreferenceStorage(this)

         // Login logic
        val autoLogin = sharedPreferences.autoLogin

        //guard for logging in
        if (autoLogin){
            val storedToken :String? = encryptedPreferenceStorage.otaWilmaToken
            if (storedToken!= null){
                // Try to login with token
            }
            // Try to login with credentials
        }

        //load the login screen
        setContentView(R.layout.activity_login)
        val buttonLogin = findViewById<Button>(R.id.buttonLoginLogin)
        val userNameField = findViewById<EditText>(R.id.editTextTextLoginUsername)
        val passwordField = findViewById<EditText>(R.id.editTextLoginPassword)
        val progressBarLoginStatus = findViewById<ProgressBar>(R.id.progressBarLoginStatus)

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
                    if (result.first){
                        val token = result.second
                        Log.d("Networking","token is: $token")
                        tokenGlobal=token

                        // Store credentials if wanted to
                        if (autoLogin){
                            encryptedPreferenceStorage.otaWilmaToken = tokenGlobal
                            encryptedPreferenceStorage.userName = userName
                            encryptedPreferenceStorage.passWord = password
                        }
                        goToMain()
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
        startActivity(Intent(this,MainActivity::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_login_settings,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_preferences->{
                startActivity(Intent(this,PreferencesActivity::class.java))
            }
            R.id.action_info->{
                // TODO info screen
            }
        }
        return true
    }
}