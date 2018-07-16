package com.asaddour.autoroomdao.dao

import com.asaddour.autoroomdao.annotations.AutoDao
import com.asaddour.autoroomdao.models.User

@AutoDao(User::class)
abstract class UserDao
