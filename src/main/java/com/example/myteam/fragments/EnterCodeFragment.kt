package com.example.myteam.fragments

import androidx.fragment.app.Fragment
import com.example.myteam.MainActivity
import com.example.myteam.R
import com.example.myteam.activities.RegisterActivity
import com.example.myteam.utilits.*
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_enter_code.*

//Фрагмент для ввода кода подтверждения при регистрации
class EnterCodeFragment(val phoneNumber: String, val id: String) :
    Fragment(R.layout.fragment_enter_code) {

    override fun onStart() {
        super.onStart()
        (activity as RegisterActivity).title = phoneNumber
        register_input_code.addTextChangedListener(AppTextWatcher {
            val string = register_input_code.text.toString()
            if (string.length == 6) {
                enterCode()
            }
        })
    }

    //Функция проверяет код, если все нормально, производит создания информации о пользователе в БД
    private fun enterCode() {

        val code = register_input_code.text.toString()
        val credential = PhoneAuthProvider.getCredential(id, code)

        AUTH.signInWithCredential(credential).addOnCompleteListener { task ->

            if (task.isSuccessful) {

                val uid = AUTH.currentUser?.uid.toString()
                val dateMap = mutableMapOf<String, Any>()

                dateMap[CHILD_ID] = uid
                dateMap[CHILD_PHONE] = phoneNumber
                dateMap[CHILD_USERNAME] = uid
                //CHILD_USERNAME потом можно будет изменить

                //Добавляем отдельную ноду для номеров телефонов
                REF_DATABASE_ROOT.child(NODE_PHONES).child(phoneNumber).setValue(uid)
                    .addOnFailureListener {
                        showToast(it.message.toString())
                    }
                    //Если все хорошо
                    .addOnSuccessListener {
                        //Map создали, теперь его нужно записать в БД
                        //Создаем еще одну ноду
                        REF_DATABASE_ROOT.child(NODE_USERS).child(uid).updateChildren(dateMap)
                            .addOnSuccessListener {
                                showToast("Добро пожаловать в MyTeam")
                                (activity as RegisterActivity).replaceActivity(MainActivity())
                            }
                            .addOnFailureListener { showToast(it.message.toString()) }
                    }
            } else showToast(task.exception?.message.toString())
        }
    }
}
