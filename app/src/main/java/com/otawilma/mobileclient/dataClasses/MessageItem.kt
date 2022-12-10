package com.otawilma.mobileclient.dataClasses

import android.text.Html
import java.time.LocalDateTime


abstract class MessageItem (
    open val id : Int,
    open val subject: String,
    open val timestamp: LocalDateTime,
    open val senders : List<Person>,
    open val body: Html?
    )

data class Message(
    override val id: Int,
    override val subject: String,
    override val timestamp: LocalDateTime,
    override val senders: List<Person>,
    override val body: Html?,
    val recipients : List<Person>?,
    val new : Boolean
):MessageItem(id, subject,
    timestamp,
    senders,
    body
)

data class Appointment(
    override val id: Int,
    override val subject: String,
    override val timestamp: LocalDateTime,
    override val senders: List<Person>,
    override val body: Html?,
    val status : String
):MessageItem(
    id,
    subject,
    timestamp,
    senders,
    body)