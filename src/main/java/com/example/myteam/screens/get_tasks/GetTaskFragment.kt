package com.example.myteam.screens.get_tasks

import androidx.fragment.app.Fragment
import com.example.myteam.R
import com.example.myteam.database.*
import com.example.myteam.models.CommonModel
import com.example.myteam.models.UserModel
import com.example.myteam.utilits.downloadAndSetImage
import com.example.myteam.utilits.showToast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ServerValue
import kotlinx.android.synthetic.main.contact_item.*
import kotlinx.android.synthetic.main.fragment_get_task.*

class GetTaskFragment(private val contact: CommonModel)
    : Fragment(R.layout.fragment_get_task) {

    private lateinit var mRefUser: DatabaseReference
    private lateinit var mReceivingUser: UserModel

    override fun onResume() {
        super.onResume()
        initFields()
    }

    private fun initFields() {
        mRefUser = REF_DATABASE_ROOT.child(NODE_USERS).child(contact.id)
        task_received_photo.downloadAndSetImage(contact.photoUrl)
        task_received_full_name.text = contact.fullname
        task_received_status.text = contact.state

        add_task_btn.setOnClickListener {
            checkValue()
        }
    }

    private fun checkValue() {
    val task = task_input_title.text
        val description = task_input_description.text
        if(task.isEmpty()){
            showToast("Введите наименование задачи")
        }else{
            testSendMessage(task.toString(),description.toString(),contact.id){
                task_input_title.setText("")
            }
        }
    //else {sendTaskToDatabase(task.toString(),description.toString(),contact.id, TYPE_TEXT) }
    }

    private fun testSendMessage(message: String, description: String, receivingUserID: String, function: () -> Unit) {
        val refTaskUser = "$NODE_TASKS/$NODE_SENDER/$CURRENT_UID"
        val refTaskReceivingUser = "$NODE_TASKS/$NODE_RECEIVER/$receivingUserID"
        val taskKey = REF_DATABASE_ROOT.child(refTaskUser).push().key

        val mapMessage = hashMapOf<String, Any>()
        mapMessage[CHILD_TASK] = message
        mapMessage[CHILD_DESCRIPTION] = description
        mapMessage[CHILD_FROM] = CURRENT_UID
        mapMessage[CHILD_FROM_USERNAME] = USER.fullname
        mapMessage[CHILD_RECEIVED] = contact.fullname
        mapMessage[CHILD_RECEIVED_ID] = contact.id
        mapMessage[CHILD_TIME_STAMP] = ServerValue.TIMESTAMP

        val mapDialog = hashMapOf<String,Any>()
        mapDialog["$refTaskUser/$taskKey"] = mapMessage
        mapDialog["$refTaskReceivingUser/$taskKey"] = mapMessage

        REF_DATABASE_ROOT
            .updateChildren(mapDialog)
            .addOnSuccessListener { function() }
            .addOnFailureListener { showToast(it.message.toString()) }

    }
}
