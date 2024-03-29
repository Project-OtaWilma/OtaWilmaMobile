package com.otawilma.mobileclient.messaging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.otawilma.mobileclient.R
import com.otawilma.mobileclient.dataClasses.Message
import com.otawilma.mobileclient.dataClasses.MessageItem

class MessageAdapter(private val onClickListener : MessageClickListener) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private var items : List<MessageItem> = listOf()

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(messageItem: MessageItem){
            val messageSubject = itemView.findViewById<TextView>(R.id.textViewRecyclerviewMessageSubject)
            val messageSender = itemView.findViewById<TextView>(R.id.textViewRecyclerviewMessageSender)
            val messageTimestamp = itemView.findViewById<TextView>(R.id.textViewRecyclerviewMessageTimeStamp)

            messageSubject.text = messageItem.subject
            messageSender.text = messageItem.senders!!.joinToString(separator = " / ") { it.name }
            val ts = messageItem.timestamp
            messageTimestamp.text = "${ts?.dayOfMonth}.${ts?.monthValue}.${ts?.year} | ${ts?.hour}:${ts?.minute}"

            if (messageItem is Message && messageItem.new){
                messageSubject.setTextColor(ContextCompat.getColor(itemView.context,R.color.black))
            }
        }
    }

    fun submitItems(list: List<MessageItem>){
        items = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layout = R.layout.item_message

        return MessageViewHolder(LayoutInflater.from(parent.context).inflate(layout,parent,false))
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val currentItem = items[position]
        holder.itemView.setOnClickListener{
            onClickListener.onClick(currentItem)
        }
        holder.bind(currentItem)
    }

    override fun getItemCount() = items.size
}

interface MessageClickListener{
    fun onClick (messageItem: MessageItem)
}