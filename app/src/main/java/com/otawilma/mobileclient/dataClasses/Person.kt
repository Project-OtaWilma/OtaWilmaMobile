package com.otawilma.mobileclient.dataClasses

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