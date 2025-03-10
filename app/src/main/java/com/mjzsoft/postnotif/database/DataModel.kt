package com.mjzsoft.postnotif.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data_table")
data class DataModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val column1: String,
    val column2: String,
    val column3: String
)
