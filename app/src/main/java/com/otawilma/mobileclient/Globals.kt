package com.otawilma.mobileclient

import android.content.Context
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.otawilma.mobileclient.storage.DayRepository
import com.otawilma.mobileclient.storage.EncryptedPreferenceStorage
import com.otawilma.mobileclient.storage.MessageRepository
import com.otawilma.mobileclient.storage.PreferenceStorage
import okhttp3.OkHttpClient
import java.time.Duration


const val OTAWILMA_API_URL = "https://beta.wilma-api.tuukk.dev/api"
const val SCHOOLDAY_FILES_DIR_NAME = "schooldays"
const val MESSAGE_FILES_DIR_NAME = "messages"
val JUMP_LESSON_THRESHOLD: Duration = Duration.ofMinutes(30)
val client = OkHttpClient()
var tokenGlobal : String? = null
lateinit var sharedPreferences : PreferenceStorage
lateinit var encryptedPreferenceStorage: EncryptedPreferenceStorage
lateinit var dayRepository: DayRepository
lateinit var messageRepository: MessageRepository
val jackSonMapper = jacksonObjectMapper()

fun initAppData(context: Context){
    sharedPreferences = PreferenceStorage(context)
    encryptedPreferenceStorage = EncryptedPreferenceStorage(context)
    dayRepository = DayRepository(context)
    messageRepository = MessageRepository(context)
    jackSonMapper.findAndRegisterModules()
}