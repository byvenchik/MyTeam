package com.example.myteam.objects

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.myteam.screens.task.task_receiver.ReceiverTaskFragment
import com.example.myteam.screens.task.task_sender.SenderTaskFragment


class TabLayoutAdapter( fm: FragmentManager, var totalTabs: Int) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> {
                ReceiverTaskFragment()
            }
            1 -> {
                SenderTaskFragment()
            }
            else -> getItem(position)
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }

}
