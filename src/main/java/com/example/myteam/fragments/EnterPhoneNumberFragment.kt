package com.example.myteam.fragments

import androidx.fragment.app.Fragment
import com.example.myteam.MainActivity
import com.example.myteam.R
import com.example.myteam.activities.RegisterActivity
import com.example.myteam.utilits.AUTH
import com.example.myteam.utilits.replaceActivity
import com.example.myteam.utilits.replaceFragment
import com.example.myteam.utilits.showToast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_enter_phone_number.*
import java.util.concurrent.TimeUnit

//Фрагмент для ввода номера телефона при регистрации
class EnterPhoneNumberFragment : Fragment(R.layout.fragment_enter_phone_number) {

    private lateinit var mPhoneNumber: String
    private lateinit var mCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onStart() {
        super.onStart()

        //Callback который возвращает результат верификации
        mCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                /*Когда все успешно передаем объект
                Функция срабатывает если верификация уже была произведена,
                пользователь авторизируется в приложении без потверждения по смс*/
                AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showToast("Добро пожаловать в MyTeam")
                        (activity as RegisterActivity).replaceActivity(MainActivity())
                    } else showToast(task.exception?.message.toString())
                }
            }

            //Функция срабатывает если верификация не удалась
            override fun onVerificationFailed(p0: FirebaseException) {
                showToast(p0.message.toString())
            }

            //Функция срабатывает если верификация впервые, и отправлена смс
            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                replaceFragment(EnterCodeFragment(mPhoneNumber, id))
            }
        }
        register_btn_next.setOnClickListener { sendCode() }
    }

    /* Функция проверяет поле для ввода номер телефона, если поле пустое выводит сообщение.
    Если поле не пустое, то начинает процедуру авторизации/регистрации */
    private fun sendCode() {
        if (register_input_phone_number.text.toString().isEmpty()) {
            showToast(getString(R.string.register_toast_enter_phone))
        } else {
            authUser()
        }
    }

    //Инициализация
    private fun authUser() {
        //Получаем номер телефона
        mPhoneNumber = register_input_phone_number.text.toString()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            mPhoneNumber,
            60,
            TimeUnit.SECONDS,
            activity as RegisterActivity,
            mCallback
        )
    }
}

