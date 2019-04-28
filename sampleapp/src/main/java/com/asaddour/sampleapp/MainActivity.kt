package com.asaddour.sampleapp

import android.app.Activity
import android.os.Bundle
import android.util.Log
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        Completable
                .fromAction {}
                // First time database needs time to fill up.. this is ugly I know..
                .delay(3000, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .andThen(Flowable.concat(
                        example1().toFlowable(),
                        example2().toFlowable(),
                        example3().toFlowable(),
                        example4().toFlowable())
                )
                .subscribe()

    }

    //
    // Demonstrate "vaargs"
    //
    private fun example1() = AppDatabase.instance
            .users()
            .getByName("Joe", "William")
            .doOnSuccess { users ->
                Log.d("MainActivity", "example1: found ${users.size} users")
                users.forEach { Log.d("MainActivity", "example1:user: $it") }
            }

    //
    // Demonstrate "orderBy" and "limit"
    //
    private fun example2() = AppDatabase.instance
            .users()
            .getByRemoteIdOrderedByAge(0, limit = 3)
            .doOnSuccess { users ->
                Log.d("MainActivity", "example2: found ${users.size} users")
                users.forEach { Log.d("MainActivity", "example2:user: $it") }
            }


    //
    // Demonstrate "@ForeignKeys" -> getting car by user id
    //
    private fun example3() = AppDatabase.instance
            .users()
            .getAll()
            .flatMap { usersId ->
                AppDatabase.instance
                        .cars()
                        .getByUserId(*usersId.map { it.id }.toLongArray())
            }
            .doOnSuccess { cars ->
                Log.d("MainActivity", "example3: found ${cars.size} cars")
                cars.forEach { Log.d("MainActivity", "example3:car: $it") }
            }

    //
    // Demonstrate that you can still write your own queries
    //
    private fun example4() = AppDatabase.instance
            .cars()
            .getAllNames()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { names ->
                Log.d("MainActivity", "example4: found ${names.size} cars")
                names.forEach { Log.d("MainActivity", "example4:name: $it") }
            }
}