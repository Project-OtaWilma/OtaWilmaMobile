package com.otawilma.mobileclient.dataClasses

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class SchoolDay(
    @PrimaryKey
    val date: LocalDate,
    val items: List<ScheduleItem>
)
