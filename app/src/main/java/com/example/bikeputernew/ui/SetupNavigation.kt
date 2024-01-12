package com.example.bikeputernew.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bikeputernew.MyManager
import com.example.bikeputernew.datastore.database.BikeViewModel
import com.example.bikeputernew.ui.screens.SettingsScreen
import com.example.bikeputernew.ui.screens.StatScreen

val MAIN_SCREEN = "Main Screen"
val SETTINGS_SCREEN = "Settings"
val STATISTICS_SCREEN = "Statistics"

@Composable
fun SetupNavigation(
    navController: NavHostController,
    bikeViewModel: BikeViewModel,
    myManager: MyManager
) {
    NavHost(
        navController = navController,
        startDestination = MAIN_SCREEN) {
        composable(MAIN_SCREEN) {
            MainScreen(bikeViewModel = bikeViewModel,
//                bleDeviceManager::startBleScan, // tymczasowo
//                bleDeviceManager::stopBleScan, // tymczasowo
                navigateToSettings = { navController.navigate(SETTINGS_SCREEN) },
                navigateToStats =  { navController.navigate(STATISTICS_SCREEN) },

                scanForBlueDevices = myManager::scanForBlueDevices,
                endTrip = myManager::resetTrip
            )
        }
        composable(SETTINGS_SCREEN) {
            SettingsScreen()
        }
        composable(STATISTICS_SCREEN) {
            StatScreen(bikeViewModel = bikeViewModel)
        }

    }

}