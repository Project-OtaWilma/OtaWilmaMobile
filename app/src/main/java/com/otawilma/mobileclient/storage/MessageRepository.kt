package com.otawilma.mobileclient.storage

import android.content.Context
import com.otawilma.mobileclient.OtawilmaNetworking
import com.otawilma.mobileclient.dataClasses.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class MessageRepository (private val context: Context) : OtawilmaNetworking, MessageCache{

    private var messageMem : List<Message> = listOf()

    fun messageFlow(until : Int) : Flow<List<Message>> = flow {
        emit(getCachedList(until)?: listOf())

        repeatUntilSuccess(context, waitUntilToken(context)) { token ->
            emit(getMessagesFromServer(token, until))
        }
    }

    private fun getCachedList(until: Int) : List<Message>?{
        if (until <= messageMem.size) return messageMem.subList(0,until)

        messageMem = getStoredMessages(context, until) ?: listOf()
        if (until <= messageMem.size) return messageMem.subList(0,until)

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