package com.otawilma.mobileclient.fragments

import android.os.Bundle
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
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
import kotlin.math.max
import kotlin.math.min

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
        val buttonCalender = view.findViewById<ImageButton>(R.id.buttonSearchSchedule)
        val buttonToday = view.findViewById<Button>(R.id.buttonScheduleToday)

        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerViewSchedule.apply {
            layoutManager = linearLayoutManager
            adapter = timeTableDayAdapter
        }

        // TODO add features
        // TODO make this work with no internet in a more CPU efficient way

        val handled : Array<Int> = Array(timeTableDayAdapter.itemCount){0}
        val layoutManager = (recyclerViewSchedule.layoutManager as LinearLayoutManager)

        recyclerViewSchedule.setOnScrollChangeListener { _, _, _, _, _ ->
            for (i in max(layoutManager.findFirstVisibleItemPosition() - 20,0)..min(layoutManager.findLastVisibleItemPosition() + 20, layoutManager.itemCount - 1)) {
                CoroutineScope(Dispatchers.IO).launch {

                    // If a request for the week in question has not been made yet
                    val item = timeTableDayAdapter[i] as SchoolDay
                    val state = item.state
                    if (state != SchoolDay.UPDATED && handled[i] == NOT_HANDLED) {
                        setHandledWeek(item.date, i, handled, REQUEST_MADE)
                        schoolDayRepository.schoolDayFlow(item.date).collect {
                            handled[i] = COLLECTOR_ATTACHED
                            CoroutineScope(Dispatchers.Main).launch {
                                timeTableDayAdapter.submitChange(it, i)
                            }
                        }
                    } else if (state != SchoolDay.UPDATED && handled[i] == REQUEST_MADE) { // If the request has been made so now we just have to await the response
                        handled[i] = COLLECTOR_ATTACHED
                        schoolDayRepository.schoolDayServerCachedFlow(item.date).collect {

                            CoroutineScope(Dispatchers.Main).launch {
                                timeTableDayAdapter.submitChange(it, i)
                            }
                        }
                    }
                    setHandledWeek(item.date, i, handled, REQUEST_MADE)
                }
            }
            var max = 0
            for (i in 0 until recyclerViewSchedule.childCount){
                val child = recyclerViewSchedule[i]
                // This took only around a fucking hour to find out
                child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
                max = max(child.measuredHeight, max)
            }
            recyclerViewSchedule.minimumHeight = max
        }
        // Set the starting position correctly
        layoutManager.scrollToPositionWithOffset(365,0)

        buttonToday.setOnClickListener {
            layoutManager.scrollToPositionWithOffset(365,0)
        }

        buttonCalender.setOnClickListener {
            val popUpView = layoutInflater.inflate(R.layout.popup_calender_seek, null)
            val popupWindow = PopupWindow(
                popUpView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val calender = popUpView.findViewById<CalendarView>(R.id.calendarViewPopupSeek)
            calender.date = (timeTableDayAdapter[layoutManager.findFirstVisibleItemPosition()] as SchoolDay).date.toEpochDay() * 86400000
            calender.setOnDateChangeListener { _, year, month, dayOfMonth ->
                val pos = timeTableDayAdapter.getPositionOfADate(LocalDate.of(year, month + 1, dayOfMonth))
                if (pos == -1) {
                    Toast.makeText(
                        context,
                        "Schedule for the selected date does not exist",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnDateChangeListener
                }
                layoutManager.scrollToPositionWithOffset(pos,0)
                popupWindow.dismiss()
            }

            val buttonCancel = popUpView.findViewById<Button>(R.id.buttonDismissCalender)
            buttonCancel.setOnClickListener { popupWindow.dismiss() }
            popupWindow.showAsDropDown(buttonCalender,0 , -buttonCalender.measuredHeight)
        }
    }

    private fun setHandledWeek(date: LocalDate, index : Int, handled : Array<Int>, state : Int){
        val offset = date.dayOfWeek.value
        for (i in 1..7){
            try {
                handled[index+i-offset] = max(state, handled[index+i-offset])
            } catch (_: IndexOutOfBoundsException){}
        }
    }

    companion object{
        const val NOT_HANDLED = 0
        const val REQUEST_MADE = 1
        const val COLLECTOR_ATTACHED = 2
    }
}