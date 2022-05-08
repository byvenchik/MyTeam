package com.example.myteam.screens.group_list

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.example.myteam.R
import com.example.myteam.database.*
import com.example.myteam.models.CommonModel
import com.example.myteam.screens.base.BaseFragment
import com.example.myteam.screens.chat_list.ChatListFragment
import com.example.myteam.utilits.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_create_group.*
import kotlinx.android.synthetic.main.fragment_settings.*

class CreateGroupFragment(private var listContacts: List<CommonModel>) :
    BaseFragment(R.layout.fragment_create_group) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AddContactsAdapter

    //Для обновления картинки команды
    private var mUri = Uri.EMPTY

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Создать команду"
        initRecyclerView()
        create_group_photo.setOnClickListener { addPhoto() }
        create_group_btn_complete.setOnClickListener {
            val nameGroup = create_group_input_name.text.toString()
            if(nameGroup.isEmpty()){
                showToast("Введите название команды")
            } else {
                createGroupToDatabase(nameGroup,mUri,listContacts){
                    replaceFragment(GroupListFragment())
                }
            }
        }
        create_group_input_name.requestFocus()
        create_group_counts.text = getPlurals(listContacts.size)
    }



    private fun addPhoto() {
        CropImage.activity()
            .setAspectRatio(1, 1)    //Чтобы окно было пропорционально
            .setRequestedSize(250, 250)  //Размер картинки
            .setCropShape(CropImageView.CropShape.OVAL)   //Овальный кроппер
            .start(APP_ACTIVITY, this)
    }

    private fun initRecyclerView() {
        mRecyclerView = create_group_recycle_view
        mAdapter = AddContactsAdapter()
        mRecyclerView.adapter = mAdapter
        listContacts.forEach { mAdapter.updateListItems(it) }
    }

    //Активность которая запускается для получения картинки для фото пользователя
    //Перехват фото-активити
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK && data != null
        ) {  //Тогда нам нужен URI
            mUri = CropImage.getActivityResult(data).uri
            create_group_photo.setImageURI(mUri)
        }
    }
}