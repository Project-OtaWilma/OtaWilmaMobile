package com.otawilma.mobileclient.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.otawilma.mobileclient.OtawilmaNetworking
import com.otawilma.mobileclient.R
import com.otawilma.mobileclient.TimeTableDayAdapter
import com.otawilma.mobileclient.dataClasses.Lesson
import com.otawilma.mobileclient.sharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class FragmentHomePage:Fragment(R.layout.fragment_home_page), OtawilmaNetworking {

    private lateinit var timeTableDayAdapter: TimeTableDayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timeTableDayAdapter = TimeTableDayAdapter()


        CoroutineScope(Dispatchers.IO).launch {
            val scheduleResult = getScheduleOfAWeek(LocalDate.now())
            CoroutineScope(Dispatchers.Main).launch {
                if (scheduleResult.first) {
                    val lessonList = scheduleResult.second
                    Log.d("FragmentHomePage", "LessonList is: $lessonList")

                    // TODO Write a class for a day and fix this awful shit

                    val today = LocalDate.now()
                    val lessonListByDay = ArrayList<List<Lesson>>()

                    for (i in 0 until sharedPreferences.homePageDays){
                        val dayToTake = today.plusDays(i.toLong())
                        val lessonOfTheDay = lessonList.filter {
                            it.date == dayToTake
                        }
                        lessonListByDay.add(lessonOfTheDay)
                    }
                    timeTableDayAdapter.submitItems(lessonListByDay)

                } else {
                    Toast.makeText(
                        activity?.applicationContext,
                        "Failed to load schedule for some reason",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManagerBetter = DayRecyclerLinearLayoutManager(context)
        layoutManagerBetter.orientation = LinearLayoutManager.HORIZONTAL
        view.findViewById<RecyclerView>(R.id.recyclerViewHomeSchedule)?.apply {

            // THIS IS BULLSHIT FUCK YOU DROID
            layoutManager = layoutManagerBetter
            isNestedScrollingEnabled = false
            adapter = timeTableDayAdapter

        }

    }

    class DayRecyclerLinearLayoutManager(context: Context?) : LinearLayoutManager(context){
        override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {

            if (lp != null) {
                //lp.width = width/sharedPreferences.homePageDays
            }

            return super.checkLayoutParams(lp)
        }
    }
}
