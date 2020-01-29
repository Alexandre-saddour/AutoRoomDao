package com.asaddour.sampledao.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "cars",
        foreignKeys = [
            ForeignKey(
                    entity = User::class,
                    parentColumns = ["id"],
                    childColumns = ["userId"],
                    onUpdate = ForeignKey.CASCADE,
                    onDelete = ForeignKey.CASCADE)
        ]
)
data class Car(
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,
        var userId: Long,
        var name: String

)