package com.otawilma.mobileclient.dataClasses

import java.time.LocalTime

abstract class ScheduleItem(
    open val startTime: LocalTime,
    open val endTime: LocalTime
)

data class NormalLesson(
    override val startTime: LocalTime,
    override val endTime: LocalTime,
    val code: String,
    val name: String,
    val classRoom: List<ClassRoom>,
    val teacher: List<Teacher>
) : ScheduleItem(startTime, endTime)

data class JumpLesson(
    override val startTime: LocalTime,
    override val endTime: LocalTime
): ScheduleItem(
    startTime,
    endTime
)