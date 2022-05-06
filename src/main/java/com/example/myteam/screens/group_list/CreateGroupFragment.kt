package com.example.myteam.screens.group_list

import androidx.recyclerview.widget.RecyclerView
import com.example.myteam.R
import com.example.myteam.models.CommonModel
import com.example.myteam.screens.base.BaseFragment
import com.example.myteam.utilits.APP_ACTIVITY
import com.example.myteam.utilits.showToast
import kotlinx.android.synthetic.main.fragment_create_group.*

class CreateGroupFragment(private var listContacts:List<CommonModel>): BaseFragment(R.layout.fragment_create_group) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AddContactsAdapter

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Создать команду"
        initRecyclerView()
        create_group_btn_complete.setOnClickListener { showToast("click") }
        create_group_input_name.requestFocus()
    }

    private fun initRecyclerView() {
        mRecyclerView = create_group_recycle_view
        mAdapter = AddContactsAdapter()
        mRecyclerView.adapter = mAdapter
        listContacts.forEach{ mAdapter.updateListItems(it) }
    }
}