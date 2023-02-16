package com.otawilma.mobileclient

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.OkHttpClient
import java.time.Duration


const val OTAWILMA_API_URL = "https://beta.wilma-api.tuukk.dev/api"
const val SCHOOLDAY_FILES_DIR_NAME = "schooldays"
const val MESSAGE_FILES_DIR_NAME = "messages"
val JUMP_LESSON_THRESHOLD: Duration = Duration.ofMinutes(30)
val client = OkHttpClient()
var tokenGlobal : String? = null
val jackSonMapper = jacksonObjectMapper()

fun initAppData(){
    jackSonMapper.findAndRegisterModules()
}