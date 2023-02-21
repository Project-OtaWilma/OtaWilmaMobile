package com.otawilma.mobileclient.storage

import android.content.Context
import com.otawilma.mobileclient.OtawilmaNetworking
import com.otawilma.mobileclient.dataClasses.SchoolDay
import com.otawilma.mobileclient.jackSonMapper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

class DayRepository(private val context: Context): OtawilmaNetworking, SchoolDayCache{

    private val scheduleMem : HashMap<LocalDate, SchoolDay> = hashMapOf()
    private val serverCache : HashMap<LocalDate, SchoolDay> = hashMapOf()
    private val sharedPreferences = PreferenceStorage(context)

    init {
        jackSonMapper.findAndRegisterModules()
    }

    val schoolDayFlowForHomePage = flow {

        var day = LocalDate.now()
        val listCache : MutableList<SchoolDay> = mutableListOf()
        while (listCache.size < sharedPreferences.homePageDays && day <= LocalDate.now().plusDays(20)) {
            val element = getCached(day)
            if (element != null && element.items.isNotEmpty()) {
                listCache.add(element.cached())
                emit(listCache)
            }
            day = day.plusDays(1)
        }

        day = LocalDate.now()

        val listActual : MutableList<SchoolDay> = mutableListOf()
        while (listActual.size < sharedPreferences.homePageDays && day <= LocalDate.now().plusDays(20)) {
            repeatUntilSuccess(context, waitUntilToken(context)) { token ->
                val element = getFromServer(token, day)
                if (element != null && element.items.isNotEmpty()) {
                    listActual.add(element.updated())
                }
                day = day.plusDays(1)
            }
        }
        serverCache.clear()
        emit(listActual)
    }

    fun schoolDayFlow(date : LocalDate) : Flow<SchoolDay> = flow {
        emit(getCached(date) ?: SchoolDay(date, listOf(), SchoolDay.NO_INFO))
        repeatUntilSuccess(context, waitUntilToken(context)){
            emit(getFromServer(it, date)?: SchoolDay(date))
        }
    }

    fun schoolDayServerCachedFlow(date : LocalDate) : Flow<SchoolDay> = flow {
        emit(getCached(date) ?: SchoolDay(date))
        do {
            val stored : SchoolDay? = serverCache[date]?.updated()
            if (stored != null) emit(stored)
            delay(100)
        } while (stored == null)
    }

    private fun getCached(day : LocalDate) : SchoolDay? {

            if (scheduleMem.containsKey(day)) return (scheduleMem[day]!!)

            val stored = getStoredScheduleOfADay(context, day)
            if (stored != null) {
                scheduleMem[day] = stored
                return (stored)
            }
        return null
    }

    private suspend fun getFromServer(token: String, day: LocalDate) : SchoolDay? {

        val stored : SchoolDay? = serverCache[day]?.updated()
        if (stored != null) return stored

        val week = getScheduleOfAWeek(token, day)

        if (week != null) {
            for (schoolDay in week){
                val date = schoolDay.date
                storeScheduleOfADay(context, schoolDay)
                scheduleMem[date] = schoolDay.updated()
                serverCache[date] = schoolDay.updated()
            }
            return scheduleMem[day]
        }
        return null
    }
}