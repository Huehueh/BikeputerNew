package com.example.bikeputernew.datastore.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bikeputernew.values.Constants

@Entity(tableName = Constants.RESEARCH_DB_TABLE)
class ResearchData (
    @PrimaryKey val time: Long,
    val data: ByteArray,
) {

}