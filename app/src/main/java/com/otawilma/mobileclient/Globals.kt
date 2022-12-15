package com.otawilma.mobileclient

import okhttp3.OkHttpClient
import java.time.Duration


const val OTAWILMA_API_URL = "https://beta.wilma-api.tuukk.dev/api"
val JUMP_LESSON_THRESHOLD: Duration = Duration.ofMinutes(30)
val client = OkHttpClient()
lateinit var tokenGlobal : String
lateinit var sharedPreferences : PreferenceStorage
lateinit var encryptedPreferenceStorage: EncryptedPreferenceStorage
lateinit var dayRepository: DayRepository