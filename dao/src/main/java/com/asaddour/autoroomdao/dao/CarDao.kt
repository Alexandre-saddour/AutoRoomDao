package com.asaddour.autoroomdao.dao

import com.asaddour.autoroomdao.annotations.AutoDao
import com.asaddour.autoroomdao.models.User
import io.reactivex.Maybe

//
// Demonstrate configuration
// You can check the readme for more information.
//
@AutoDao(
        entityClass = User::class,
        defaultRxReturnType = Maybe::class,
        generateOnlyDefaultRxReturnType = true,
        generateOrderBy = false
)
abstract class CarDao