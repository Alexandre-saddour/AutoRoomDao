package com.asaddour.autoroomdao.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

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