package com.example.myteam.objects

import android.content.Context
import android.net.wifi.hotspot2.pps.HomeSp
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.myteam.screens.AcceptTaskFragment
import com.example.myteam.screens.SubmitTaskFragment
import com.example.myteam.screens.task.TaskMainFragment

class TabLayoutAdapter( fm: FragmentManager, var totalTabs: Int) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> {
                AcceptTaskFragment()
            }
            1 -> {
                SubmitTaskFragment()
            }
            else -> getItem(position)
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }

}