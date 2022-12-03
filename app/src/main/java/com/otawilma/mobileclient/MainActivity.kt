package com.otawilma.mobileclient

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), OtawilmaNetworking {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //change later:
        val loggedIn = false

        //guard for logging in
        if (loggedIn) goToMain()

        //load the loginscreen
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
                val result = login(userName,password)

                // first = did it succeed?
                // second = token
                if (result.first){
                    //TODO handle token
                    goToMain()
                }else{
                    Toast.makeText(this, R.string.checkCredentials, Toast.LENGTH_SHORT).show()
                }
            }

            //if one the field are empty
            else Toast.makeText(
                this,
                R.string.checkCredentials,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun goToMain(){

    }
}