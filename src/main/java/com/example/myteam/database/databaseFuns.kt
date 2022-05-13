package com.example.myteam.database

import android.net.Uri
import com.example.myteam.R
import com.example.myteam.models.CommonModel
import com.example.myteam.models.UserModel
import com.example.myteam.screens.group_list.AddContactsFragment.Companion.listContacts
import com.example.myteam.utilits.APP_ACTIVITY
import com.example.myteam.utilits.AppValueEventListener
import com.example.myteam.utilits.TYPE_GROUP
import com.example.myteam.utilits.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.util.HashMap

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
inline fun putFileToStorage(uri: Uri, path: StorageReference, crossinline function: () -> Unit) {
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
    mapMessage[CHILD_ID] = messageKey.toString()
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

fun getMessageKey(id: String) = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID)
    .child(id).push().key.toString()

fun uploadFileToStorage(
    uri: Uri,
    messageKey: String,
    receivedID: String,
    typeMessage: String,
    filename: String = ""
) {
    val path = REF_STORAGE_ROOT.child(FOLDER_FILES).child(messageKey)
    //Функции высшего порядка
    putFileToStorage(uri, path) {
        getUrlFromStorage(path) {
            sendMessageAsFile(receivedID, it, messageKey, typeMessage, filename)
        }
    }
}

fun sendMessageAsFile(
    receivingUserID: String,
    fileUrl: String,
    messageKey: String,
    typeMessage: String,
    filename: String
) {

    //Создаем текстовую ссылку для моментального обновления
    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserID"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserID/$CURRENT_UID"

    //Map
    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID    //Отправитель
    mapMessage[CHILD_TYPE] = typeMessage
    mapMessage[CHILD_ID] = messageKey
    mapMessage[CHILD_TIME_STAMP] = ServerValue.TIMESTAMP
    mapMessage[CHILD_FILE_URL] = fileUrl
    mapMessage[CHILD_TEXT] = filename

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT
        .updateChildren(mapDialog)
        .addOnFailureListener { showToast(it.message.toString()) }

}

fun getFileFromStorage(mFile: File, fileUrl: String, function: () -> Unit) {
    val path = REF_STORAGE_ROOT.storage.getReferenceFromUrl(fileUrl)
    path.getFile(mFile)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun saveToMainList(id: String, type: String) {
    val refUser = "$NODE_CHAT_LIST/$CURRENT_UID/$id"
    val refReceived = "$NODE_CHAT_LIST/$id/$CURRENT_UID"

    //Для сохранения нод одновременно
    val mapUser = hashMapOf<String, Any>()
    val mapReceived = hashMapOf<String, Any>()

    //Заполнение мап
    mapUser[CHILD_ID] = id
    mapUser[CHILD_TYPE] = type

    mapReceived[CHILD_ID] = CURRENT_UID
    mapReceived[CHILD_TYPE] = type

    //Создаем теперь общую мапу для одновременного сохранения
    val commonMap = hashMapOf<String, Any>()
    commonMap[refUser] = mapUser
    commonMap[refReceived] = mapReceived

    REF_DATABASE_ROOT.updateChildren(commonMap)
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun deleteChat(id: String, function: () -> Unit) {
    REF_DATABASE_ROOT
        .child(NODE_CHAT_LIST)
        .child(CURRENT_UID)
        .child(id)
        .removeValue()
    REF_DATABASE_ROOT
        .child(NODE_MESSAGES)
        .child(CURRENT_UID)
        .child(id)
        .removeValue()
    REF_DATABASE_ROOT
        .child(NODE_MESSAGES)
        .child(id)
        .child(CURRENT_UID)
        .removeValue()
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun clearChat(id: String, function: () -> Unit) {
    REF_DATABASE_ROOT
        .child(NODE_MESSAGES)
        .child(CURRENT_UID)
        .child(id)
        .removeValue()
        .addOnFailureListener { showToast(it.message.toString()) }
        .addOnSuccessListener {
            REF_DATABASE_ROOT
                .child(NODE_MESSAGES)
                .child(id)
                .child(CURRENT_UID)
                .removeValue()
                .addOnSuccessListener { function() }
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun createGroupToDatabase(
    nameGroup: String,
    uri: Uri,
    listContacts: List<CommonModel>,
    function: () -> Unit
) {
    val keyGroup = REF_DATABASE_ROOT.child(NODE_GROUPS).push().key.toString()
    val path = REF_DATABASE_ROOT.child(NODE_GROUPS).child(keyGroup)
    val pathStorage = REF_STORAGE_ROOT.child(FOLDER_GROUPS_IMAGE).child(keyGroup)

    //Мап для хранения данных
    val mapData = hashMapOf<String, Any>()
    mapData[CHILD_ID] = keyGroup
    mapData[CHILD_FULLNAME] = nameGroup
    mapData[CHILD_PHOTO_URL] = "empty"

    val mapMembers = hashMapOf<String, Any>()
    listContacts.forEach {
        mapMembers[it.id] = USER_MEMBER //Изначально все пользователи
    }
    mapMembers[CURRENT_UID] = USER_CREATOR

    mapData[NODE_MEMBERS] = mapMembers

    path.updateChildren(mapData)
        .addOnSuccessListener {

            if (uri != Uri.EMPTY) {
                putFileToStorage(uri, pathStorage) {
                    getUrlFromStorage(pathStorage) {
                        path.child(CHILD_PHOTO_URL).setValue(it)
                        addGroupsToGroopList(mapData, listContacts) {
                            function()
                        }
                    }
                }
            } else {
                addGroupsToGroopList(mapData, listContacts) {
                    function()
                }
            }

        }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun addGroupsToGroopList(
    mapData: HashMap<String, Any>,
    listContacts: List<CommonModel>,
    function: () -> Unit
) {
    val path = REF_DATABASE_ROOT.child(NODE_GROUP_LIST)
    val map = hashMapOf<String, Any>()

    map[CHILD_ID] = mapData[CHILD_ID].toString()
    map[CHILD_TYPE] = TYPE_GROUP
    listContacts.forEach {
        path.child(it.id).child(map[CHILD_ID].toString()).updateChildren(map)
    }
    //Для текущего пользователя
    path.child(CURRENT_UID).child(map[CHILD_ID].toString()).updateChildren(map)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

/*Отправка сообщений в групповой чат*/
fun sendMessageToGroup(message: String, groupID: String, typeText: String, function: () -> Unit) {

    val refMessages = "$NODE_GROUPS/$groupID/$NODE_MESSAGES"
    val messageKey = REF_DATABASE_ROOT.child(refMessages).push().key

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_FROM_USERNAME] = USER.fullname
    mapMessage[CHILD_TYPE] = typeText
    mapMessage[CHILD_TEXT] = message
    mapMessage[CHILD_ID] = messageKey.toString()
    mapMessage[CHILD_TIME_STAMP] = ServerValue.TIMESTAMP

    REF_DATABASE_ROOT.child(refMessages).child(messageKey.toString())
        .updateChildren(mapMessage)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

/*Для отправки задач*/

/*fun sendTaskToDatabase(
    task: String,
    description: String,
    receivingUserID: String,
    type: String
) {
    val refTaskUser = "$NODE_TASKS/$CURRENT_UID/$receivingUserID"
    val refTaskReceived = "$NODE_TASKS/$receivingUserID/$CURRENT_UID"
    val messageKey = REF_DATABASE_ROOT.child(refTaskUser).push().key
    //Map
    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_FROM_USERNAME] = USER.fullname
    mapMessage[CHILD_ID] = messageKey.toString()
    mapMessage[CHILD_TASK] = task
    mapMessage[CHILD_DESCRIPTION] = description
    mapMessage[CHILD_TYPE] = type
    mapMessage[CHILD_TIME_STAMP] = ServerValue.TIMESTAMP

    val mapTask = hashMapOf<String, Any>()
    mapTask["$refTaskUser/$messageKey"] = mapMessage
    mapTask["$refTaskReceived/$messageKey"] = mapMessage

    REF_DATABASE_ROOT
        .updateChildren(mapTask)
}*/

fun testSendMessage(message: String, receivingUserID: String, typeText: String, function: () -> Unit) {
    val refTaskUser = "$NODE_TASKS/$NODE_SENDER/$CURRENT_UID"
    val refTaskReceivingUser = "$NODE_TASKS/$NODE_RECEIVER/$receivingUserID"
    val taskKey = REF_DATABASE_ROOT.child(refTaskUser).push().key

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_TYPE] = typeText
    mapMessage[CHILD_TEXT] = message
    mapMessage[CHILD_TIME_STAMP] = ServerValue.TIMESTAMP

    val mapDialog = hashMapOf<String,Any>()
    mapDialog["$refTaskUser/$taskKey"] = mapMessage
    mapDialog["$refTaskReceivingUser/$taskKey"] = mapMessage

    REF_DATABASE_ROOT
        .updateChildren(mapDialog)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }

}

