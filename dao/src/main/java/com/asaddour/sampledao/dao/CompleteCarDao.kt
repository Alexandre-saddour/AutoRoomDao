package com.asaddour.sampledao.dao

import com.asaddour.autoroomdao.annotations.AutoDao
import com.asaddour.sampledao.models.CompleteUser

@AutoDao(
        entityClass = CompleteUser::class,
        tableName = "users"
)
// Demonstrate one to many
abstract class CompleteUserDao

