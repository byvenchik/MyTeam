package com.example.myteam.utilits

import android.net.Uri
import android.provider.ContactsContract
import com.example.myteam.R
import com.example.myteam.models.CommonModel
import com.example.myteam.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

//Файл содержит все необходимые инструменты для работы с базой данных

lateinit var AUTH: FirebaseAuth
lateinit var CURRENT_UID: String
lateinit var REF_DATABASE_ROOT: DatabaseReference    //Для ссылки на БД
lateinit var REF_STORAGE_ROOT: StorageReference
lateinit var USER: UserModel

const val TYPE_TEXT = "text"

//Ноды в Firebase
const val NODE_USERS = "users"
const val NODE_USERNAMES = "usernames"
const val NODE_PHONES = "phones"

const val NODE_MESSAGES = "messages"

//В ней отдельно будем хранить телефоны клиентов, так удобнее при загрузке контактов
const val NODE_PHONES_CONTACTS = "phones_contacts"
//Контакты, которые тоже пользуются приложением


//Чилды в Firebase
const val CHILD_ID = "id"
const val CHILD_PHONE = "phone"
const val CHILD_USERNAME = "username"
const val CHILD_FULLNAME = "fullname"
const val CHILD_BIO = "bio"
const val CHILD_PHOTO_URL = "photoUrl"
const val CHILD_STATE = "state"

//Добавил для сообщений
const val CHILD_TEXT = "text"
const val CHILD_TYPE = "type"
const val CHILD_FROM = "from"
const val CHILD_TIME_STAMP = "timeStamp"

const val FOLDER_PROFILE_IMAGE = "profile_image"

//Инициализация базы данных Firebase
fun initFirebase() {
    AUTH = FirebaseAuth.getInstance()
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference    //Обращение к элементу
    USER = UserModel()
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
            USER = it.getValue(UserModel::class.java) ?: UserModel()
            //Метод Firebase может принять тип как класс полностью
            //Элвис оператор Если 0, то инициализировать EmptyUser
            if (USER.username.isEmpty()) {
                USER.username = CURRENT_UID
            }
            function()
        })
}

fun updatePhonesToDatabase(arrayContacts: ArrayList<CommonModel>) {
    //Проверка
    if (AUTH.currentUser != null) {
        //Считываем ноду и делаем одиночный запрос
        REF_DATABASE_ROOT.child(NODE_PHONES).addListenerForSingleValueEvent(AppValueEventListener {
            //Проходим по всем номерам
            it.children.forEach { snapshot ->
                arrayContacts.forEach { contact ->
                    if (snapshot.key == contact.phone) {
                        REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
                            .child(snapshot.value.toString()).child(CHILD_ID)   //Уже значение
                            .setValue(snapshot.value.toString())
                            .addOnFailureListener { showToast(it.message.toString()) }

                        REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
                            .child(snapshot.value.toString()).child(CHILD_FULLNAME)   //Уже значение
                            .setValue(contact.fullname)
                            .addOnFailureListener { showToast(it.message.toString()) }
                    }
                }
            }
        })
    }
}

//Для выгрузки контактов
//Если не будет ничего, то вернем пустую модель
fun DataSnapshot.getCommonModel(): CommonModel =
    this.getValue(CommonModel::class.java) ?: CommonModel()


fun DataSnapshot.getUserModel(): UserModel =
    this.getValue(UserModel::class.java) ?: UserModel()


fun sendMessage(message: String, receivingUserID: String, typeText: String, function: () -> Unit) {

    //Создаем текстовую ссылку для моментального обновления
    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserID"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserID/$CURRENT_UID"
    //Уникальный ключ для сообщения
    val messageKey = REF_DATABASE_ROOT.child(refDialogUser).push().key
    //Map
    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID    //Отправитель
    mapMessage[CHILD_TYPE] = typeText
    mapMessage[CHILD_TEXT] = message    //Сообщение
    mapMessage[CHILD_TIME_STAMP] = ServerValue.TIMESTAMP

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT
        .updateChildren(mapDialog)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }

}

//Обновление username в базе данных у текущего пользователя
fun updateCurrentUsername(newUserName: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_USERNAME)
        .setValue(newUserName)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                showToast(APP_ACTIVITY.getString(R.string.toast_data_update))
                deleteOldUsername(newUserName)
            } else {
                showToast(it.exception?.message.toString())
            }
        }
}

//Удаление старого username из базы данных
private fun deleteOldUsername(newUserName: String) {
    REF_DATABASE_ROOT.child(NODE_USERNAMES).child(USER.username).removeValue()
        .addOnSuccessListener {
            showToast(APP_ACTIVITY.getString(R.string.toast_data_update))
            APP_ACTIVITY.supportFragmentManager.popBackStack()  //По стеку назад
            USER.username = newUserName    //Обновить модель
        }.addOnFailureListener {
            showToast(it.message.toString())
        }
}

fun setBioToDatabase(newBio: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_BIO)
        .setValue(newBio)
        .addOnSuccessListener {
            showToast(APP_ACTIVITY.getString(R.string.toast_data_update))
            USER.bio = newBio
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }.addOnFailureListener {
            showToast(it.message.toString())
        }
}


fun setNameToDatabase(fullname: String) {
    //Обращение к БД
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_FULLNAME)
        .setValue(fullname)
        .addOnSuccessListener { //Передаем значение и вешаем слушатель
            showToast(APP_ACTIVITY.getString(R.string.toast_data_update)) //Обновлены
            USER.fullname = fullname
            APP_ACTIVITY.mAppDrawer.updateHeader()  //Обновление имени в шапке
            APP_ACTIVITY.supportFragmentManager.popBackStack() //Переход по стеку назад
        }.addOnFailureListener {
            showToast(it.message.toString())
        }
}
