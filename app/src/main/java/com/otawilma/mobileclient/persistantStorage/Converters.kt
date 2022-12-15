package com.otawilma.mobileclient.persistantStorage

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {

    @TypeConverter
    fun fromDate(date: LocalDate) : Long{
        return date.toEpochDay()
    }

    @TypeConverter
    fun toDate(long: Long) : LocalDate{
        return LocalDate.ofEpochDay(long)
    }
}