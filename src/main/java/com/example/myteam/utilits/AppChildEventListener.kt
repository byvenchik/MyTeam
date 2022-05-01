package com.example.myteam.utilits

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

//Для прослушивания БД
//Разовый, при входе
class AppChildEventListener(val onSuccess: (DataSnapshot) -> Unit) : ChildEventListener {

    //Он отрабатывает тогда, когда добавляем в ноду новый элемент
    override fun onChildAdded(p0: DataSnapshot, previousChildName: String?) {
        onSuccess(p0)
    }

    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        //TODO("Not yet implemented")
    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
        //TODO("Not yet implemented")
    }

    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        //TODO("Not yet implemented")
    }

    override fun onCancelled(error: DatabaseError) {
        //TODO("Not yet implemented")
    }

}
