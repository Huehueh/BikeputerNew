package com.example.bikeputernew.datastore.database

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

val TAG = "BikeViewModel"

@HiltViewModel
class BikeViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _allTrips = MutableStateFlow<List<Trip>>(listOf())
    val allTrips: StateFlow<List<Trip>> = _allTrips

    var connected: MutableState<Boolean> = mutableStateOf(false)
    var tripSaved = mutableStateOf(false)
    var tripEnded : Boolean = true //oszustwo bo nie moge zresetowac current trip

    fun getAllTrips() {
        viewModelScope.launch {
            tripRepository.getAllTrips.collect {
                _allTrips.value = it
            }
        }
    }

    suspend fun deleteAllTrips() {
        viewModelScope.launch {
            tripRepository.deleteAllTrips()
        }
    }

    enum class TurnSignal(val value: Int) {
        NO(0),
        LEFT(1),
        RIGHT(2),
        BOTH(3);

        companion object {
            fun fromInt(intValue: Int) = values().first{ it.value == intValue }
        }
    }
    var turnSignal: MutableState<TurnSignal> = mutableStateOf(TurnSignal.NO)

    // current trip values
    var velocity: MutableState<Float> = mutableStateOf(0.0f)

    private val _currentTrip: MutableStateFlow<Trip?> = MutableStateFlow(null)
    val currentTrip: StateFlow<Trip?> = _currentTrip

    fun getCurrentTrip() {
        Log.i(TAG, "getCurrentTrip")
        viewModelScope.launch {
            tripRepository.getCurrentTrip.collect{ trip ->
                _currentTrip.value = trip
            }
        }
    }

    fun loadCurrentTripIfNotFinished() :Boolean {
        if(_allTrips.value.isNotEmpty() && _allTrips.value[_allTrips.value.size-1].timeEnd == 0L)
        {
            getCurrentTrip()
            return true
        }
        return false
    }

    private fun createNewTrip()
    {
        if(!loadCurrentTripIfNotFinished())
        {
            Log.i(TAG, "createNewTrip")
            val newTrip = Trip(System.currentTimeMillis())
            viewModelScope.launch {
                tripRepository.addTrip(newTrip)
            }
            getCurrentTrip()
        }
    }

    fun endTrip() {
        if(_currentTrip.value == null || _currentTrip.value?.distance == 0.0f) {
            // jak wycieczka nie ma dystansu to jej nie zapisujemy
            _currentTrip.value = null
            return
        }

        Log.i(TAG, "koncze trase o dystansie ${_currentTrip.value?.distance?.toInt()}")
        _currentTrip.value?.timeEnd = System.currentTimeMillis()

        viewModelScope.launch {
            currentTrip.value?.let { trip: Trip ->
                tripRepository.updateTrip(trip)
            }
        }

        _currentTrip.value = null
        tripSaved.value = true
        tripEnded = true
    }

    fun setTripAttributes(_distance: Float, _avVelocity: Float, _maxVelocity: Float) {
        if (_currentTrip.value == null || tripEnded)
        {
            Log.i(TAG,"Trying to create new trip")
            createNewTrip()
            tripEnded = false
        }
//        else
//        {
//            Log.d(TAG, "Not creatingnew trip, current: ${_currentTrip.value?.distance}")
//        }

        // workaround for data race
        // TODO change it
        if(_currentTrip.value?.timeEnd != 0L) {
            return
        }

        _currentTrip.value?.distance = _distance
        Log.i(TAG, "update trip: start time: ${_currentTrip.value?.timeStart} distance: ${_currentTrip.value?.distance?.toInt()}")
        _currentTrip.value?.avVelocity = _avVelocity
        _currentTrip.value?.maxVelocity = _maxVelocity
        viewModelScope.launch {
            currentTrip.value?.let { tripRepository.updateTrip(it) }
        }
    }


}