package com.asaddour.sampledao.models

import androidx.room.Embedded
import androidx.room.Relation

data class CompleteUser(
        @Embedded val user: User,
        @Relation(
                parentColumn = "id",
                entityColumn = "userId"
        )
        val cars: List<Car>
)