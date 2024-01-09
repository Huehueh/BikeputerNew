package com.example.bikeputernew.datastore.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface ResearchDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addData(data : ResearchData)
}