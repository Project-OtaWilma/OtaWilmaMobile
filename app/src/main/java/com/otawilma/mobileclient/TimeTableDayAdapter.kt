package com.otawilma.mobileclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.otawilma.mobileclient.dataClasses.DayItem
import com.otawilma.mobileclient.dataClasses.SchoolDay
import com.otawilma.mobileclient.dataClasses.Separator
import java.time.format.TextStyle
import java.util.*


class TimeTableDayAdapter: RecyclerView.Adapter<TimeTableDayAdapter.TimeTableDayViewHolder>() {
    private var items: Array<DayItem> = arrayOf()

    // The view holder class
    class TimeTableDayViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        fun bindSchoolDay(position: Int, item : SchoolDay){
            val lessonRecyclerView : RecyclerView = itemView.findViewById<RecyclerView>(R.id.recyclerViewHomePageScheduleDay)
            val dateTextView: TextView = itemView.findViewById<TextView>(R.id.textViewHomePageShceduleDay)
            val adapterT = TimeTableAdapter()
            adapterT.submitItems(item.items)
            val date = item.date
            dateTextView.text = "${date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${date.dayOfMonth}/${date.month.value}"

            lessonRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                isNestedScrollingEnabled = false
                adapter = adapterT
            }
        }

    }

    fun getItemAtPosition (position: Int) = items[position]

    fun submitItems(list: List<SchoolDay>){


        val itemMutableList = mutableListOf<DayItem>()
        var last : SchoolDay? = null
        for (i in list){
            if (last != null && last.date != i.date.minusDays(1)){
                itemMutableList.add(Separator())
            }
            itemMutableList.add(i)
            last = i
        }
        items = itemMutableList.toTypedArray()
        notifyDataSetChanged()
    }

    fun submitChange(schoolDay: SchoolDay, position: Int){
        items[position] = schoolDay
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeTableDayViewHolder {
        val layout = when (viewType){
            TYPE_DAY -> R.layout.cardview_homepage_schedule_day
            TYPE_SEPARATOR -> R.layout.schedule_separator
            else -> R.layout.schedule_separator
        }
        val itemView = LayoutInflater.from(parent.context).inflate(
            layout,
            parent,
            false
        )
        return TimeTableDayViewHolder(itemView)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: TimeTableDayViewHolder, position: Int) {
        val currentItem = items[position]

        if (currentItem is SchoolDay) holder.bindSchoolDay(position, currentItem)

    }

    override fun getItemViewType(position: Int): Int {
        return when(items[position]){
            is SchoolDay -> TYPE_DAY
            is Separator -> TYPE_SEPARATOR
            else -> TYPE_SEPARATOR
        }
    }

    companion object {
        private const val TYPE_DAY = 0
        private const val TYPE_SEPARATOR = 1
        var adapterList: ArrayList<TimeTableAdapter> = ArrayList()
    }
}