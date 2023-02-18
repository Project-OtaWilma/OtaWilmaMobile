package com.otawilma.mobileclient.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.otawilma.mobileclient.R

class FragmentSchedule:Fragment(R.layout.fragment_schedule){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.title = getString(R.string.app_name) + " - Schedule"
    }
}