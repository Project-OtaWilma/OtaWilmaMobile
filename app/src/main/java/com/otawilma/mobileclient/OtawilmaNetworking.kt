package com.otawilma.mobileclient

interface OtawilmaNetworking {

    // KATA-funktiot:

    // Returns if the Otawilma-server can be reached
    fun pingOtawilma():Boolean{
        return false
    }

    // Returns if Otawilma can reach the wilma-server
    fun pingwilma():Boolean{
        return false
    }

    //KATA-funktiot loppuvat (onneksi)

    // Passes the userName and password as a request and expects back a success and a token
    fun login (userName:String, password:String):Pair<Boolean,String>{

        return Pair(false,"THE FUCK YOU EXPECT HERE TO BE? A FUCKING TOKEN?")
    }
}