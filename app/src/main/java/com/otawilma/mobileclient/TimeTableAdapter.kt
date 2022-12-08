package com.otawilma.mobileclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.otawilma.mobileclient.dataClasses.JumpLesson
import com.otawilma.mobileclient.dataClasses.NormalLesson
import com.otawilma.mobileclient.dataClasses.ScheduleItem


class TimeTableAdapter: RecyclerView.Adapter<TimeTableAdapter.TimeTableViewHolder>() {
    private var items = emptyList<ScheduleItem>()

    // The view holder class
    class TimeTableViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        private fun bindNormalLesson(normalLesson: NormalLesson){
            val lessonCode: TextView = itemView.findViewById(R.id.textViewLessonCode)
            val lessonTime : TextView = itemView.findViewById(R.id.textViewLessonTime)
            val lessonClass : TextView = itemView.findViewById(R.id.textViewLessonClass)

            lessonCode.text = normalLesson.code
            lessonTime.text = "${normalLesson.startTime} - ${normalLesson.endTime}"
            lessonClass.text = normalLesson.classRoom.map { it.caption }.joinToString(separator = " / ") {
                it
            }
        }

        private fun bindJumpLesson(jumpLesson: JumpLesson){
            // no more here
            val lengthText: TextView = itemView.findViewById(R.id.textViewJumpLessonTime)

            lengthText.text = "${jumpLesson.startTime} - ${jumpLesson.endTime}"

        }

        fun bind (scheduleItem: ScheduleItem){
            when (scheduleItem){
                is NormalLesson -> bindNormalLesson(scheduleItem as NormalLesson)
                is JumpLesson -> bindJumpLesson(scheduleItem as JumpLesson)
            }
        }
    }


    fun submitItems(list: List<ScheduleItem>){
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

        val currentItem = items[position]
        holder.bind(currentItem)

    }

    override fun getItemViewType(position: Int): Int {
        return when(items[position]){
            is NormalLesson -> TYPE_LESSON
            is JumpLesson -> TYPE_JUMP
            else -> TYPE_JUMP
        }
    }

    companion object {
        private const val TYPE_LESSON = 0
        private const val TYPE_JUMP = 1
    }

}