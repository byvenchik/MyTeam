package com.example.myteam.objects

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.drawerlayout.widget.DrawerLayout
import com.example.myteam.R
import com.example.myteam.screens.contacts.ContactsFragment
import com.example.myteam.screens.settings.SettingsFragment
import com.example.myteam.utilits.APP_ACTIVITY
import com.example.myteam.database.USER
import com.example.myteam.screens.main.StatisticsFragment
import com.example.myteam.screens.chat_list.ChatListFragment
import com.example.myteam.screens.get_tasks.ChoiceContactsForTasksFragment
import com.example.myteam.screens.group_list.AddContactsFragment
import com.example.myteam.screens.group_list.GroupListFragment
import com.example.myteam.screens.task.TaskMainFragment
import com.example.myteam.screens.task.task_receiver.ReceiverTaskFragment
import com.example.myteam.screens.task.task_sender.SenderTaskFragment
import com.example.myteam.utilits.downloadAndSetImage
import com.example.myteam.utilits.replaceFragment
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerImageLoader

//Класс для управления боковым меню NavigationDrawer
class AppDrawer {

    private lateinit var mDrawer: Drawer
    private lateinit var mHeader: AccountHeader
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mCurrentProfile: ProfileDrawerItem  //Обновляем профиль

    //Создание бокового меню
    fun create() {
        initLoader()
        createHeader()  //Шапка меню
        createDrawer()  //Меню
        mDrawerLayout = mDrawer.drawerLayout
    }

    //Отключение выдвигающего меню
    fun disableDrawer() {
        mDrawer.actionBarDrawerToggle?.isDrawerIndicatorEnabled = false
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        APP_ACTIVITY.mToolbar.setNavigationOnClickListener {
            APP_ACTIVITY.supportFragmentManager.popBackStack()
        }
    }

    //Включение выдвигающего меню
    fun enableDrawer() {
        APP_ACTIVITY.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mDrawer.actionBarDrawerToggle?.isDrawerIndicatorEnabled = true
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        APP_ACTIVITY.mToolbar.setNavigationOnClickListener {
            mDrawer.openDrawer()
        }
    }

    //Создание дравера
    private fun createDrawer() {
        mDrawer = DrawerBuilder()
            .withActivity(APP_ACTIVITY)
            .withToolbar(APP_ACTIVITY.mToolbar)
            .withActionBarDrawerToggle(true)
            .withSelectedItem(-1)
            .withAccountHeader(mHeader)
            .addDrawerItems(

                PrimaryDrawerItem().withIdentifier(1)
                    .withIconTintingEnabled(true)
                    .withName("Статистика")
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_analytics),

                PrimaryDrawerItem().withIdentifier(2)
                    .withIconTintingEnabled(true)
                    .withName("Личные сообщения")
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_chats),

                PrimaryDrawerItem().withIdentifier(3)
                    .withIconTintingEnabled(true)
                    .withName("Команда")
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_groups),

                PrimaryDrawerItem().withIdentifier(4)
                    .withIconTintingEnabled(true)
                    .withName("Создать команду")
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_group_add),

                PrimaryDrawerItem().withIdentifier(5)
                    .withIconTintingEnabled(true)
                    .withName("Создать задачу")
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_task_add),

                PrimaryDrawerItem().withIdentifier(6)
                    .withIconTintingEnabled(true)
                    .withName("Задачи")
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_task),

                PrimaryDrawerItem().withIdentifier(7)
                    .withIconTintingEnabled(true)
                    .withName("Контакты")
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_contacts),

                PrimaryDrawerItem().withIdentifier(8)
                    .withIconTintingEnabled(true)
                    .withName("Настройки")
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_settings),

                DividerDrawerItem(),

                PrimaryDrawerItem().withIdentifier(9)
                    .withIconTintingEnabled(true)
                    .withName("Пригласить друзей")
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_person_add),

                PrimaryDrawerItem().withIdentifier(10)
                    .withIconTintingEnabled(true)
                    .withName("Вопросы о приложении")
                    .withSelectable(false)
                    .withIcon(R.drawable.ic_menu_help)

            ).withOnDrawerItemClickListener(object : Drawer.OnDrawerItemClickListener {
                override fun onItemClick(
                    view: View?,
                    position: Int,
                    drawerItem: IDrawerItem<*>
                ): Boolean {
                    clickToItem(position)
                    return false
                }
            }).build()
    }

    private fun clickToItem(position: Int) {
        when (position) {
            1 -> replaceFragment(StatisticsFragment())
            2 -> replaceFragment(ChatListFragment())
            3 -> replaceFragment(GroupListFragment())
            4 -> replaceFragment(AddContactsFragment())
            5 -> replaceFragment(ChoiceContactsForTasksFragment())
            6 -> replaceFragment(TaskMainFragment())
            7 -> replaceFragment(ContactsFragment())
            8 -> replaceFragment(SettingsFragment())
        }
    }

    //Создание хедера
    private fun createHeader() {
        mCurrentProfile = ProfileDrawerItem()   //Текущий пользователь
            .withName(USER.fullname)
            .withEmail(USER.phone)
            .withIcon(USER.photoUrl)
            .withIdentifier(200)
        mHeader = AccountHeaderBuilder()
            .withActivity(APP_ACTIVITY)
            .withHeaderBackground(R.color.mainColor)
            .addProfiles(
                mCurrentProfile).build()
    }

    //Обновление хедера
    fun updateHeader() {
        mCurrentProfile
            .withName(USER.fullname)
            .withEmail(USER.phone)
            .withIcon(USER.photoUrl)
        mHeader.updateProfile(mCurrentProfile)
    }

    //Инициализация лоадера для загрузки картинок в хедер
    private fun initLoader() {
        DrawerImageLoader.init(object : AbstractDrawerImageLoader() {
            override fun set(imageView: ImageView, uri: Uri, placeholder: Drawable) {
                imageView.downloadAndSetImage(uri.toString())
            }
        })
    }
}
