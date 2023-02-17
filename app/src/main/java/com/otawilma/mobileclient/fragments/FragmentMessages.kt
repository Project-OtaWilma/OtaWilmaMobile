package com.otawilma.mobileclient.fragments

import android.os.Bundle
import android.text.Html
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
import com.otawilma.mobileclient.OtawilmaNetworking
import com.otawilma.mobileclient.R
import com.otawilma.mobileclient.dataClasses.Message
import com.otawilma.mobileclient.dataClasses.MessageItem
import com.otawilma.mobileclient.messaging.MessageAdapter
import com.otawilma.mobileclient.messaging.MessageClickListener
import com.otawilma.mobileclient.storage.MessageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FragmentMessages : Fragment(R.layout.fragment_messages), OtawilmaNetworking, MessageClickListener {

    private val messageAdapter : MessageAdapter = MessageAdapter(this@FragmentMessages)

    override fun onCreate(savedInstanceState: Bundle?) {
        val messageRepository = MessageRepository(requireContext())
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            val messageFlow = messageRepository.messageFlow(100)

            messageFlow.collect {
                CoroutineScope(Dispatchers.Main).launch{
                    messageAdapter.submitItems(it)
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
            val messageRepository = MessageRepository(requireContext())

            // If the message has no body loaded in memory
            if (messageItem.body == null){
                messageItem.body = messageRepository.getStoredMessageBody(requireContext(),messageItem as Message)

                // If the message has no body cached
                if (messageItem.body == null) repeatUntilSuccess(requireContext(),waitUntilToken(requireContext())){
                    val networkedMessage = (getMessageBody(it,messageItem) as Message)
                    messageItem.body = networkedMessage.body
                    CoroutineScope(Dispatchers.IO).launch { messageRepository.storeMessage(requireContext(),messageItem) }
                }
            }
            CoroutineScope(Dispatchers.Main).launch {
                Log.d("Messaging", "Body of message is: $messageItem")

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

                textTitle.text = messageItem.subject
                textContent.text = Html.fromHtml(messageItem.body, Html.FROM_HTML_MODE_COMPACT)

                popupWindow.showAtLocation(activity?.findViewById(R.id.navHostFragmentMain),Gravity.TOP,0,0)


                buttonDismiss.setOnClickListener { popupWindow.dismiss() }

            }
        }
    }

}