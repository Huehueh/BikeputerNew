package com.example.bikeputernew.ui.screens


import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import com.example.bikeputernew.R
import com.example.bikeputernew.datastore.database.BikeViewModel
import com.example.bikeputernew.datastore.datastore.StoreBikeputerData
import com.example.bikeputernew.ui.theme.BikeButerBetterTheme
import com.example.bikeputernew.values.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val TAG = "SettingsScreen"

@Composable
fun SettingsScreen(){
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = StoreBikeputerData(context = context)

    BikeButerBetterTheme {
        Scaffold(topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        maxLines = 1,
                        color = MaterialTheme.colors.primary
                    )
                },
                backgroundColor = MaterialTheme.colors.background
            )
        },
            content = {
                Column(Modifier.padding(10.dp)) {
                    DeviceNameInput(
                        scope = scope,
                        dataStore = dataStore
                    )

                }
            }
        )
    }
}



@Composable
fun DeviceNameInput(scope: CoroutineScope, dataStore: StoreBikeputerData) {
    var deviceName by remember {
        mutableStateOf("")
    }
    val savedDeviceName by dataStore.getDeviceName.collectAsState(initial = Constants.DEVICE_DEFAULT_NAME)

    LaunchedEffect(key1 = 1) {
        deviceName = savedDeviceName!!
        Log.i(TAG, "on BLE service connected")

    }

    Column(modifier = Modifier.padding(10.dp)) {
        OutlinedTextField(
            value = deviceName,
            onValueChange = { deviceName = it },
            label = { Text(text = stringResource(id = R.string.device_name))}
        )
        Button(onClick = {
            scope.launch {
                dataStore.saveDeviceName(deviceName)
            }
        }) {
            Text("Ustaw")
        }
        Text(text = "")
    }
}
