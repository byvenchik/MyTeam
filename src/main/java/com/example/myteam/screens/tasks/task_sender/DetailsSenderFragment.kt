package com.example.myteam.screens.tasks.task_sender

import android.view.View
import com.example.myteam.R
import com.example.myteam.database.*
import com.example.myteam.screens.base.BaseFragment
import com.example.myteam.screens.tasks.TaskMainFragment
import com.example.myteam.utilits.APP_ACTIVITY
import com.example.myteam.utilits.downloadAndSetImage
import com.example.myteam.utilits.replaceFragment
import com.example.myteam.utilits.showToast
import kotlinx.android.synthetic.main.fragment_details_sender.*

class DetailsSenderFragment(
    val task_id: String,
    val task_message: String,
    val task_description: String,
    val task_date: String,
    val task_time: String,
    val task_received: String,
    val photoID: String,
    val task_status: String,
    val idReceived: String
) : BaseFragment(R.layout.fragment_details_sender) {

    //Для задач
    private val refTaskUser = REF_DATABASE_ROOT.child(NODE_TASKS).child(NODE_SENDER).child(USER.id)
    private val refTaskReceivingUser = REF_DATABASE_ROOT.child(NODE_TASKS).child(NODE_RECEIVER).child(idReceived)
    //Для положительной статистики
    private val mRefCompletedTask = REF_DATABASE_ROOT.child(NODE_STATISTICS).child(idReceived).child(CHILD_COMPLETED_TASK)
    private val mRefCompletedGetTask = REF_DATABASE_ROOT.child(NODE_STATISTICS).child(USER.id).child(CHILD_GET_COMPLETED_TASK)
    //Для отрицательной статистики
    private val mRefUnfulfilledTask = REF_DATABASE_ROOT.child(NODE_STATISTICS).child(idReceived).child(CHILD_UNFULFILLED_TASK)
    private val mRefUnfulfilledGetTask = REF_DATABASE_ROOT.child(NODE_STATISTICS).child(USER.id).child(CHILD_GET_UNFULFILLED_TASK)
    //Для отказанных задач
    private val mRefDeclineTask = REF_DATABASE_ROOT.child(NODE_STATISTICS).child(idReceived).child(CHILD_DECLINE_TASK)
    private val mRefDeclineGetTask = REF_DATABASE_ROOT.child(NODE_STATISTICS).child(USER.id).child(CHILD_GET_DECLINE_TASK)
    //Задача выполняется
    private val mRefProgressTask = REF_DATABASE_ROOT.child(NODE_STATISTICS).child(idReceived).child(CHILD_PROGRESS_TASK)
    //Задача под контролем
    private val mRefControlTask = REF_DATABASE_ROOT.child(NODE_STATISTICS).child(USER.id).child(CHILD_CONTROL_TASK)

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Подробности о задаче"
        initFields()

    }


    private fun initFields() {
        details_full_name.text = task_received
        details_user_photo.downloadAndSetImage(photoID)
        details_date.text = task_date
        details_time.text = task_time
        details_status.text = task_status
        details_title_task.text = task_message
        details_description_task.text = task_description


        if (task_status == "Принята" || task_status == "Не принята" || task_status == "Отказ исполнителя") {
            details_star.visibility = View.VISIBLE
            val getStatusTask: String = task_status
            val taskID: String = task_id
            val receivedID: String = idReceived
            val fromID: String = USER.id
            details_star.setOnClickListener {
                changeStatisticsToDatabase(getStatusTask, taskID)
            }
        }

        details_complete_work.setOnClickListener {
            if (task_status != "Отказ исполнителя") {
                if (task_status != "Отправлена исполнителю") {
                    if (task_status != "Принята") {
                        changeGoneStatusTask(task_id, idReceived)
                        showToast("Статус задачи изменен")
                        replaceFragment(TaskMainFragment())
                    } else showToast("Вы не можете повторно принять задачу")
                } else showToast("Вы не можете изменять статус задачи, пока исполнитель не проверит")
            } else showToast("Исполнитель отказался выполнять ваше задание, вы не можете изменить статус задачи")
        }
        details_error_work.setOnClickListener {
            if (task_status != "Отказ исполнителя") {
                if (task_status != "Отправлена исполнителю") {
                    if (task_status != "Не принята") {
                        changeErrorStatusTask(task_id, idReceived)
                        showToast("Статус задачи изменен")
                        replaceFragment(TaskMainFragment())
                    } else showToast("Вы не можете повторно не принять задачу")
                } else showToast("Вы не можете изменять статус задачи, пока исполнитель не проверит")
            } else showToast("Исполнитель отказался выполнять ваше задание, вы не можете изменить статус задачи")
        }
    }

    private fun changeStatisticsToDatabase(
        statusTask: String,
        taskID: String
    ) {
        when (statusTask) {
            "Принята" -> {
                mRefCompletedTask.get().addOnSuccessListener {
                    val oldValue = it.value
                    if (oldValue == null) {
                        val firstValue: Int = 1
                        mRefCompletedTask.setValue(firstValue)
                    } else {
                        val convertValue = oldValue.toString().toInt()
                        val newValue = convertValue + 1
                        mRefCompletedTask.setValue(newValue)
                    }
                }
                mRefCompletedGetTask.get().addOnSuccessListener {
                    val oldValue = it.value
                    if (oldValue == null) {
                        val firstValue: Int = 1
                        mRefCompletedGetTask.setValue(firstValue)
                    } else {
                        val convertValue = oldValue.toString().toInt()
                        val newValue = convertValue + 1
                        mRefCompletedGetTask.setValue(newValue)
                    }
                }
            }
            "Не принята" -> {
                mRefUnfulfilledTask.get().addOnSuccessListener {
                    val oldValue = it.value
                    if (oldValue == null) {
                        val firstValue: Int = 1
                        mRefUnfulfilledTask.setValue(firstValue)
                    } else {
                        val convertValue = oldValue.toString().toInt()
                        val newValue = convertValue + 1
                        mRefUnfulfilledTask.setValue(newValue)
                    }
                }
                mRefUnfulfilledGetTask.get().addOnSuccessListener {
                    val oldValue = it.value
                    if (oldValue == null) {
                        val firstValue: Int = 1
                        mRefUnfulfilledGetTask.setValue(firstValue)
                    } else {
                        val convertValue = oldValue.toString().toInt()
                        val newValue = convertValue + 1
                        mRefUnfulfilledGetTask.setValue(newValue)
                    }
                }
            }
            "Отказ исполнителя" -> {
                mRefDeclineTask.get().addOnSuccessListener {
                    val oldValue = it.value
                    if (oldValue == null) {
                        val firstValue: Int = 1
                        mRefDeclineTask.setValue(firstValue)
                    } else {
                        val convertValue = oldValue.toString().toInt()
                        val newValue = convertValue + 1
                        mRefDeclineTask.setValue(newValue)
                    }
                }
                mRefDeclineGetTask.get().addOnSuccessListener {
                    val oldValue = it.value
                    if (oldValue == null) {
                        val firstValue: Int = 1
                        mRefDeclineGetTask.setValue(firstValue)
                    } else {
                        val convertValue = oldValue.toString().toInt()
                        val newValue = convertValue + 1
                        mRefDeclineGetTask.setValue(newValue)
                    }
                }
            }
        }
        refTaskUser.child(taskID).removeValue()
        refTaskReceivingUser.child(taskID).removeValue()
        replaceFragment(TaskMainFragment())
    }


    private fun changeGoneStatusTask(task_id: String, idReceived: String) {
        val done = "Принята"
        REF_DATABASE_ROOT.child(NODE_TASKS).child(NODE_SENDER).child(CURRENT_UID).child(task_id)
            .child(CHILD_STATUS_TASK)
            .setValue(done)
        REF_DATABASE_ROOT.child(NODE_TASKS).child(NODE_RECEIVER).child(idReceived).child(task_id)
            .child(CHILD_STATUS_TASK)
            .setValue(done)

        mRefProgressTask.get().addOnSuccessListener {
            val oldValue = it.value
            if (oldValue == null) {
                val firstValue: Int = 1
                mRefProgressTask.setValue(firstValue)
            } else {
                val convertValue = oldValue.toString().toInt()
                val newValue = convertValue - 1
                mRefProgressTask.setValue(newValue)
            }
        }
        mRefControlTask.get().addOnSuccessListener {
            val oldValue = it.value
            if (oldValue == null) {
                val firstValue: Int = 1
                mRefControlTask.setValue(firstValue)
            } else {
                val convertValue = oldValue.toString().toInt()
                val newValue = convertValue - 1
                mRefControlTask.setValue(newValue)
            }
        }
    }

    private fun changeErrorStatusTask(task_id: String, idReceived: String) {
        val done = "Не принята"
        REF_DATABASE_ROOT.child(NODE_TASKS).child(NODE_SENDER).child(CURRENT_UID).child(task_id)
            .child(CHILD_STATUS_TASK)
            .setValue(done)
        REF_DATABASE_ROOT.child(NODE_TASKS).child(NODE_RECEIVER).child(idReceived).child(task_id)
            .child(CHILD_STATUS_TASK)
            .setValue(done)

        mRefProgressTask.get().addOnSuccessListener {
            val oldValue = it.value
            if (oldValue == null) {
                val firstValue: Int = 1
                mRefProgressTask.setValue(firstValue)
            } else {
                val convertValue = oldValue.toString().toInt()
                val newValue = convertValue - 1
                mRefProgressTask.setValue(newValue)
            }
        }
        mRefControlTask.get().addOnSuccessListener {
            val oldValue = it.value
            if (oldValue == null) {
                val firstValue: Int = 1
                mRefControlTask.setValue(firstValue)
            } else {
                val convertValue = oldValue.toString().toInt()
                val newValue = convertValue - 1
                mRefControlTask.setValue(newValue)
            }
        }
    }
}
