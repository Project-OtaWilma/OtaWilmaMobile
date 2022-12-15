package com.otawilma.mobileclient

import android.content.Context
import androidx.room.Room
import com.otawilma.mobileclient.dataClasses.SchoolDay
import com.otawilma.mobileclient.persistantStorage.SchoolDayDataBase
import java.time.LocalDate

class DayRepository(context : Context): OtawilmaNetworking {

    private val scheduleItems: HashMap<LocalDate, SchoolDay> = HashMap()
    private val dataBase = Room.databaseBuilder(context,SchoolDayDataBase::class.java, "schoolday").build()
    private val schoolDayDao = dataBase.schoolDayDao()

    // TODO There is a weakness that while the app runs and RAM is on, it wont accept any change to schedule
    suspend fun getScheduleOfADay(day : LocalDate) : SchoolDay {
        if (scheduleItems.containsKey(day)) return scheduleItems[day]!!

        // TODO local repository
        return schoolDayDao.getDay(day)

        val dayList : List<SchoolDay> = getScheduleOfAWeek(day)
            ?: //TODO handle network error
            throw Exception("Network request returned nothing")

        dayList.forEach {
            scheduleItems[it.date] = it
        }

        return scheduleItems[day]!!
    }

}