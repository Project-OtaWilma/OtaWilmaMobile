package com.otawilma.mobileclient.dataClasses

import java.time.LocalDate

open class DayItem

data class SchoolDay(val date : LocalDate, val items: List<ScheduleItem>) : DayItem()

class Separator : DayItem()
