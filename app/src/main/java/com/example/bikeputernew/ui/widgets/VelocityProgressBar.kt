package com.example.bikeputernew.ui.widgets

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bikeputernew.ui.theme.*

val radius = 100.dp

@Composable
fun VelocityProgressBar(velocity: Float) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(radius * 3f)
    ) {
        Canvas(
            modifier = Modifier.size(radius * 2f)
        ) {
            drawArc(
                color = velocityColor(velocity),
                startAngle = -270f,
                sweepAngle = (36f * velocity.mod(10f)),
                useCenter = false,
                style = Stroke(width = 60f, cap = StrokeCap.Square)
            )
            drawArc(
                color = previousVelocityColor(velocity),
                startAngle = (- 270f + 36f * velocity.mod(10f)),
                sweepAngle = (360f - 36f * velocity.mod(10f)),
                useCenter = false,
                style = Stroke(width = 60f, cap = StrokeCap.Square)
            )
        }
        Text(
            text = String.format("%.1f", velocity) + " km/h",
            color = MaterialTheme.colors.primary,
            fontSize = 30.sp,
            maxLines = 1
        )
    }
}

fun velocityColor(velocity: Float): Color {
    return when (velocity) {
        in 0.0..10.0 -> Velocity1
        in 10.0..20.0 -> Velocity2
        in 20.0..30.0 -> Velocity3
        else -> Velocity4
    }
}

fun previousVelocityColor(velocity: Float): Color {
    val previous = velocity - 10f
    if (previous < 0f) {
        return Color.Transparent
    }
    return velocityColor(previous)
}

@Preview
@Composable
fun VelocityProgressBarPreview() {
    VelocityProgressBar(
        velocity = 5.0f
    )
}