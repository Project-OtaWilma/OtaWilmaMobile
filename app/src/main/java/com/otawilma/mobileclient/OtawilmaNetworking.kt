package com.otawilma.mobileclient

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

interface OtawilmaNetworking {

    // KATA-funktiot:

    // Returns if the Otawilma-server can be reached
    suspend fun pingOtawilma():Boolean{
        return false
    }

    // Returns if Otawilma can reach the wilma-server
    suspend fun pingwilma():Boolean{
        return false
    }

    //KATA-funktiot loppuvat (onneksi)

    // Passes the userName and password as a request and expects back a success and a token
    suspend fun login (userName:String, password:String):Pair<Boolean,String>{

        // Create a JSON object for the user
        val user=JSONObject()
        user.put("username",userName)
        user.put("password",password)

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody:RequestBody=RequestBody.create(mediaType,user.toString())
        val request = Request.Builder().url("$OTAWILMA_API_URL/login").post(requestBody).build()

        client.newCall(request).execute().use {
            //Log.d("Networking",it.body!!.string())
            if (it.isSuccessful) {
                val token = Gson().fromJson<Map<String,String>>(it.body!!.string(), Map::class.java)["token"]
                return Pair(true,token!!)
            }
            return Pair(false,"THE FUCK YOU EXPECT HERE TO BE? A FUCKING TOKEN?")
        }


    }
}