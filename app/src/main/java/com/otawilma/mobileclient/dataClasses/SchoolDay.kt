package com.otawilma.mobileclient.dataClasses

import java.time.LocalDate

data class SchoolDay(val date : LocalDate, val items: List<ScheduleItem>)
