package com.otawilma.mobileclient

import com.otawilma.mobileclient.dataClasses.SchoolDay
import java.time.LocalDate

class DayRepository: OtawilmaNetworking {

    private val scheduleItems: HashMap<LocalDate, SchoolDay> = HashMap()

    suspend fun getScheduleOfADay(day : LocalDate) : SchoolDay {
        if (scheduleItems.containsKey(day)) return scheduleItems[day]!!

        // TODO local repository

        val dayList : List<SchoolDay> = getScheduleOfAWeek(day)
            ?: //TODO handle network error
            throw Exception("Network request returned nothing")

        dayList.forEach {
            scheduleItems[it.date] = it
        }

        return scheduleItems[day]!!
    }

}