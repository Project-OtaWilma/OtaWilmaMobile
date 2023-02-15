package com.otawilma.mobileclient.storage

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey


class EncryptedPreferenceStorage(context: Context) {

    private val encryptedPreferenceFileName = "com.otawilma.mobileclient.encryptedPreferences"
    private val masterKey = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
    private val encryptedSharedPreferences = EncryptedSharedPreferences(context,encryptedPreferenceFileName,masterKey,EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

    var otaWilmaToken
        get() = encryptedSharedPreferences.getString("otaWilmaToken",null)
        set(value) {encryptedSharedPreferences.edit().putString("otaWilmaToken",value).apply()}

    var userName
        get() = encryptedSharedPreferences.getString("userName",null)
        set(value) {encryptedSharedPreferences.edit().putString("userName",value).apply()}

    var passWord
        get() = encryptedSharedPreferences.getString("passWord",null)
        set(value) {encryptedSharedPreferences.edit().putString("passWord",value).apply()}

    fun wipeEncryptedSharedPreferences(){
        encryptedSharedPreferences.edit().clear().apply()
    }
}