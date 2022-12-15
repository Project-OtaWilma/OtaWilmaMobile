package com.otawilma.mobileclient.persistantStorage

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.otawilma.mobileclient.dataClasses.SchoolDay
import java.time.LocalDate

@Dao
interface SchoolDayDao {
    @Query("SELECT * FROM schoolday")
    fun getAll() : HashMap<LocalDate, SchoolDay>

    @Query("SELECT * FROM schoolday WHERE date IN (:dateToGet)")
    fun getDay(dateToGet : LocalDate) : SchoolDay

    @Insert
    fun insertList(vararg schooDays : HashMap<LocalDate,SchoolDay>)

    @Update
    fun updateDate(schoolDay: SchoolDay)

    @Delete
    fun deleteOne(schoolDay: SchoolDay)

    @Query("DELETE FROM schoolday")
    fun deleteAll()
}