package com.otawilma.mobileclient

import android.content.Context


class PreferenceStorage(private val context: Context) {

    private val preferenceFileName = "com.otawilma.mobileclient.preferences"
    private val sharedPreferences = context.getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE)

    var autoLogin
        get() = sharedPreferences.getBoolean("autoLogin", false)
        set(value) {sharedPreferences.edit().putBoolean("autoLogin",value).apply()}

    var homePageDays
        get() = sharedPreferences.getInt("homePageDays",2)
        set(value) {sharedPreferences.edit().putInt("homePageDays",value).apply()}
}