package com.otawilma.mobileclient.dataClasses

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.LocalTime


@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes(
    Type(value = NormalLesson::class, name = "normalLesson"),
    Type(value = JumpLesson::class, name = "jumpLesson")
)


abstract class ScheduleItem {
    @Transient
    open val startTime: LocalTime? = null
    @Transient
    open val endTime: LocalTime? = null
}

data class NormalLesson(
    override val startTime: LocalTime,
    override val endTime: LocalTime,
    val code: String,
    val name: String,
    val classRoom: List<ClassRoom>,
    val teacher: List<Teacher>
) : ScheduleItem()

data class JumpLesson(
    override val startTime: LocalTime,
    override val endTime: LocalTime
): ScheduleItem()