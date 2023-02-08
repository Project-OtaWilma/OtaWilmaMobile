package com.otawilma.mobileclient.fragments

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.otawilma.mobileclient.InvalidTokenNetworkException
import com.otawilma.mobileclient.OtawilmaNetworking
import com.otawilma.mobileclient.R
import com.otawilma.mobileclient.dataClasses.Message
import com.otawilma.mobileclient.dataClasses.MessageItem
import com.otawilma.mobileclient.messaging.MessageAdapter
import com.otawilma.mobileclient.messaging.MessageClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException


class FragmentMessages : Fragment(R.layout.fragment_messages), OtawilmaNetworking, MessageClickListener {

    private lateinit var messageAdapter : MessageAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        messageAdapter = MessageAdapter(this@FragmentMessages)

        CoroutineScope(Dispatchers.IO).launch {

            var token = waitUntilToken(context!!)
            while (true) {
                try {
                    val messages = getMessages(token,500)
                    CoroutineScope(Dispatchers.Main).launch {
                        messageAdapter.submitItems(messages)
                    }
                    break
                } catch (e: InvalidTokenNetworkException) {
                    token = invalidateTokenAndGetNew(context!!)
                }catch (e: SocketTimeoutException){
                    delay(100)
                }
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

    override fun onClick(messageItem: MessageItem) {
        Log.d("Messaging", "Clicked $messageItem")
        CoroutineScope(Dispatchers.IO).launch {

            val token = getToken()

            val messageToDisplay = getMessageBody(token, messageItem as Message)

            if (messageToDisplay != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    Log.d("Messaging", "Body of message is: $messageToDisplay")

                    val popUpView = layoutInflater.inflate(R.layout.popup_message, null)
                    val popupWindow = PopupWindow(
                        popUpView,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )

                    val textTitle = popUpView.findViewById<TextView>(R.id.textViewPopupMessageTitle)
                    val textContent =
                        popUpView.findViewById<TextView>(R.id.textViewPopupMessageContent)
                    val buttonDismiss = popUpView.findViewById<ImageButton>(R.id.buttonDismissMessage)

                    textTitle.text = messageToDisplay.subject
                    textContent.text = messageToDisplay.body

                    popupWindow.showAtLocation(activity?.findViewById(R.id.navHostFragmentMain),Gravity.TOP,0,0)


                    buttonDismiss.setOnClickListener { popupWindow.dismiss() }

                }
            }
        }
    }

}