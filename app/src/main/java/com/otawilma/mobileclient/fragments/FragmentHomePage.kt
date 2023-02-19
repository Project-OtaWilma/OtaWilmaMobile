package com.otawilma.mobileclient.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.otawilma.mobileclient.OtawilmaNetworking
import com.otawilma.mobileclient.R
import com.otawilma.mobileclient.TimeTableDayAdapter
import com.otawilma.mobileclient.dataClasses.MessageItem
import com.otawilma.mobileclient.messaging.MessageAdapter
import com.otawilma.mobileclient.messaging.MessageClickListener
import com.otawilma.mobileclient.storage.DayRepository
import com.otawilma.mobileclient.storage.MessageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class FragmentHomePage:Fragment(R.layout.fragment_home_page), OtawilmaNetworking, MessageClickListener {


    private val timeTableDayAdapter: TimeTableDayAdapter = TimeTableDayAdapter()
    private val messageAdapter : MessageAdapter = MessageAdapter(this@FragmentHomePage)

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val coroutineScopeMain = CoroutineScope(Dispatchers.Main )

    override fun onCreate(savedInstanceState: Bundle?) {
        val dayRepository = DayRepository(requireContext())
        val messageRepository = MessageRepository(requireContext())
        super.onCreate(savedInstanceState)
        activity?.title = getString(R.string.app_name) + " - Home"

        coroutineScope.launch {
            val schoolDayFlow = dayRepository.schoolDayFlowForHomePage
            schoolDayFlow.collect {
                coroutineScopeMain.launch {
                    timeTableDayAdapter.submitItems(it)
                }
            }
        }
        coroutineScope.launch {
            messageRepository.newMessageFlow.collect{
                coroutineScopeMain.launch {
                    messageAdapter.submitItems(it)
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
            isNestedScrollingEnabled = true
            adapter = timeTableDayAdapter
        }

        view.findViewById<RecyclerView>(R.id.recyclerViewHomeMessages)?.apply {
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = messageAdapter
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

    override fun onClick(messageItem: MessageItem) {
        Toast.makeText(context, messageItem.subject,Toast.LENGTH_SHORT).show()
    }
}
