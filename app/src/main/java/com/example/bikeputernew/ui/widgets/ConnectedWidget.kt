package com.example.bikeputernew.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ConnectedWidget(connected: Boolean) {
    val color = if(connected) {
        Color.Green
    } else {
        Color.Red
    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
        .height(15.dp)
        .background(color))
}

@Preview(showBackground = true)
@Composable
fun ConnectedWidgetPreview() {
    ConnectedWidget(connected = true)
}