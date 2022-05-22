package com.example.myteam.screens.tasks

import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.myteam.R
import com.example.myteam.objects.TabLayoutAdapter
import com.example.myteam.utilits.APP_ACTIVITY
import com.google.android.material.tabs.TabLayout

class TaskMainFragment : Fragment(R.layout.fragment_task_main) {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Задачи"
        goPager()
    }

   private fun goPager() {
        tabLayout = APP_ACTIVITY.findViewById(R.id.tablayout_for_tasks)
        viewPager = APP_ACTIVITY.findViewById(R.id.view_pager)
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = TabLayoutAdapter(childFragmentManager, tabLayout.tabCount)
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
}

