package com.asaddour.autoroomdao

import android.app.Activity
import android.os.Bundle
import com.asaddour.autoroomdao.models.User

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        test()
    }

    fun test() {
        AppDatabase.instance
                .users()
                .add(User(name = "user1"), User(name = "user2"), User(name = "user3"))
                .subscribe()
    }
}