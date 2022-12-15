package com.otawilma.mobileclient

import android.content.Context
import com.otawilma.mobileclient.dataClasses.SchoolDay
import com.otawilma.mobileclient.storage.SchoolDayCache
import java.time.LocalDate

class DayRepository(private val context: Context): OtawilmaNetworking, SchoolDayCache{

    private val scheduleItems: HashMap<LocalDate, SchoolDay> = HashMap()

    // TODO There is a weakness that while the app runs and RAM is on, it wont accept any change to schedule
    suspend fun getScheduleOfADay(day : LocalDate) : SchoolDay {
        if (scheduleItems.containsKey(day)) return scheduleItems[day]!!

        // TODO local repository
        val stored = getStoredScheduleOfADay(context, day)
        if (stored!=null) return stored

        val dayList : List<SchoolDay> = getScheduleOfAWeek(day)
            ?: //TODO handle network error
            throw Exception("Network request returned nothing")

        dayList.forEach {
            scheduleItems[it.date] = it
            storeScheduleOfADay(context, it)
        }



        return scheduleItems[day]!!
    }

}