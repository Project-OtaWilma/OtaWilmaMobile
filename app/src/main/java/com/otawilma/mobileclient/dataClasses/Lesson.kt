package com.otawilma.mobileclient.dataClasses

import java.time.LocalDate
import java.time.LocalTime

abstract class Lesson(
    open val startTime: LocalTime,
    open val endTime: LocalTime,
    open val date: LocalDate
)

data class NormalLesson(
    override val startTime: LocalTime,
    override val endTime: LocalTime,
    override val date: LocalDate,
    val code: String,
    val name: String,
    val classRoom: List<ClassRoom>,
    val teacher: List<Teacher>
) : Lesson(startTime, endTime, date)
