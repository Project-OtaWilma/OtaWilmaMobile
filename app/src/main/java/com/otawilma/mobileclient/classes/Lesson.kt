package com.otawilma.mobileclient.classes

import java.time.LocalDate
import java.time.LocalTime

data class Lesson(
    val code: String,
    val name: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val classRoom: List<ClassRoom>,
    val teacher: List<Teacher>,
    val date: LocalDate
)
