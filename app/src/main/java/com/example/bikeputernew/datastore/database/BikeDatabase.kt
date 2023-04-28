package com.example.bikeputernew.datastore.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Trip::class], version = 1, exportSchema = false)
abstract class BikeDatabase: RoomDatabase() {
    abstract fun tripDao(): TripDao
}