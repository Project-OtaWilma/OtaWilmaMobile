package com.otawilma.mobileclient.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.otawilma.mobileclient.OtawilmaNetworking
import com.otawilma.mobileclient.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FragmentMessages : Fragment(R.layout.fragment_messages), OtawilmaNetworking {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            Log.d("Messaging","${getMessages(10)}")
        }

    }

}