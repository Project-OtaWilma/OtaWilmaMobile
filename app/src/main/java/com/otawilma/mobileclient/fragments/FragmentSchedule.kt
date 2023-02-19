package com.otawilma.mobileclient.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.otawilma.mobileclient.OtawilmaNetworking
import com.otawilma.mobileclient.R

class FragmentSchedule : Fragment(R.layout.fragment_schedule), OtawilmaNetworking {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.title = getString(R.string.app_name) + " - Schedule"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}