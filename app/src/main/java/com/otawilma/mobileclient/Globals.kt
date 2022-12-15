package com.otawilma.mobileclient

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import java.time.Duration


const val OTAWILMA_API_URL = "https://beta.wilma-api.tuukk.dev/api"
const val SchoolDay_FILES_DIR_NAME = "schooldays"
val JUMP_LESSON_THRESHOLD: Duration = Duration.ofMinutes(30)
val client = OkHttpClient()
lateinit var tokenGlobal : String
lateinit var sharedPreferences : PreferenceStorage
lateinit var encryptedPreferenceStorage: EncryptedPreferenceStorage
lateinit var dayRepository: DayRepository
val jackSonMapper = jacksonObjectMapper()
