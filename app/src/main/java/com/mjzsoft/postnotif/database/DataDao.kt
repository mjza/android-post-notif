package com.mjzsoft.postnotif.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: DataModel)

    @Query("SELECT * FROM data_table")
    suspend fun getAllData(): List<DataModel>

    @Query("DELETE FROM data_table")
    suspend fun deleteAll()
}
