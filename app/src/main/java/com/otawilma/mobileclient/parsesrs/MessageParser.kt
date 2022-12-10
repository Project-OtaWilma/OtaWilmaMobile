package com.otawilma.mobileclient.parsesrs

import android.text.Html
import com.otawilma.mobileclient.dataClasses.Message
import com.otawilma.mobileclient.dataClasses.Person
import org.json.JSONArray
import org.json.JSONObject

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface MessageParser : PersonParser {

    fun parseMessageList(messageListJSONArray: JSONArray) : List<Message>{

        val messageMutableList: MutableList<Message> = mutableListOf()
        for (messageListJSONArrayIndex in 0 until messageListJSONArray.length()){
            messageMutableList.add(parseToMessage(messageListJSONArray[messageListJSONArrayIndex] as JSONObject))
        }
        return messageMutableList
    }

    fun parseToMessage(messageJson : JSONObject) : Message{
        val id  = messageJson["id"] as Int
        val subject = messageJson["subject"] as String
        val timestamp: LocalDateTime = LocalDateTime.parse(messageJson["timeStamp"] as String,
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        val senders: List<Person> = parsePersonList(messageJson["senders"] as JSONArray)
        val body: Html? = null
        val recipients: List<Person>? = null
        val new: Boolean = messageJson["new"] as Boolean

        return Message(id, subject, timestamp, senders, body, recipients, new)
    }
}