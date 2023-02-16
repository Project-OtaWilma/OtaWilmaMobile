package com.otawilma.mobileclient.storage

import android.content.Context
import com.otawilma.mobileclient.SCHOOLDAY_FILES_DIR_NAME
import com.otawilma.mobileclient.dataClasses.SchoolDay
import com.otawilma.mobileclient.jackSonMapper
import java.io.File
import java.io.FileNotFoundException
import java.time.LocalDate

interface SchoolDayCache {

    fun getStoredScheduleOfADay(context: Context, date: LocalDate) : SchoolDay?{

        return try {
            val dir = context.getDir(SCHOOLDAY_FILES_DIR_NAME, Context.MODE_PRIVATE)
            val file = File(dir, "$date")
            jackSonMapper.readValue(file, SchoolDay::class.java)
        }catch (e : FileNotFoundException){
            null
        }
    }

    fun storeScheduleOfADay(context: Context, schoolDay: SchoolDay){
        val preferenceStorage = PreferenceStorage(context)
        if (!preferenceStorage.enableCache) return
        val dir = context.getDir(SCHOOLDAY_FILES_DIR_NAME, Context.MODE_PRIVATE)
        val file = File(dir,"${schoolDay.date}")

        file.writeText(jackSonMapper.writeValueAsString(schoolDay))
    }

}