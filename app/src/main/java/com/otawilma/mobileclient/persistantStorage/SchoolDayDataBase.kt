package com.otawilma.mobileclient.persistantStorage

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.otawilma.mobileclient.dataClasses.SchoolDay

@Database(entities = [SchoolDay::class], version = 1)
@TypeConverters(Converters::class)
abstract class SchoolDayDataBase : RoomDatabase() {
    abstract fun schoolDayDao() : SchoolDayDao
}