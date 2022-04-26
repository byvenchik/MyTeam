package com.example.myteam.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myteam.R
import com.example.myteam.databinding.ActivityRegisterBinding
import com.example.myteam.fragments.EnterPhoneNumberFragment
import com.example.myteam.utilits.initFirebase
import com.example.myteam.utilits.replaceFragment

//Активность для регистрации нового пользователя
class RegisterActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityRegisterBinding
    private lateinit var mToolbar: androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initFirebase()
    }

    override fun onStart() {
        super.onStart()
        mToolbar = mBinding.registerToolbar
        setSupportActionBar(mToolbar)
        title = getString(R.string.register_title_your_phone)
        replaceFragment(EnterPhoneNumberFragment(), false)
    }
}