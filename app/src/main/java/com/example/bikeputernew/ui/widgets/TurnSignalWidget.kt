package com.example.bikeputernew.ui.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bikeputernew.datastore.database.BikeViewModel.TurnSignal
import com.example.bikeputernew.R


fun showLeft(turnSignal: TurnSignal):Boolean {
    return (turnSignal == TurnSignal.LEFT || turnSignal == TurnSignal.BOTH)
}

fun showRight(turnSignal: TurnSignal):Boolean {
    return (turnSignal == TurnSignal.RIGHT || turnSignal == TurnSignal.BOTH)
}

@Composable
fun TurnSignalWidget(turnSignal: TurnSignal) {
    val leftResource: Painter = painterResource(id =
        if (showLeft(turnSignal))
            R.drawable.turn_signal_left_on
        else
            R.drawable.turn_signal_left_off
    )
    val rightResource: Painter = painterResource(id =
        if (showRight(turnSignal))
            R.drawable.turn_signal_right_on
        else
            R.drawable.turn_signal_right_off
    )

    Row(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = leftResource,
            contentDescription = null,
            alignment = Alignment.CenterStart
        )
        Image(
            painter = rightResource,
            contentDescription = null,
            alignment = Alignment.CenterEnd
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TurnSignalWidgetPreview() {
    TurnSignalWidget(turnSignal = TurnSignal.BOTH)
}