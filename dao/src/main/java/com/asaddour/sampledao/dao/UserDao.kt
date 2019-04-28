package com.asaddour.sampledao.dao

import com.asaddour.autoroomdao.annotations.AutoDao
import com.asaddour.sampledao.models.User

@AutoDao(User::class)
abstract class UserDao
