@file:Suppress("DEPRECATION")

package com.example.bikeputernew

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.bikeputernew.ble.BleDeviceManager
import com.example.bikeputernew.ble.BroadcastReceiver
import com.example.bikeputernew.datastore.database.BikeViewModel
import com.example.bikeputernew.datastore.database.RDViewModel
import com.example.bikeputernew.ui.SetupNavigation
import com.example.bikeputernew.ui.theme.BikeButerBetterTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val TAG = "BikePuterActivity"
    private lateinit var navController: NavHostController
    private val bikeViewModel: BikeViewModel by viewModels()
    private val researchDataViewModel: RDViewModel by viewModels()
    private val broadcastReceiver by lazy {
        BroadcastReceiver(bikeViewModel = bikeViewModel, researchDataViewModel = researchDataViewModel)
    }

    // BLE
    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }
    private val bleDeviceManager: BleDeviceManager by lazy {
         BleDeviceManager(
             bluetoothAdapter = bluetoothAdapter,
             bikeViewModel = bikeViewModel
         )
    }
    private val myManager: MyManager by lazy {
        MyManager(bikeViewModel = bikeViewModel, bleDeviceManager = bleDeviceManager)
    }

//    // BLE permissions
//    private fun Context.hasPermission(permissionType: String): Boolean {
//        return ContextCompat.checkSelfPermission(this, permissionType) ==
//                PackageManager.PERMISSION_GRANTED
//    }


    // enabling BLE in device
//    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == RESULT_OK) {
//           bleDeviceManager.startBleService(this)
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        registerReceiver(broadcastReceiver, broadcastReceiver.getIntentFilter())

        setContent{
            BikeButerBetterTheme {
                navController = rememberNavController()
                SetupNavigation(
                    navController = navController,
                    bikeViewModel = bikeViewModel,
                    myManager = myManager
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
        bleDeviceManager.stopBleService(this)
    }

    private val PERMISSIONS_LOCATION = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_PRIVILEGED
    )


    @RequiresApi(Build.VERSION_CODES.S)
    fun ObtainAllBlePermissions() {
        Log.i(TAG, "ObtainAllBlePermissions");
        val permissions =
            arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)

        for (permission in permissions) {
            Log.i(TAG, "checkSelfPermission");
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION, 1)
                return
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onStart() {
        super.onStart()
        Log.i(TAG, "OnStart")
        ObtainAllBlePermissions()

        if (!bluetoothAdapter.isEnabled)
        {
            // prompt enable bluetooth
            val enableBltIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//            resultLauncher.launch(enableBltIntent)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_LOCATION, 1)
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            startActivityForResult(enableBltIntent, 1)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1)
        {
            bleDeviceManager.startBleService(this)
        }
    }

}