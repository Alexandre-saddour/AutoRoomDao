package com.asaddour.autoroomdao.dao

import com.asaddour.autoroomdao.annotations.AutoDao
import com.asaddour.autoroomdao.models.User
import io.reactivex.Flowable

@AutoDao(User::class,
        defaultRxReturnType = Flowable::class,
        generateOrderBy = true)
abstract class UserDao {
//    @Query("SELECT * FROM users WHERE name = :arg0")
//    protected abstract fun getByName_(name: String): Single<List<User>>

//    fun getByName(name: String): Single<List<User>> {
//        return getByName_(name).subscribeOn(Schedulers.io())
//    }


//    @Query("SELECT * FROM users WHERE remoteId=:arg0 LIMIT :arg1")
//    protected abstract fun getByRemoteIdLimit_(remoteId: Int, limit: Int): Single<List<User>>
//
//    fun getByRemoteIdLimit(remoteId: Int, limit: Int): Single<List<User>> {
//        return when (limit){
//            -1 -> getByRemoteIdLimit_(remoteId, limit)
//            else -> getByRemoteIdLimit_(remoteId, limit)
//        }.subscribeOn(Schedulers.io())
//    }
}
