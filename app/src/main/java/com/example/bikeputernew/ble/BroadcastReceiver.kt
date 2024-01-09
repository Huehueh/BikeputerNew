package com.example.bikeputernew.ble

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.bikeputernew.datastore.database.BikeViewModel
import com.example.bikeputernew.datastore.database.RDViewModel
import java.nio.ByteBuffer
import java.nio.ByteOrder


class BroadcastReceiver(
    val bikeViewModel: BikeViewModel,
    val researchDataViewModel: RDViewModel
) : BroadcastReceiver() {

    private val TAG = "BleReceiver"
    private var connected by bikeViewModel.connected
    private var turnSignal by bikeViewModel.turnSignal
    private var velocity by bikeViewModel.velocity

    private fun processData(data: Bundle) {
        if(data.containsKey(Uuid.TURN_SIGNAL.stringValue))
        {
            val byte: ByteArray = data.get(Uuid.TURN_SIGNAL.stringValue) as ByteArray
            val turnSignalInt = littleEndianConversion(byte)
            turnSignal = BikeViewModel.TurnSignal.fromInt(turnSignalInt)
            Log.d(TAG, "kierunkowskaz $turnSignal")
        }
        else if (data.containsKey(Uuid.MOVEMENT.stringValue))
        {
            Log.i(TAG, "przychodzi nowy pomiar")
            val byte:ByteArray = data.get(Uuid.MOVEMENT.stringValue) as ByteArray
            val values = byteArrayToFloats(byte)
            if (values.size == 4) {
                var distance = values[1]
                if(distance != 0.0f) {
                    velocity = values[0]
                    bikeViewModel.setTripAttributes(distance, values[2], values[3])
                }
            }
            else {
                Log.w(TAG, "unable to parse velocity & distance from BLE message")
            }
        }
        else if (data.containsKey(Uuid.WHEEL_TIMESTAMP.stringValue))
        {
            Log.i(TAG, "Wheel timestamp")
            val researchData = data.get((Uuid.WHEEL_TIMESTAMP.stringValue)) as ByteArray
            researchDataViewModel.addNewResearchData(researchData)
        }
    }


    override fun onReceive(context: Context?, intent: Intent?) {
        with(intent!!) {
            Log.i(TAG, action!!)
            when (action) {
                BluetoothLeService.ACTION_GATT_CONNECTED -> {
                    connected = true
                    Log.i(TAG, "Connected")
                }
                BluetoothLeService.ACTION_GATT_DISCONNECTED -> {
                    connected = false
                    // todo reconnect
                }
                BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED -> {
                    // notifications are enabled by the BluetoothLeService
                    bikeViewModel.loadCurrentTripIfNotFinished()
                }
                BluetoothLeService.ACTION_DATA_AVAILABLE -> {
                    processData(intent.extras!!)
                }
                BluetoothLeService.ACTION_WRITE_SUCCEEDED -> {
                    if (intent.extras!!.containsKey(Uuid.RESET.stringValue)) {
                        Log.d(TAG, "ending trip")
                        bikeViewModel.endTrip()
                    } else {
                        Log.i(TAG, "to nie reset?")
                    }
                }
                else -> {
                    Log.d(TAG, "no action sent via BLE")
                }
            }
        }
    }

    fun getIntentFilter() : IntentFilter {
        val filter = IntentFilter().apply {
            addAction(BluetoothLeService.ACTION_DATA_AVAILABLE)
            addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
            addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)
            addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
            addAction(BluetoothLeService.ACTION_WRITE_SUCCEEDED)
        }
        return filter
    }
}


fun littleEndianConversion(bytes: ByteArray): Int {
    var result = 0
    for (i in bytes.indices) {
        result = result or (bytes[i].toInt() shl 8 * i)
    }
    return result
}

fun byteArrayToDoubles(bytes: ByteArray): ArrayList<Double> {
    val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
    val a1: Double =  buffer.getDouble(0)
    val a2: Double =  buffer.getDouble(8)
    return arrayListOf(a1, a2)
}


fun byteArrayToFloats(bytes: ByteArray): ArrayList<Float> {
    val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
    val a1 =  buffer.getFloat(0)
    val a2 =  buffer.getFloat(4)
    val a3 =  buffer.getFloat(8)
    val a4 =  buffer.getFloat(12)
    return arrayListOf(a1, a2, a3, a4)
}