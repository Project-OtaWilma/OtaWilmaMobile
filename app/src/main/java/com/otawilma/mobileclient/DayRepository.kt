package com.otawilma.mobileclient

import android.content.Context
import com.otawilma.mobileclient.dataClasses.SchoolDay
import com.otawilma.mobileclient.storage.SchoolDayCache
import java.time.Duration
import java.time.LocalDate

class DayRepository(private val context: Context): OtawilmaNetworking, SchoolDayCache{


    private val scheduleMem : HashMap<LocalDate, SchoolDay> = hashMapOf()

    private val updateInterval : Duration = Duration.ofSeconds(10)


    fun getCached(day : LocalDate) : SchoolDay? {

            if (scheduleMem.containsKey(day)) return (scheduleMem[day]!!)

            val stored = getStoredScheduleOfADay(context, day)
            if (stored != null) {
                scheduleMem[day] = stored
                return (stored)
            }
        return null
    }

    suspend fun getFromServer(token: String, day: LocalDate) : SchoolDay? {
        val week = getScheduleOfAWeek(token, day)

        if (week != null) {
            for (schoolDay in week){
                val date = schoolDay.date
                storeScheduleOfADay(context, schoolDay)
                scheduleMem[date] = schoolDay
            }
            return scheduleMem[day]
        }
        return null
    }
}