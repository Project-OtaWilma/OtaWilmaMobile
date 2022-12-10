package com.otawilma.mobileclient.dataClasses

abstract class Person (open val name : String)

data class Student(
    override val name: String,
    val group: String
): Person(name)

data class  Teacher(
    override val name: String,
    val id: Int,
    val caption: String
): Person(name)

data class Personnel(
    override val name: String,
    val id: Int,
    val caption: String
): Person(name)