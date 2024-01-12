package com.example.bikeputernew.ui


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.bikeputernew.R
import com.example.bikeputernew.datastore.database.BikeDatabase
import com.example.bikeputernew.datastore.database.BikeViewModel
import com.example.bikeputernew.datastore.database.RDViewModel
import com.example.bikeputernew.datastore.datastore.StoreBikeputerData
import com.example.bikeputernew.ui.theme.BikeButerBetterTheme
import com.example.bikeputernew.ui.widgets.CurrentTripWidget
import com.example.bikeputernew.ui.widgets.EndTripButton
import com.example.bikeputernew.ui.widgets.TurnSignalWidget
import com.example.bikeputernew.ui.widgets.VelocityProgressBar
import com.example.bikeputernew.values.Constants

@Composable
fun MainScreen(
    bikeViewModel: BikeViewModel,
    navigateToSettings: () ->Unit,
    navigateToStats:() -> Unit,
    scanForBlueDevices:(String) -> Unit,
    endTrip: () -> Unit
) {
    val context = LocalContext.current
    val dataStore = StoreBikeputerData(context = context)
    val deviceName by dataStore.getDeviceName.collectAsState(initial = Constants.DEVICE_DEFAULT_NAME)
    LaunchedEffect(key1 = true) {
        bikeViewModel.getAllTrips()
    }
    BikeButerBetterTheme {
        Scaffold(
            topBar = {
                MainAppBar(
                    onSettingsClicked = navigateToSettings,
                    onStatsClicked = navigateToStats
                )
            },
            content = {
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
                    EndTripButton(endTrip = endTrip)
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
            }
        )
    }
}