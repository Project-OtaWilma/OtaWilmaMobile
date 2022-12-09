package com.otawilma.mobileclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.otawilma.mobileclient.dataClasses.ScheduleItem
import com.otawilma.mobileclient.dataClasses.SchoolDay
import java.time.format.TextStyle
import java.util.*


class TimeTableDayAdapter: RecyclerView.Adapter<TimeTableDayAdapter.TimeTableDayViewHolder>() {
    private  var items: List<SchoolDay> = emptyList()
    private  var adapterList: ArrayList<TimeTableAdapter> = ArrayList()

    // The view holder class
    class TimeTableDayViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val lessonRecyclerView : RecyclerView = itemView.findViewById<RecyclerView>(R.id.recyclerViewHomePageScheduleDay)
        val dateTextView: TextView = itemView.findViewById<TextView>(R.id.textViewHomePageShceduleDay)
    }

    fun submitItems(list: List<SchoolDay>){
        items = list.filter {
            it.items != emptyList<ScheduleItem>()
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeTableDayViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.cardview_homepage_schedule_day,
            parent,
            false
        )
        return TimeTableDayViewHolder(itemView)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: TimeTableDayViewHolder, position: Int) {
        val currentItem = items[position]


        adapterList.add(TimeTableAdapter())
        adapterList[position].submitItems(currentItem.items)
        val date = currentItem.date
        holder.dateTextView.text = "${date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${date.dayOfMonth}/${date.month.value}"

        holder.lessonRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = adapterList[position]
        }
    }

}