package com.otawilma.mobileclient

import okhttp3.OkHttpClient

const val OTAWILMA_API_URL = "https://beta.wilma-api.tuukk.dev/api/"
val client = OkHttpClient()
lateinit var tokenGlobal : String
lateinit var sharedPreferences : PreferenceStorage