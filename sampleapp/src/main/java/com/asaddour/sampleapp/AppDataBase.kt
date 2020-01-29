package com.asaddour.sampleapp

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.asaddour.sampledao.dao.Auto_CarDao
import com.asaddour.sampledao.dao.Auto_CompleteUserDao
import com.asaddour.sampledao.dao.Auto_UserDao
import com.asaddour.sampledao.models.Car
import com.asaddour.sampledao.models.User

@Database(
        entities = [
            User::class,
            Car::class
        ],
        version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun users(): Auto_UserDao // notice "Auto_UserDao" and not "UserDao"
    abstract fun cars(): Auto_CarDao
    abstract fun completeUsers(): Auto_CompleteUserDao

    companion object {
        lateinit var instance: AppDatabase
            private set

        fun init(context: Context) {
            instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "database_test")
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            instance
                                    .users()
                                    .add(
                                            User(name = "Joe", age = 3),
                                            User(name = "William", age = 1),
                                            User(name = "Jack", age = 4),
                                            User(name = "Averell", age = 2)
                                    )
                                    .flatMap { userIds ->
                                        instance.cars().add(
                                                Car(name = "car1", userId = userIds[0]),
                                                Car(name = "car2", userId = userIds[1]),
                                                Car(name = "car3", userId = userIds[1]),
                                                Car(name = "car4", userId = userIds[2]),
                                                Car(name = "car5", userId = userIds[2]),
                                                Car(name = "car6", userId = userIds[2])
                                        )
                                    }
                                    .subscribe()
                        }
                    })
                    .build()
        }
    }


}
