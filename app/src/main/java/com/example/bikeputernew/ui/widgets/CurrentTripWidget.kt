package com.example.bikeputernew.ui.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.bikeputernew.R
import com.example.bikeputernew.datastore.database.Trip

@Composable
fun CurrentTripWidget(currentTrip: Trip?) {
    currentTrip?.let {
        Column {
            Text(text = "${stringResource(id = R.string.distance)} ${String.format("%.1f", currentTrip.distance)} m")
            Text(text = "${stringResource(id = R.string.max_velocity)}  ${String.format("%.1f", currentTrip.maxVelocity)} km/h")
            Text(text = "${stringResource(id = R.string.av_velocity)}  ${String.format("%.1f", currentTrip.avVelocity)} km/h")
        }
    }
}

@Preview
@Composable
fun CurrentTripWidgetPreview() {
    CurrentTripWidget(currentTrip = Trip(903840124707))
}