package com.asaddour.autoroomdao

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.asaddour.autoroomdao.dao.Auto_UserDao
import com.asaddour.autoroomdao.models.User

@Database(
        entities = [User::class],
        version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun users(): Auto_UserDao // notice "Auto_UserDao" and not "UserDao"

    companion object {
        lateinit var instance: AppDatabase
            private set

        fun init(context: Context) {
            instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "database_test")
                    .addCallback(object : Callback(){
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            AppDatabase.instance
                                    .users()
                                    .add(
                                            User(name = "Joe", age = 3),
                                            User(name = "William", age = 1),
                                            User(name = "Jack", age = 4),
                                            User(name = "Averell", age = 2)
                                    )
                                    .subscribe()
                        }
                    })
                    .build()
        }
    }

}