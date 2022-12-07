package com.otawilma.mobileclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.otawilma.mobileclient.dataClasses.Lesson
import java.time.format.TextStyle
import java.util.*


class TimeTableDayAdapter: RecyclerView.Adapter<TimeTableDayAdapter.TimeTableDayViewHolder>() {
    private  var items: List<List<Lesson>> = emptyList()
    private  var adapterList: ArrayList<TimeTableAdapter> = ArrayList()

    // The view holder class
    class TimeTableDayViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val lessonRecyclerView : RecyclerView = itemView.findViewById<RecyclerView>(R.id.recyclerViewHomePageScheduleDay)
        val dateTextView: TextView = itemView.findViewById<TextView>(R.id.textViewHomePageShceduleDay)
    }

    fun submitItems(list: List<List<Lesson>>){
        items = list.filter{ it.isNotEmpty() }
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
        adapterList[position].submitItems(currentItem)
        val date = currentItem[0].date
        holder.dateTextView.text = "${date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${date.dayOfMonth}/${date.month.value}"

        holder.lessonRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = adapterList[position]
        }
    }

}