package com.example.myteam.utilits

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.provider.OpenableColumns
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myteam.MainActivity
import com.example.myteam.R
import com.example.myteam.database.updatePhonesToDatabase
import com.example.myteam.models.CommonModel
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

//Файл для хранения утилитарных функции, доступных во всем приложении

//Функция показывает сообщение
fun showToast(message: String) {
    Toast.makeText(APP_ACTIVITY, message, Toast.LENGTH_SHORT).show()
}

//Функция расширения для AppCompatActivity, позволяет запускать активити
fun restartActivity() {
    val intent = Intent(APP_ACTIVITY, MainActivity::class.java)
    APP_ACTIVITY.startActivity(intent)
    APP_ACTIVITY.finish()
}

//Функция расширения для AppCompatActivity, позволяет устанавливать фрагменты
fun replaceFragment(
    fragment: Fragment,
    addStack: Boolean = true
) {
    if (addStack) {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.data_container, fragment)
            .commit()
    } else {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()   //Добавляем фрагмент уже без добавления в стек
            .replace(R.id.data_container, fragment)
            .commit()
    }
}


//Функция скрывает клавиатуру
fun hideKeyboard() {
    val imm: InputMethodManager =
        APP_ACTIVITY.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(APP_ACTIVITY.window.decorView.windowToken, 0)
}

//Функция раширения ImageView, скачивает и устанавливает картинку
fun ImageView.downloadAndSetImage(url: String) {
    Picasso.get()   //Качаем изображение
        .load(url)
        .fit()  //Чтобы изображение встало в размер
        .placeholder(R.drawable.default_photo)  //Картинка которая установится
        .into(this) //Куда установить картинку
}

//Функция инициализирует проверку разрешения на доступ к контактам
fun initContacts() {
    //Функция считывает контакты с телефонной книги, хаполняет массив arrayContacts моделями CommonModel
    if (checkPermission(READ_CONTACTS)) {
        var arrayContacts = arrayListOf<CommonModel>()
        val cursor = APP_ACTIVITY.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        //Для безопасного вызова курсора
        cursor?.let {
            //Цикл для считывания
            while (it.moveToNext()) {    //Пока есть следующие элементы, двигаемся дальше
                //Не читаются контакты было без orThrow
                val fullName =
                    it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                val phone =
                    it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val newModel = CommonModel()
                newModel.fullname = fullName
                newModel.phone = phone.replace(Regex("[\\s,-]"), "")
                //Убрал пробелы в номере, чтобы норм считывать для БД
                arrayContacts.add(newModel)
            }
        }
        cursor?.close()
        //Закончили считываение, запустим функцию для сравнения
        updatePhonesToDatabase(arrayContacts)
    }
}

//Конвертирование времени из лонг в обычное
fun String.asTime(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeFormat.format(time)
}

fun getFilenameFromUri(uri: Uri): String {
    var result = ""
    val cursor = APP_ACTIVITY.contentResolver.query(uri,null,null,null,null)
    try {
        if(cursor!=null && cursor.moveToFirst()){
            result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
        }
    }catch (e:Exception){
        showToast(e.message.toString())
    }finally {
        cursor?.close()
        return result
    }
}

//Функция для кол-ва участников

fun getPlurals(count:Int) = APP_ACTIVITY.resources.getQuantityString(
    R.plurals.count_members,count,count
)