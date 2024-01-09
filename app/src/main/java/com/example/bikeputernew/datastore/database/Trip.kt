package com.example.bikeputernew.datastore.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bikeputernew.values.Constants

@Entity(tableName = Constants.TRIP_DB_TABLE)
data class Trip (
    @PrimaryKey val timeStart: Long,
    var timeEnd: Long = 0,
    var distance: Float = 0.0f,
    var avVelocity: Float = 0.0f,
    var maxVelocity: Float  = 0.0f,
) {
}
