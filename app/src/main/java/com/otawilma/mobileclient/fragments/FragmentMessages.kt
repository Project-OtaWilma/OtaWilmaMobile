package com.otawilma.mobileclient.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.otawilma.mobileclient.OtawilmaNetworking
import com.otawilma.mobileclient.R
import com.otawilma.mobileclient.messaging.MessageAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FragmentMessages : Fragment(R.layout.fragment_messages), OtawilmaNetworking {

    private lateinit var messageAdapter : MessageAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        messageAdapter = MessageAdapter()

        CoroutineScope(Dispatchers.IO).launch {
            Log.d("Messaging","${getMessages(10)}")

            val messages = getMessages(100).second

            CoroutineScope(Dispatchers.Main).launch {
                messageAdapter.submitItems(messages)
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerViewMessages = view.findViewById<RecyclerView>(R.id.recyclerViewMessagesMessages)

        recyclerViewMessages.layoutManager = LinearLayoutManager(context)
        recyclerViewMessages.adapter = messageAdapter
        recyclerViewMessages.addItemDecoration(DividerItemDecoration(context,DividerItemDecoration.VERTICAL))
    }

}