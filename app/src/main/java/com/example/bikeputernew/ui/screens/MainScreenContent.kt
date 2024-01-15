package com.example.bikeputernew.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.bikeputernew.R
import com.example.bikeputernew.datastore.database.BikeDatabase
import com.example.bikeputernew.datastore.database.BikeViewModel
import com.example.bikeputernew.datastore.datastore.StoreBikeputerData
import com.example.bikeputernew.ui.ConnectedWidget
import com.example.bikeputernew.ui.widgets.CurrentTripWidget
import com.example.bikeputernew.ui.widgets.TurnSignalWidget
import com.example.bikeputernew.ui.widgets.VelocityProgressBar
import com.example.bikeputernew.values.Constants

@Composable
fun MainScreenContent(
    bikeViewModel: BikeViewModel,
    scanForBlueDevices:(String) -> Unit,
    endTrip: () -> Unit,
) {
    val context = LocalContext.current
    val dataStore = StoreBikeputerData(context = context)
    val deviceName by dataStore.getDeviceName.collectAsState(initial = Constants.DEVICE_DEFAULT_NAME)
    var openPopup by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            scanForBlueDevices(deviceName!!)
        }) {
            Text(text = "Search for $deviceName")
        }
        ConnectedWidget(connected = bikeViewModel.connected.value)
        Box(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            TurnSignalWidget(turnSignal = bikeViewModel.turnSignal.value)
        }
        VelocityProgressBar(velocity = bikeViewModel.velocity.value)
        val currentTrip by bikeViewModel.currentTrip.collectAsState()
        CurrentTripWidget(currentTrip = currentTrip)

        Button(
            onClick = {
                openPopup = true
            },
            //        onClick = endTrip,
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = stringResource(id = R.string.end_route),
                fontSize = 15.sp
            )
        }
        if(bikeViewModel.tripSaved.value)
        {
            Toast.makeText(context, stringResource(id = R.string.trip_saved), Toast.LENGTH_SHORT).show()
            bikeViewModel.tripSaved.value = false
        }
        var db = BikeDatabase.getInstance(context)
        Button(onClick = { db.rdToCsvFile() }
        ) {
            Text(text = stringResource(R.string.save_rd))
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val popupWidth = 300.dp
        val popupHeight = 100.dp
        if (openPopup) {
            Popup(
                alignment = Alignment.Center,
                properties = PopupProperties()
            ) {
                Box(modifier = Modifier
                    .size(popupWidth, popupHeight)
                    .padding(5.dp)
                    .background(color = MaterialTheme.colors.secondary)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = stringResource(id = R.string.end_trip_question))
                        Row {
                            Button(
                                onClick = {
                                    endTrip()
                                    openPopup = false
                                }
                            ) {
                                Text(text = stringResource(id = R.string.yes))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Button(
                                onClick = { openPopup = false }
                            ) {
                                Text(text = stringResource(id = R.string.no))
                            }
                        }

                    }
                }
            }

        }
    }
}