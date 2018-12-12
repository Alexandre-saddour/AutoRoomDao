package com.asaddour.autoroomdao.dao

import androidx.room.Query
import com.asaddour.autoroomdao.annotations.AutoDao
import com.asaddour.autoroomdao.models.Car
import io.reactivex.Single

//
// Demonstrate configuration
// You can check the readme for more information.
//
@AutoDao(
        entityClass = Car::class,
        defaultRxReturnType = Single::class,
        generateOnlyDefaultRxReturnType = true,
        generateOrderBy = false
)
abstract class CarDao {

    //
    // Demonstrate that you can still write your own queries
    //
    @Query("SELECT name from cars")
    abstract fun getAllNames(): Single<List<String>>
}