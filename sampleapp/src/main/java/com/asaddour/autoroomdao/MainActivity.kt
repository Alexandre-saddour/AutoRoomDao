package com.asaddour.autoroomdao

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.asaddour.autoroomdao.models.User
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        Flowable.concat(
                // First time database needs time to fill up.. this is ugly I know..
                Flowable.empty<User>().delay(
                        500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()
                ),
                example1().toFlowable(),
                example2().toFlowable())
                .subscribe()
    }

    //
    // Demonstrate "vaargs"
    //
    fun example1() = AppDatabase.instance
            .users()
            .getByName("Joe", "William")
            .doOnSuccess { users ->
                Log.d("MainActivity", "example1: found ${users.size} users")
                users.forEach { Log.d("MainActivity", "example1:user: $it") }
            }

    //
    // Demonstrate "orderBy" and "limit"
    //
    fun example2() = AppDatabase.instance
            .users()
            .getByRemoteIdOrderedByAge(0, limit = 3)
            .doOnSuccess { users ->
                Log.d("MainActivity", "example2: found ${users.size} users")
                users.forEach { Log.d("MainActivity", "example2:user: $it") }
            }

}