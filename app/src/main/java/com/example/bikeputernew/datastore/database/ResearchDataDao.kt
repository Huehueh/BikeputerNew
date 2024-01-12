package com.example.bikeputernew.datastore.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface ResearchDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addData(data : ResearchData)

    @Query("SELECT * FROM research_data_table")
    fun getAllResearchData():List<ResearchData>
}