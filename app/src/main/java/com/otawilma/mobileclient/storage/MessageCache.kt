package com.otawilma.mobileclient.storage

import android.content.Context
import com.otawilma.mobileclient.MESSAGE_FILES_DIR_NAME
import com.otawilma.mobileclient.dataClasses.Message
import com.otawilma.mobileclient.jackSonMapper
import okio.FileNotFoundException
import java.io.File

interface MessageCache {


    fun getStoredMessages(context : Context, until : Int) : List<Message>?{
        return try {
            val dir = context.getDir(MESSAGE_FILES_DIR_NAME, Context.MODE_PRIVATE)
            val fileList = dir.listFiles()
            val messageItemList : MutableList<Message> = mutableListOf()
            for (i in fileList){
                val item = jackSonMapper.readValue(i, Message::class.java)
                messageItemList.add(item)
            }
            messageItemList
        } catch (e : FileNotFoundException){
            null
        }
    }

    fun storeMessage(context: Context, message: Message){
        val dir = context.getDir(MESSAGE_FILES_DIR_NAME, Context.MODE_PRIVATE)
        val file = File(dir, "${message.timestamp}")

        file.writeText(jackSonMapper.writeValueAsString(message))
    }
}