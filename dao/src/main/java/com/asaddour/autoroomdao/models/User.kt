package com.asaddour.autoroomdao.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "users")
data class User(
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,
        @ColumnInfo(name = "remote_id")
        var remoteId: Int = 0,
        var name: String = "",
        @Ignore
        var job: String = ""
)