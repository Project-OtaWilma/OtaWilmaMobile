package com.otawilma.mobileclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.otawilma.mobileclient.dataClasses.Lesson


class TimeTableAdapter: RecyclerView.Adapter<TimeTableAdapter.TimeTableViewHolder>() {
    private var items = emptyList<Lesson>()

    // The view holder class
    class TimeTableViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val lessonCode: TextView = itemView.findViewById(R.id.textViewLessonCode)
        val lessonTime : TextView = itemView.findViewById(R.id.textViewLessonTime)
        val lessonClass : TextView = itemView.findViewById(R.id.textViewLessonClass)
    }

    fun submitItems(list: List<Lesson>){
        items = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeTableViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.layout_recyclerview_schedule_home,
            parent,
            false
        )
        return TimeTableViewHolder(itemView)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: TimeTableViewHolder, position: Int) {
        val currentItem = items[position]

        holder.lessonCode.text = currentItem.code
        holder.lessonTime.text = "${currentItem.startTime} - ${currentItem.endTime}"
        holder.lessonClass.text = "placeholder"
    }

}