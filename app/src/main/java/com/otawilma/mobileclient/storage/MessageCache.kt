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
            messageItemList.sortedBy {
                it.timestamp
            }.reversed().take(until)
        } catch (e : FileNotFoundException){
            null
        }
    }

    fun getStoredMessageBody(context: Context, message: Message) : String?{
        return try {
            val dir = context.getDir(MESSAGE_FILES_DIR_NAME, Context.MODE_PRIVATE)
            val file = File(dir, "${message.id}")
            jackSonMapper.readValue(file, Message::class.java).body
        } catch (e : FileNotFoundException) {
            null
        }
    }

    fun storeMessage(context: Context, message: Message){
        val preferenceStorage = PreferenceStorage(context)
        if (!preferenceStorage.enableCache) return
        val dir = context.getDir(MESSAGE_FILES_DIR_NAME, Context.MODE_PRIVATE)
        val file = File(dir, "${message.id}")
        try {
            val storedMessage = jackSonMapper.readValue(file, Message::class.java)
            message.body = storedMessage.body ?: message.body
        } catch (_: FileNotFoundException){}
        finally {
            file.writeText(jackSonMapper.writeValueAsString(message))
        }
    }
}