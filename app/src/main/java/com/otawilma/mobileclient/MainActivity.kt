package com.otawilma.mobileclient

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), OtawilmaNetworking {

    private val scopeIO = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //TODO change later:
        val loggedIn = false

        //guard for logging in
        if (loggedIn) goToMain()

        //load the login screen
        setContentView(R.layout.activity_login)
        val buttonLogin = findViewById<Button>(R.id.buttonLoginLogin)
        val userNameField = findViewById<EditText>(R.id.editTextTextLoginUsername)
        val passwordField = findViewById<EditText>(R.id.editTextLoginPassword)

        buttonLogin.setOnClickListener{

            //get the login credentials
            val userName = userNameField.text.toString()
            val password = passwordField.text.toString()

            //attempt to login
            if (userName!=""&&password!=""){

                 scopeIO.launch {

                     val result = login(userName,password)

                    // first = did it succeed?
                    // second = token
                    if (result.first){
                        val token = result.second
                        Log.d("Networking","token is: $token")
                        tokenGlobal=token
                        goToMain()
                    }else{
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, R.string.checkCredentials, Toast.LENGTH_SHORT).show()
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
    }

    override fun onDestroy() {
        super.onDestroy()
        scopeIO.cancel()
    }

    private fun goToMain(){

    }
}