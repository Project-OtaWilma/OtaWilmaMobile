package com.otawilma.mobileclient.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.otawilma.mobileclient.OtawilmaNetworking
import com.otawilma.mobileclient.R
import com.otawilma.mobileclient.TimeTableDayAdapter
import com.otawilma.mobileclient.dataClasses.SchoolDay
import com.otawilma.mobileclient.storage.DayRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class FragmentSchedule : Fragment(R.layout.fragment_schedule), OtawilmaNetworking {

    private val timeTableDayAdapter = TimeTableDayAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.title = getString(R.string.app_name) + " - Schedule"
        val schoolDayMutableList : MutableList<SchoolDay> = mutableListOf()
        for (i in -365..365){
            val day = LocalDate.now().plusDays(i.toLong())
            schoolDayMutableList.add( SchoolDay(
                day,
                listOf(),
                SchoolDay.NO_INFO
            )
            )
        }
        timeTableDayAdapter.submitItems(schoolDayMutableList.toList())

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val schoolDayRepository = DayRepository(requireContext())

        val recyclerViewSchedule = view.findViewById<RecyclerView>(R.id.recyclerViewFragmentSchedule)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerViewSchedule.apply {
            layoutManager = linearLayoutManager
            adapter = timeTableDayAdapter
        }

        // TODO fix height issue with "Hikkepäivät"
        // TODO add features

        val handled : BooleanArray = BooleanArray(timeTableDayAdapter.itemCount){
            false
        }

        val layoutManager = (recyclerViewSchedule.layoutManager as LinearLayoutManager)

        recyclerViewSchedule.setOnScrollChangeListener { _, _, _, _, _ ->

                for (i in layoutManager.findFirstVisibleItemPosition() - 20..layoutManager.findLastVisibleItemPosition() + 20) {

                    CoroutineScope(Dispatchers.IO).launch {
                    val item = timeTableDayAdapter.getItemAtPosition(i) as SchoolDay
                    val state = item.state
                    if (state != SchoolDay.UPDATED && !handled[i]) {
                        setHandledWeek(item.date, i, handled)
                        schoolDayRepository.schoolDayFlow(item.date).collect {
                            CoroutineScope(Dispatchers.Main).launch {
                                timeTableDayAdapter.submitChange(it, i)
                            }
                        }
                    } else if (state != SchoolDay.UPDATED) {
                        schoolDayRepository.schoolDayCachedFlow(item.date).collect {
                            CoroutineScope(Dispatchers.Main).launch {
                                timeTableDayAdapter.submitChange(it, i)
                            }
                        }
                    }
                        setHandledWeek(item.date, i, handled)
                }
            }
        }
        layoutManager.scrollToPosition(365)
    }

    private fun setHandledWeek(date: LocalDate, index : Int, handled : BooleanArray){
        val offset = date.dayOfWeek.value
        for (i in 1..7){
            handled[index+i-offset] = true
        }
    }
}