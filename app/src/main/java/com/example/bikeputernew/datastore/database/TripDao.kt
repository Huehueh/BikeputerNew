package com.example.bikeputernew.datastore.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrip(trip : Trip)

    @Delete
    suspend fun deleteTrip(trip: Trip)

    @Update
    suspend fun updateTrip(trip: Trip)

    @Query("SELECT * from trip_table ORDER BY timeStart ASC")
    fun getAllTrips(): Flow<List<Trip>>

    @Query("SELECT * FROM trip_table ORDER BY timeStart DESC LIMIT 1")
    fun getCurrentTrip(): Flow<Trip>

    @Query("DELETE FROM trip_table")
    fun deleteAllTrips()
}