package com.example.myteam.database

import com.example.myteam.models.CommonModel
import com.example.myteam.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference

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
const val CHILD_FROM_USERNAME = "from_username"
const val CHILD_TYPE = "type"
const val CHILD_FROM = "from"
const val CHILD_TIME_STAMP = "timeStamp"
const val CHILD_FILE_URL = "fileUrl"
const val FOLDER_PROFILE_IMAGE = "profile_image"    //Папка для хранения изображений
const val FOLDER_FILES = "messages_files"

//Для чатов
const val NODE_CHAT_LIST = "chat_list"

const val NODE_GROUP_LIST="group_list"

const val NODE_MEMBERS = "members"
const val NODE_GROUPS = "groups"
const val FOLDER_GROUPS_IMAGE = "groups_image"
const val USER_CREATOR = "creator"
const val USER_ADMIN = "admin"
const val USER_MEMBER = "member"

const val NODE_TASKS = "tasks"
const val NODE_RECEIVER = "receiver"
const val NODE_SENDER = "sender"

const val CHILD_TASK = "task"
const val CHILD_STATUS_TASK = "status_task"
const val CHILD_DESCRIPTION = "description"
const val CHILD_RECEIVED = "received"
const val CHILD_RECEIVED_ID = "received_id"
const val CHILD_RECEIVED_PHOTO = "received_photo"
const val CHILD_FROM_PHOTO = "from_photo"
const val CHILD_TASK_ID = "task_id"

const val NODE_STATISTICS = "statistics"
const val CHILD_COMPLETED_TASK = "completed_task"
const val CHILD_GET_COMPLETED_TASK = "get_completed_task"

const val CHILD_UNFULFILLED_TASK = "unfulfilled_task"
const val CHILD_GET_UNFULFILLED_TASK = "get_unfulfilled_task"

const val CHILD_DECLINE_TASK = "decline_task"
const val CHILD_GET_DECLINE_TASK = "get_decline_task"

const val CHILD_PROGRESS_TASK = "progress_task"
const val CHILD_CONTROL_TASK = "control_task"