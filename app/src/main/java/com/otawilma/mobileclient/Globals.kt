package com.otawilma.mobileclient

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import java.time.Duration


const val OTAWILMA_API_URL = "https://wilma.otawilma.fi/api/"
const val SCHOOLDAY_FILES_DIR_NAME = "schooldays"
const val MESSAGE_FILES_DIR_NAME = "messages"
val JUMP_LESSON_THRESHOLD: Duration = Duration.ofMinutes(30)
val client = OkHttpClient()
var tokenGlobal : String? = null
val jackSonMapper = jacksonObjectMapper()
const val NEW_MESSAGE_BATCH_SIZE : Int = 10