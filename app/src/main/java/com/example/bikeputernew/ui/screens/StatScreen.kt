package com.example.bikeputernew.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.bikeputernew.R
import com.example.bikeputernew.datastore.database.BikeViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.bytebeats.views.charts.bar.BarChart
import me.bytebeats.views.charts.bar.BarChartData
import me.bytebeats.views.charts.bar.render.bar.SimpleBarDrawer
import me.bytebeats.views.charts.bar.render.label.SimpleLabelDrawer
import me.bytebeats.views.charts.simpleChartAnimation
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatScreen(bikeViewModel: BikeViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.statistics),
                        maxLines = 1,
                        color = MaterialTheme.colors.primary
                    )
                },
                backgroundColor = MaterialTheme.colors.background
            )
        }) 
    {
        val scope = rememberCoroutineScope()
        LaunchedEffect(key1 = true) {
            bikeViewModel.getAllTrips()
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val distances = distanceData(bikeViewModel = bikeViewModel)
            if(distances.isEmpty()) {
                Text(text = "NO DATA")
            }
            else {
//                Button(onClick = {
//                    scope.launch {
//                        bikeViewModel.deleteAllTrips()
//                    }
//                }) {
//                    Text(text = "Usun wszystkie dane")
//                }
                BarChart(
                    barChartData = BarChartData(
                        bars = distances
                    ),
                    modifier = Modifier.padding(20.dp),
                    animation = simpleChartAnimation(),
                    barDrawer = SimpleBarDrawer(),
                    labelDrawer = SimpleLabelDrawer(
                        drawLocation = SimpleLabelDrawer.DrawLocation.Outside,
                        labelTextColor = MaterialTheme.colors.primary
                    )
                )
            }

        }
    }
}

fun distanceToDisplay(distance: Float) : String {
    val distanceInt = distance.toInt()
    return if(distanceInt < 1000) {
        "$distanceInt m"
    }
    else {
        val distKm = distance/1000
        "${String.format("%.1f", distKm)} km"
    }
}

@Composable
fun distanceData(bikeViewModel: BikeViewModel) : List<BarChartData.Bar> {
    val bars : MutableList<BarChartData.Bar> = mutableListOf()
    val trips = bikeViewModel.allTrips.collectAsState().value
    for(trip in trips) {
        bars.add(element = BarChartData.Bar(
            label = distanceToDisplay(trip.distance),
            value =  trip.distance,
            color = if (trip.timeEnd != 0L) Color.Blue else Color.Red)
        )
    }
    return bars
}

private fun getDateTime(time: Long): String {
    return try {
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        val netDate = Date(time)
        sdf.format(netDate)
    } catch (e: Exception) {
        e.toString()
    }
}