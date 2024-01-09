package com.example.bikeputernew.datastore.database

import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class TripRepository @Inject constructor(
    private val tripDao: TripDao
    ){
        suspend fun addTrip(trip: Trip) = tripDao.addTrip(trip = trip)
        suspend fun updateTrip(trip: Trip) = tripDao.updateTrip(trip = trip)
        suspend fun deleteTrip(trip: Trip) = tripDao.deleteTrip(trip = trip)
        fun deleteAllTrips() = tripDao.deleteAllTrips()
        val getAllTrips : Flow<List<Trip>> = tripDao.getAllTrips()
        val getCurrentTrip : Flow<Trip> = tripDao.getCurrentTrip()
    }