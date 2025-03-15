package com.mjzsoft.postnotif.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data_table")
data class DataModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val unitNumber: String,
    val email: String,
    val telephone: String
)
