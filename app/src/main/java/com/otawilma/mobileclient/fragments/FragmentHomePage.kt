package com.otawilma.mobileclient.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.otawilma.mobileclient.OtawilmaNetworking
import com.otawilma.mobileclient.R
import com.otawilma.mobileclient.TimeTableDayAdapter
import com.otawilma.mobileclient.dayRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FragmentHomePage:Fragment(R.layout.fragment_home_page), OtawilmaNetworking {

    private lateinit var timeTableDayAdapter: TimeTableDayAdapter

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val coroutineScopeMain = CoroutineScope(Dispatchers.Main )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timeTableDayAdapter = TimeTableDayAdapter()



        coroutineScope.launch {
            val schoolDayFlow = dayRepository.schoolDayFlow
            schoolDayFlow.collect {
                coroutineScopeMain.launch {
                    timeTableDayAdapter.submitItems(it)
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
            /* Useless for now
            if (lp != null) {
                lp.width = width/sharedPreferences.homePageDays
            }
             */
            return super.checkLayoutParams(lp)
        }
    }

    override fun onDestroy() {
        coroutineScope.cancel()
        coroutineScopeMain.cancel()
        super.onDestroy()
    }
}
