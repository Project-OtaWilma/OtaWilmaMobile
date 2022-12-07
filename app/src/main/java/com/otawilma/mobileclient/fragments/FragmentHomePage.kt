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
import com.otawilma.mobileclient.TimeTableAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class FragmentHomePage:Fragment(R.layout.fragment_home_page), OtawilmaNetworking {

    private lateinit var timeTableAdapter: TimeTableAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timeTableAdapter = TimeTableAdapter()


        CoroutineScope(Dispatchers.IO).launch {
            val scheduleResult = getScheduleOfAWeek(LocalDate.now())
            CoroutineScope(Dispatchers.Main).launch {
                if (scheduleResult.first) {
                    val lessonList = scheduleResult.second
                    Log.d("FragmentHomePage", "LessonList is: $lessonList")
                    timeTableAdapter.submitItems(lessonList)
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
        val layoutManagerBetter = LinearLayoutManager(context)
        view.findViewById<RecyclerView>(R.id.recyclerViewHomeSchedule)?.apply {

            // THIS IS BULLSHIT FUCK YOU DROID
            layoutManager = layoutManagerBetter
            isNestedScrollingEnabled = false
            adapter = timeTableAdapter

        }

    }
}
