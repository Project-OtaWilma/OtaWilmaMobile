package com.otawilma.mobileclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.otawilma.mobileclient.dataClasses.Lesson
import com.otawilma.mobileclient.dataClasses.NormalLesson


class TimeTableAdapter: RecyclerView.Adapter<TimeTableAdapter.TimeTableViewHolder>() {
    private var items = emptyList<Lesson>()

    // The view holder class
    open class TimeTableViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val lessonCode: TextView = itemView.findViewById(R.id.textViewLessonCode)
        val lessonTime : TextView = itemView.findViewById(R.id.textViewLessonTime)
        val lessonClass : TextView = itemView.findViewById(R.id.textViewLessonClass)
    }


    fun submitItems(list: List<Lesson>){
        items = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeTableViewHolder {
        val layout = when (viewType){
            TYPE_LESSON -> R.layout.layout_recyclerview_schedule_home
            TYPE_JUMP -> R.layout.item_jump_lesson
            else -> {R.layout.layout_recyclerview_schedule_home}
        }

        val itemView = LayoutInflater.from(parent.context).inflate(
            layout,
            parent,
            false
        )
        return TimeTableViewHolder(itemView)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: TimeTableViewHolder, position: Int) {

        var currentItem = items[position]
        if (currentItem::class.java == NormalLesson::class.java){
            currentItem = currentItem as NormalLesson
            holder.lessonCode.text = currentItem.code
            holder.lessonTime.text = "${currentItem.startTime} - ${currentItem.endTime}"
            holder.lessonClass.text = currentItem.classRoom.map { it.caption }.joinToString(separator = " / ") {
                it
            }
        }



    }

    companion object{
        private const val TYPE_LESSON = 0
        private const val TYPE_JUMP = 1
    }

}