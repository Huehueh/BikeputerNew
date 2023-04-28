package com.example.bikeputernew

import com.example.bikeputernew.ble.BleDeviceManager
import com.example.bikeputernew.ble.Uuid
import com.example.bikeputernew.datastore.database.BikeViewModel

class MyManager constructor(
    val bikeViewModel: BikeViewModel,
    val bleDeviceManager: BleDeviceManager
){
    fun scanForBlueDevices(name: String) {
        bleDeviceManager.startBleScan(name)
    }

    fun resetTrip() {
        bleDeviceManager.sendBleData(Uuid.RESET)
    }
}