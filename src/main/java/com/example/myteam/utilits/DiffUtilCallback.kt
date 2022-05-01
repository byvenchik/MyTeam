package com.example.myteam.utilits

import androidx.recyclerview.widget.DiffUtil
import com.example.myteam.models.CommonModel

class DiffUtilCallback(
    private val oldList: List<CommonModel>,  //Старый лист
    private val newList: List<CommonModel>  //Новый лист
) : DiffUtil.Callback() {

    //Передать размер старого листа
    override fun getOldListSize(): Int = oldList.size

    //Передать размер нового листа
    override fun getNewListSize(): Int = newList.size

    //Проверка по идентификатору, если одинаковые id, то надо проверить значения
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        //id не было, привязались к времени сервера
        oldList[oldItemPosition].timeStamp == newList[newItemPosition].timeStamp

    //Проверка по значению, отрабатывает тогда, когда одинаковые id
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        //id не было, привязались к времени сервера
        oldList[oldItemPosition] == newList[newItemPosition]
}