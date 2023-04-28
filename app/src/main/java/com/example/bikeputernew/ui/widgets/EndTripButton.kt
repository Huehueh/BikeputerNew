package com.example.bikeputernew.ui.widgets

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bikeputernew.R

@Composable
fun EndTripButton(endTrip: ()->Unit) {
    Button(
        onClick = endTrip,
        modifier = Modifier.padding(10.dp)
    ) {
        Text(
            text = stringResource(id = R.string.end_route),
            fontSize = 15.sp
        )
    }
}

@Preview
@Composable
fun EndTripButtonPreview() {
    EndTripButton(endTrip = {})
}