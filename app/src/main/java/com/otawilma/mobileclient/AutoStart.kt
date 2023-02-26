package com.otawilma.mobileclient

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.otawilma.mobileclient.messaging.MessageWatcher

class AutoStart : BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) context!!.startService(Intent(context, MessageWatcher::class.java))
    }
}