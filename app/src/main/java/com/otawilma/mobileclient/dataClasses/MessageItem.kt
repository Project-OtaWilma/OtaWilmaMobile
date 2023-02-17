package com.otawilma.mobileclient.dataClasses

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.LocalDateTime


abstract class MessageItem {
    @Transient
    open val id: Int? = null

    @Transient
    open val subject: String? = null

    @Transient
    open val timestamp: LocalDateTime? = null

    @Transient
    open val senders: List<Person>? = null

    @Transient
    open var body: String? = null
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes(
    Type(value = Message::class, name = "message"),
    Type(value = Appointment::class, name = "appointment")
)

data class Message(
    override val id: Int,
    override val subject: String,
    override val timestamp: LocalDateTime,
    override val senders: List<Person>,
    override var body: String?,
    val recipients : List<Person>?,
    val new : Boolean
):MessageItem()

data class Appointment(
    override val id: Int,
    override val subject: String,
    override val timestamp: LocalDateTime,
    override val senders: List<Person>,
    override var body: String?,
    val status : String
):MessageItem()