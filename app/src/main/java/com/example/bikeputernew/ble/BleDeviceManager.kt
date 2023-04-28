package com.example.bikeputernew.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.example.bikeputernew.datastore.database.BikeViewModel

// scanning BLE devices & connecting to them

class BleDeviceManager(
    val bluetoothAdapter: BluetoothAdapter,
    private val bikeViewModel: BikeViewModel) {
    private val TAG = "BleDeviceManager"
    val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private var bluetoothLeService: BluetoothLeService? = null
    private val scanHandler = Handler(Looper.getMainLooper())
    private val SCAN_PERIOD: Long = 100000
    private var scanning = false
    private var bikeputerDeviceName:String = ""

    private val serviceConnection = object : ServiceConnection {
        var bound = false
        // Called when the connection with the service is established
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Log.i(TAG, "on BLE service connected")
            val binder = service as BluetoothLeService.LocalBinder
            bluetoothLeService = binder.getService()
            bound = true
        }

        // Called when the connection with the service disconnects unexpectedly
        override fun onServiceDisconnected(className: ComponentName) {
            Log.e(TAG, "onServiceDisconnected")
            bound = false
        }
    }

    val bleScanCallback = object: ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)

            try {
                with(result!!.device) {
                    Log.i(TAG, name ?: "no name")
                    if (name == bikeputerDeviceName) {
                        stopBleScan()
                        Log.i(TAG,"Connecting to ${name} address  ${this.address}");
                        Log.i(TAG,"bluetoothLeService ${bluetoothLeService}");
                        bluetoothLeService?.connect(bluetoothAdapter, this.address)
                    }
                }
            } catch (s: SecurityException) {
                Log.w(TAG, "Bluetooth inaccessible: No callback")
            }
        }
    }

    fun startBleService(context: Context): Boolean {
        if (!bluetoothAdapter.isEnabled)
            return false
        Log.i(TAG, "startBleService")
        Intent(context, BluetoothLeService::class.java).also {
            Log.i(TAG, "intent starting")
            context.bindService(it, serviceConnection, Context.BIND_AUTO_CREATE)
            return true
        }
    }

    fun stopBleService(context: Context)
    {
        if (serviceConnection.bound)
        {
            context.unbindService(serviceConnection)
        }
    }

    fun startBleScan(name: String) {
        if(bikeViewModel.connected.value) {
            return
        }
        bikeputerDeviceName = name
        Log.i(TAG, "starting Ble scan for $bikeputerDeviceName")
        if(scanning) {
            stopBleScan()
        }
        scanHandler.postDelayed({stopBleScan()}, SCAN_PERIOD)
        try {
            bleScanner.startScan(null, ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build(), bleScanCallback)
            scanning = true
        } catch (s: SecurityException) {
            Log.w(TAG, "Bluetooth inaccessible: Cannot start scanning  ${s.stackTraceToString()}")
        }

    }

    fun stopBleScan() {
        try {
            bleScanner.stopScan(bleScanCallback)
            scanning = false
        } catch (s: SecurityException) {
            Log.w(TAG, "Bluetooth inaccessible: Cannot stop ble scan")
        }
    }

    fun sendBleData(uuid: Uuid, data: ByteArray = ByteArray(0)) {
        bluetoothLeService?.sendData(uuid, data)
    }


}