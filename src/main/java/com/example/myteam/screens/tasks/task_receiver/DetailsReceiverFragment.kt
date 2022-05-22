package com.example.myteam.screens.tasks.task_receiver

import com.example.myteam.R
import com.example.myteam.database.*
import com.example.myteam.screens.base.BaseFragment
import com.example.myteam.screens.tasks.TaskMainFragment
import com.example.myteam.utilits.APP_ACTIVITY
import com.example.myteam.utilits.downloadAndSetImage
import com.example.myteam.utilits.replaceFragment
import com.example.myteam.utilits.showToast
import kotlinx.android.synthetic.main.fragment_details_receiver.*

class DetailsReceiverFragment(
    val task_id: String,
    val task_message: String,
    val task_description: String,
    val task_date: String,
    val task_time: String,
    val task_received: String,
    val photoID: String,
    val task_status: String,
    val idSender: String
) : BaseFragment(R.layout.fragment_details_receiver) {

    //Задача выполняется
    private val mRefProgressTask =
        REF_DATABASE_ROOT.child(NODE_STATISTICS).child(USER.id).child(CHILD_PROGRESS_TASK)

    //Задача под контролем
    private val mRefControlTask =
        REF_DATABASE_ROOT.child(NODE_STATISTICS).child(idSender).child(CHILD_CONTROL_TASK)

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Подробности о задаче"
        initFields()
    }

    private fun initFields() {
        details_receiver_full_name.text = task_received
        details_receiver_user_photo.downloadAndSetImage(photoID)
        details_receiver_date.text = task_date
        details_receiver_time.text = task_time
        details_receiver_status.text = task_status
        details_receiver_title_task.text = task_message
        details_receiver_description_task.text = task_description

        details_receiver_complete_work.setOnClickListener {
            if (task_status != "Отправлена на проверку") {
                changeGoneStatusTask(task_id, idSender)
                showToast("Статус задачи изменен")
                replaceFragment(TaskMainFragment())
            } else showToast("Вы не можете выполнять задание пока оно на проверке")

        }
        details_receiver_error_work.setOnClickListener {
            if (task_status != "Отправлена на проверку") {
                changeErrorStatusTask(task_id, idSender)
                showToast("Статус задачи изменен")
                replaceFragment(TaskMainFragment())
            } else showToast("Вы не можете отказаться от задания пока оно на проверке")
        }
        details_receiver_send_to_check_work.setOnClickListener {
            if (task_status != "Отправлена на проверку") {
                if (task_status == "Выполняется") {
                    changeCheckStatusTask(task_id, idSender)
                    showToast("Статус задачи изменен")
                    replaceFragment(TaskMainFragment())
                } else showToast("Вы не можете отправить задачу, пока не приступите к ее выполнению")
            } else showToast("Вы не можете повторно отправить задачу, пока она на проверке")
        }
    }

    private fun changeCheckStatusTask(task_id: String, idSender: String) {
        val done = "Отправлена на проверку"
        REF_DATABASE_ROOT.child(NODE_TASKS).child(NODE_SENDER).child(idSender).child(task_id)
            .child(CHILD_STATUS_TASK)
            .setValue(done)
        REF_DATABASE_ROOT.child(NODE_TASKS).child(NODE_RECEIVER).child(CURRENT_UID).child(task_id)
            .child(CHILD_STATUS_TASK)
            .setValue(done)
    }

    private fun changeGoneStatusTask(task_id: String, idSender: String) {
        val done = "Выполняется"
        REF_DATABASE_ROOT.child(NODE_TASKS).child(NODE_SENDER).child(idSender).child(task_id)
            .child(CHILD_STATUS_TASK)
            .setValue(done)
        REF_DATABASE_ROOT.child(NODE_TASKS).child(NODE_RECEIVER).child(CURRENT_UID).child(task_id)
            .child(CHILD_STATUS_TASK)
            .setValue(done)

        mRefProgressTask.get().addOnSuccessListener {
            val oldValue = it.value
            if (oldValue == null) {
                val firstValue: Int = 1
                mRefProgressTask.setValue(firstValue)
            } else {
                val convertValue = oldValue.toString().toInt()
                val newValue = convertValue + 1
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
                val newValue = convertValue + 1
                mRefControlTask.setValue(newValue)
            }
        }
    }

    private fun changeErrorStatusTask(task_id: String, idSender: String) {
        val done = "Отказ исполнителя"
        REF_DATABASE_ROOT.child(NODE_TASKS).child(NODE_SENDER).child(idSender).child(task_id)
            .child(CHILD_STATUS_TASK)
            .setValue(done)
        REF_DATABASE_ROOT.child(NODE_TASKS).child(NODE_RECEIVER).child(CURRENT_UID).child(task_id)
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
