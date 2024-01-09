package com.example.bikeputernew.ble

import android.app.Service
import android.bluetooth.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import java.lang.IllegalArgumentException
import android.bluetooth.BluetoothGattDescriptor
import com.example.bikeputernew.MyManager
import com.example.bikeputernew.datastore.database.BikeViewModel
import java.util.*
import kotlin.NoSuchElementException


class BluetoothLeService : Service() {
    private val TAG: String = "ble_service"
    private var bluetoothGatt: BluetoothGatt? = null
    lateinit var notificationUuids : Queue<Uuid>
    private lateinit var bikeViewModel: BikeViewModel
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var currentAddress: String = ""


    companion object {
        const val ACTION_GATT_CONNECTED = " com.example.bikeputernew.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED =
            " com.example.bikeputernewr.le.ACTION_GATT_DISCONNECTED"
        const val ACTION_GATT_SERVICES_DISCOVERED =
            " com.example.bikeputernew.ACTION_GATT_SERVICES_DISCOVERED"
        const val ACTION_DATA_AVAILABLE = " com.example.bikeputernew.ACTION_DATA_AVAILABLE"
        const val ACTION_WRITE_SUCCEEDED = " com.example.bikeputernew.ACTION_WRITE_SUCCEEDED"
    }
//
//    fun setupBikeViewModel(bikeViewModel: BikeViewModel) {
//        this@BluetoothLeService.bikeViewModel = bikeViewModel
//    }

    fun sendData(uuid: Uuid, value: ByteArray = ByteArray(0)) {
        bluetoothGatt?.services?.forEach { gattService ->
            gattService?.characteristics?.forEach { gattCharacteristic ->
                if(uuid.ToUUID() == gattCharacteristic.uuid) {
                    try {
                        Log.d(TAG, "writeCharacteristic")
                        gattCharacteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                        gattCharacteristic.value = value
                        bluetoothGatt?.writeCharacteristic(gattCharacteristic)
                    } catch (s: SecurityException) {
                        Log.w(TAG, "BLE not accessible")
                    }
                }
            }
        }
    }

    fun readData(uuid: Uuid) {
        bluetoothGatt?.services?.forEach { gattService ->
            gattService?.characteristics?.forEach { gattCharacteristic ->
                if(uuid.ToUUID() == gattCharacteristic.uuid) {
                    try {
                        Log.d(TAG, "readCharacteristic")
                        bluetoothGatt?.readCharacteristic(gattCharacteristic) // result in onCharacteristicChange
                    } catch (s: SecurityException) {
                        Log.w(TAG, "BLE not accessible")
                    }
                }
            }
        }
    }

    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(
            gatt: BluetoothGatt?,
            status: Int,
            newState: Int
        ) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "successfully connected to the GATT Server")
                broadcastUpdate(ACTION_GATT_CONNECTED)
                try {
                    bluetoothGatt?.discoverServices()
                } catch (s: SecurityException)
                {

                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "disconnected from the GATT Server")
                broadcastUpdate(ACTION_GATT_DISCONNECTED)
                //connectToCurrentAddress() // working?
            }
        }

        override fun onServicesDiscovered(
            gatt: BluetoothGatt?,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(TAG, "service discovered")

                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
                notificationUuids = LinkedList(listOf(Uuid.MOVEMENT, Uuid.TURN_SIGNAL, Uuid.WHEEL_TIMESTAMP))
                enableNotifications(bluetoothGatt?.services) // gatt.services?
                // czytam ostatni pomiar
//                readData(Uuid.MOVEMENT)
            }
            else {
                Log.w(TAG, "onServicesDiscovered received: $status")
            }
        }

        private fun enableNotifications(
            gattServices: List<BluetoothGattService?>?
        ) {
            if(gattServices == null) return
            if(notificationUuids.size == 0) return

            val uuid = notificationUuids.remove().ToUUID()
            Log.i(TAG, notificationUuids.toString())
            gattServices.forEach{ gattService ->
                gattService!!.characteristics.forEach{ gattCharacteristic ->
                    try {
                        if(uuid == gattCharacteristic.uuid)
                        {
                            Log.i(TAG, "setting notification for ${gattCharacteristic.uuid.toString()}")
                            enableCharacteristicNotification(gattCharacteristic)
                            // tylko jedna na raz
                            return
                        }
                    } catch (i: NoSuchElementException) {
                        Log.e(TAG, "No such uuid ${i.stackTraceToString()}")
                    }

                }
            }
        }

        fun enableCharacteristicNotification(
            characteristic: BluetoothGattCharacteristic
        ) {
            bluetoothGatt?.let { gatt ->
                try {
                    gatt.setCharacteristicNotification(characteristic, true)

                    val descriptor = characteristic.getDescriptor(Uuid.NOTIFICATION.ToUUID())
                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    gatt.writeDescriptor(descriptor)
                }
                catch (s: SecurityException) {
                    Log.w(TAG, "Bluetooth is not turned on")
                }
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "WRITE SUCCEEDED ${characteristic?.uuid.toString()}")
                broadcastUpdate(ACTION_WRITE_SUCCEEDED, characteristic)
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?
        ) {
            Log.i(TAG, "onCharacteristicChanged")
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt?,
            descriptor: BluetoothGattDescriptor?,
            status: Int
        ) {
            // only when one descriptor is written you can continue turning on notifications on the next descriptor
            if (gatt != null && notificationUuids.isNotEmpty()) {
                enableNotifications(gatt.services)
            }
        }
    }

    fun connectToCurrentAddress() : Boolean {
        Log.i(TAG, "connecting to $currentAddress")
        this@BluetoothLeService.bluetoothAdapter.let { adapter ->
            try {
                val device = adapter.getRemoteDevice(currentAddress)
                bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback)

            } catch (exception: IllegalArgumentException) {
                Log.w(TAG, "Device not found with provided address. Unable to connect.")
                return false
            } catch (s: SecurityException) {
                Log.w(TAG, "Bluetooth is not turned on")
                return false
            }
        }
        return true
    }


    fun connect(bluetoothAdapter: BluetoothAdapter, address: String): Boolean {
        this@BluetoothLeService.bluetoothAdapter = bluetoothAdapter
        currentAddress = address
        return connectToCurrentAddress()
    }

    private val binder = LocalBinder()
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    inner class LocalBinder : Binder() {
        fun getService() : BluetoothLeService {
            return this@BluetoothLeService
        }
    }

    private fun broadcastUpdate(action: String) {
        Log.i(TAG, "broadcastUpdate $action")
        val intent = Intent(action)
        sendBroadcast(intent)
    }

    private fun broadcastUpdate(action: String, characteristic: BluetoothGattCharacteristic?) {
        Log.i(TAG, "broadcastUpdate $action")
        val intent = Intent(action)
        intent.putExtra(characteristic!!.uuid.toString(), characteristic.value)
        sendBroadcast(intent)
    }
}
