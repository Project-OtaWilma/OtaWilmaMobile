package com.otawilma.mobileclient.storage

import android.content.Context
import com.otawilma.mobileclient.NEW_MESSAGE_BATCH_SIZE
import com.otawilma.mobileclient.OtawilmaNetworking
import com.otawilma.mobileclient.dataClasses.Message
import com.otawilma.mobileclient.jackSonMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class MessageRepository (private val context: Context) : OtawilmaNetworking, MessageCache{

    init {
        jackSonMapper.findAndRegisterModules()
    }

    private var messageMem : List<Message> = listOf()

    val newMessageFlow : Flow<List<Message>> = flow {
        // Emit cache
        emit(doWhileNewMessages { getCachedList(it) })

        // Loop the networkRequest
        while (true){
            repeatUntilSuccess(context, waitUntilToken(context)) { t ->
                val newMessages = doWhileNewMessages {
                    getMessagesFromServer(t, it)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    for (message in newMessages){storeMessage(context, message)}
                }
                emit(newMessages)
            }

            // Wait for 5 minutes
            delay(300000)
        }
    }

    private suspend fun doWhileNewMessages(getMessageMethod : suspend (Int) -> List<Message>?) : List<Message>{
        var i = 1
        while (true) {
            val cachedMessages = getMessageMethod(NEW_MESSAGE_BATCH_SIZE * i)?: listOf()
            val cachedNewMessages = cachedMessages.filter { it.new }
            if (cachedNewMessages.size != cachedMessages.size) return cachedNewMessages
            i++
        }

    }

    fun messageFlow(until : Int) : Flow<List<Message>> = flow {
        emit(getCachedList(until)?: listOf())

        repeatUntilSuccess(context, waitUntilToken(context)) { token ->
            emit(getMessagesFromServer(token, until))
        }
    }

    private fun getCachedList(until: Int) : List<Message>?{
        if (until <= messageMem.size) return messageMem.take(until)

        messageMem = getStoredMessages(context, until) ?: listOf()
        if (until <= messageMem.size) return messageMem.take(until)

        return null
    }

    private suspend fun getMessagesFromServer(token: String, until: Int) : List<Message>{
        val messages = getMessages(token, until)
        CoroutineScope(Dispatchers.IO).launch {
            for (message in messages){
                storeMessage(context, message)
            }
        }
        messageMem = messages
        return messages
    }
}