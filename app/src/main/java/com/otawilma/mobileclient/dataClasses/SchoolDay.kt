package com.otawilma.mobileclient.dataClasses

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.LocalDate

open class DayItem

data class SchoolDay(
    val date: LocalDate,
    val items: List<ScheduleItem>,
    @JsonIgnore
    val state: Int
) : DayItem(){


    constructor (date: LocalDate, items: List<ScheduleItem>) : this(date, items, NO_INFO)
    constructor (date: LocalDate) : this(date, listOf(), NO_INFO)

    fun cached() : SchoolDay {
        return SchoolDay(date, items, CACHED)
    }

    fun updated() : SchoolDay{
        return SchoolDay(date, items, UPDATED)
    }


    companion object{
        const val  NO_INFO = 0
        const val CACHED = 1
        const val UPDATED = 2
        const val UPDATE_ON_THE_WAY = 3
    }
}

class Separator : DayItem()
