package com.otawilma.mobileclient.dataClasses

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = Student::class, name = "student"),
    JsonSubTypes.Type(value = Teacher::class, name = "teacher"),
    JsonSubTypes.Type(value = Personnel::class, name = "personnel")
)

abstract class Person {abstract val name : String}

data class Student(
    override val name: String,
    val group: String
): Person()

data class Teacher(
    override val name: String,
    val id: Int,
    val caption: String
): Person()

data class Personnel(
    override val name: String,
    val id: Int,
    val caption: String
): Person()