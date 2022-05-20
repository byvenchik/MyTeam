package com.example.myteam.screens.main

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.myteam.R
import com.example.myteam.database.*
import com.example.myteam.models.CommonModel
import com.example.myteam.models.UserModel
import com.example.myteam.utilits.APP_ACTIVITY
import com.example.myteam.utilits.AppValueEventListener
import com.example.myteam.utilits.downloadAndSetImage
import com.example.myteam.utilits.hideKeyboard
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_settings.*

//Главный фрагмент
class StatisticsFragment : Fragment(R.layout.fragment_main) {

    private lateinit var mListenerInfoStatistics: AppValueEventListener
    private var mRefStatistics = REF_DATABASE_ROOT.child(NODE_STATISTICS).child(USER.id)

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Cтатистика"
        APP_ACTIVITY.mAppDrawer.enableDrawer()  //Его надо включать после входа
        hideKeyboard()
        initFields()
        initStatistics()
    }

    private fun initFields() {
        statistics_user_photo.downloadAndSetImage(USER.photoUrl)
        statistics_fullname.text = USER.fullname
    }

    private fun initStatistics() {
        mRefStatistics.child(CHILD_PROGRESS_TASK).get().addOnSuccessListener {
            if (it.value != null) {
                count_progress.text = it.value.toString()
            }
        }
        mRefStatistics.child(CHILD_CONTROL_TASK).get().addOnSuccessListener {
            if (it.value != null) {
                count_control.text = it.value.toString()
            }
        }
        mRefStatistics.child(CHILD_COMPLETED_TASK).get().addOnSuccessListener {
            if (it.value != null) {
                count_complete_task.text = it.value.toString()
            }
        }
        mRefStatistics.child(CHILD_UNFULFILLED_TASK).get().addOnSuccessListener {
            if (it.value != null) {
                count_not_complete_task.text = it.value.toString()
            }
        }
        mRefStatistics.child(CHILD_DECLINE_TASK).get().addOnSuccessListener {
            if (it.value != null) {
                count_decline_task.text = it.value.toString()
            }
        }
        mRefStatistics.child(CHILD_GET_COMPLETED_TASK).get().addOnSuccessListener {
            if (it.value != null) {
                count_get_complete_task.text = it.value.toString()
            }
        }
        mRefStatistics.child(CHILD_GET_UNFULFILLED_TASK).get().addOnSuccessListener {
            if (it.value != null) {
                count_not_get_complete_task.text = it.value.toString()
            }
        }
        mRefStatistics.child(CHILD_GET_DECLINE_TASK).get().addOnSuccessListener {
            if (it.value != null) {
                count_decline_get_task.text = it.value.toString()
            }
        }

    }
}







