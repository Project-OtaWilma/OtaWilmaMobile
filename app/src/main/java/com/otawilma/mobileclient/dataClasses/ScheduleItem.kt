package com.otawilma.mobileclient.dataClasses

import java.time.LocalDate
import java.time.LocalTime

abstract class ScheduleItem(
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
) : ScheduleItem(startTime, endTime, date)

data class JumpLesson(
    override val startTime: LocalTime,
    override val endTime: LocalTime,
    override val date: LocalDate,
): ScheduleItem(
    startTime,
    endTime,
    date
)