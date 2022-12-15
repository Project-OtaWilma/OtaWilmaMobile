package com.otawilma.mobileclient.storage

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.otawilma.mobileclient.SchoolDay_FILES_DIR_NAME
import com.otawilma.mobileclient.dataClasses.SchoolDay
import com.otawilma.mobileclient.jackSonMapper
import java.io.File
import java.io.FileNotFoundException
import java.time.LocalDate

interface SchoolDayCache {

    fun getStoredScheduleOfADay(context: Context, date: LocalDate) : SchoolDay?{

        return try {
            val dir = context.getDir(SchoolDay_FILES_DIR_NAME, Context.MODE_PRIVATE)
            val file = File(dir, "$date")
            jackSonMapper.findAndRegisterModules()
            jackSonMapper.readValue(file.readText(), SchoolDay::class.java)
        }catch (e : FileNotFoundException){
            null
        }
    }

    fun storeScheduleOfADay(context: Context, schoolDay: SchoolDay){
        val dir = context.getDir(SchoolDay_FILES_DIR_NAME, Context.MODE_PRIVATE)
        val file = File(dir,"${schoolDay.date}")

        file.writeText(jackSonMapper.writeValueAsString(schoolDay))
        Log.d("Storage", "Json of the object is ${Gson().toJson(schoolDay)}")
        Log.d("Storage", "Content of file is: ${file.readText()}")
    }

}