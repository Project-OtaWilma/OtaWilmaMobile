package com.otawilma.mobileclient.fragments

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.otawilma.mobileclient.OtawilmaNetworking
import com.otawilma.mobileclient.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class FragmentHomePage:Fragment(R.layout.fragment_home_page), OtawilmaNetworking {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            val scheduleResult = getScheduleOfAWeek(LocalDate.now())
            CoroutineScope(Dispatchers.Main).launch {
                if (scheduleResult.first) {
                    val lessonList = scheduleResult.second
                    Log.d("FragmentHomePage", "LessonList is: $lessonList")
                } else {
                    Toast.makeText(activity?.applicationContext, "Failed to load schedule for some reason", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}