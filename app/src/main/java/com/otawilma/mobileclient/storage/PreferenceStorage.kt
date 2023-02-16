package com.otawilma.mobileclient.storage

import android.content.Context


class PreferenceStorage(context: Context) {

    private val preferenceFileName = "com.otawilma.mobileclient.preferences"
    private val sharedPreferences = context.getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE)

    var autoLogin
        get() = sharedPreferences.getBoolean("autoLogin", false)
        set(value) {sharedPreferences.edit().putBoolean("autoLogin",value).apply()}

    var enableCache
        get() = sharedPreferences.getBoolean("caching", false)
        set(value) {sharedPreferences.edit().putBoolean("caching", value).apply()}

    var homePageDays
        get() = sharedPreferences.getInt("homePageDays",3)
        set(value) {sharedPreferences.edit().putInt("homePageDays",value).apply()}
}