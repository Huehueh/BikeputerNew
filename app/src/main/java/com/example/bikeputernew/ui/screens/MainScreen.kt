package com.example.bikeputernew.ui


import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.bikeputernew.datastore.database.BikeViewModel
import com.example.bikeputernew.ui.screens.MainScreenContent
import com.example.bikeputernew.ui.theme.BikeButerBetterTheme

@Composable
fun MainScreen(
    bikeViewModel: BikeViewModel,
    navigateToSettings: () ->Unit,
    navigateToStats:() -> Unit,
    scanForBlueDevices:(String) -> Unit,
    endTrip: () -> Unit
) {
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
                MainScreenContent(
                    bikeViewModel = bikeViewModel,
                    scanForBlueDevices = scanForBlueDevices,
                    endTrip = endTrip
                )
            }
        )
    }
}