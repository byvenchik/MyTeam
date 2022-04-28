package com.example.myteam.utilits

import android.annotation.SuppressLint
import android.net.Uri
import android.provider.ContactsContract
import com.example.myteam.models.CommonModel
import com.example.myteam.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

//Файл содержит все необходимые инструменты для работы с базой данных

lateinit var AUTH: FirebaseAuth
lateinit var CURRENT_UID: String
lateinit var REF_DATABASE_ROOT: DatabaseReference    //Для ссылки на БД
lateinit var REF_STORAGE_ROOT: StorageReference
lateinit var USER: User

//Ноды в Firebase
const val NODE_USERS = "users"
const val NODE_USERNAMES = "usernames"

//Чилды в Firebase
const val CHILD_ID = "id"
const val CHILD_PHONE = "phone"
const val CHILD_USERNAME = "username"
const val CHILD_FULLNAME = "fullname"
const val CHILD_BIO = "bio"
const val CHILD_PHOTO_URL = "photoUrl"
const val CHILD_STATE = "state"

const val FOLDER_PROFILE_IMAGE = "profile_image"

//Инициализация базы данных Firebase
fun initFirebase() {
    AUTH = FirebaseAuth.getInstance()
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference    //Обращение к элементу
    USER = User()
    CURRENT_UID = AUTH.currentUser?.uid.toString()
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference  //Ссылка на сторейдж
}

/*Для изображения
Функция высшего порядка, отпраляет полученый URL в базу данных
Без inline создаются объекты функции, а с inline просто выполняется этот код
Они не создаются и не вызываются, плюсом их можно просто подставить и все ФункПрогр*/
inline fun putUrlToDatabase(url: String, crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)  //Устанавливаем в чайлд
        .child(CHILD_PHOTO_URL).setValue(url)
        .addOnSuccessListener { function() }  //Успешно
        .addOnFailureListener { showToast(it.message.toString()) } //Нет
}

//Функция высшего порядка, получает  URL картинки из хранилища
inline fun getUrlFromStorage(path: StorageReference, crossinline function: (url: String) -> Unit) {
    path.downloadUrl
        .addOnSuccessListener { function(it.toString()) }  //Успешно
        .addOnFailureListener { showToast(it.message.toString()) } //Нет
}

//Функция высшего порядка, отправляет картинку в хранилище
//Два аргумента и лямбда
inline fun putImageToStorage(uri: Uri, path: StorageReference, crossinline function: () -> Unit) {
    path.putFile(uri)
        .addOnSuccessListener { function() }  //Успешно
        .addOnFailureListener { showToast(it.message.toString()) } //Нет
}

//Функция высшего порядка, инициализация текущей модели USER
inline fun initUser(crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
        //Надо было повесить слушатель для загрузки данных из БД и закрыть
        .addListenerForSingleValueEvent(AppValueEventListener {
            //Работает только при запуске их есть 3 типа: при запуске, постоянный, дочерний
            //Теперь данные нужно записать в юзера
            USER = it.getValue(User::class.java) ?: User()
            //Метод Firebase может принять тип как класс полностью
            //Элвис оператор Если 0, то инициализировать EmptyUser
            if (USER.username.isEmpty()) {
                USER.username = CURRENT_UID
            }
            function()
        })
}

//Функция инициализирует проверку разрешения на доступ к контактам
fun initContacts() {
    /* Функция считывает контакты с телефонной книги, хаполняет массив arrayContacts моделями CommonModel */
    if (checkPermission(READ_CONTACTS)){
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
            while (it.moveToNext()){    //Пока есть следующие элементы, двигаемся дальше
                //Не читаются контакты было без orThrow
                val fullName = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                val phone = it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val newModel = CommonModel()
                newModel.fullname = fullName
                newModel.phone = phone.replace(Regex("[\\s,-]"),"")
                arrayContacts.add(newModel)
            }
        }
        cursor?.close()
    }
}
