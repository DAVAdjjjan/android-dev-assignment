package com.assignments.lab5.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.assignments.lab5.sensor.LevelReading
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun LevelCanvas(reading: LevelReading, modifier: Modifier = Modifier) {
    val levelColor = if (reading.isLevel) Color(0xFF4CAF50) else Color(0xFFFF5722)
    val bubbleColor = if (reading.isLevel) Color(0xFF66BB6A) else Color(0xFFEF5350)
    val outlineColor = MaterialTheme.colorScheme.outline

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(16.dp)
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = min(size.width, size.height) / 2 * 0.9f

        drawCircle(
            color = outlineColor,
            radius = radius,
            center = Offset(centerX, centerY),
            style = Stroke(width = 3.dp.toPx())
        )

        drawCircle(
            color = outlineColor.copy(alpha = 0.2f),
            radius = radius * 0.33f,
            center = Offset(centerX, centerY),
            style = Stroke(width = 1.dp.toPx())
        )
        drawCircle(
            color = outlineColor.copy(alpha = 0.2f),
            radius = radius * 0.66f,
            center = Offset(centerX, centerY),
            style = Stroke(width = 1.dp.toPx())
        )

        drawLine(
            color = outlineColor.copy(alpha = 0.2f),
            start = Offset(centerX - radius, centerY),
            end = Offset(centerX + radius, centerY),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = outlineColor.copy(alpha = 0.2f),
            start = Offset(centerX, centerY - radius),
            end = Offset(centerX, centerY + radius),
            strokeWidth = 1.dp.toPx()
        )

        val tiltRad = reading.angleX * (PI.toFloat() / 180f)
        val lineLen = radius * 0.95f
        drawLine(
            color = levelColor.copy(alpha = 0.5f), start = Offset(
                centerX - cos(tiltRad) * lineLen, centerY + sin(tiltRad) * lineLen
            ), end = Offset(
                centerX + cos(tiltRad) * lineLen, centerY - sin(tiltRad) * lineLen
            ), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round
        )

        drawCircle(
            color = levelColor, radius = 5.dp.toPx(), center = Offset(centerX, centerY)
        )

        val maxOffset = radius * 0.8f
        val bubbleRawX = (-reading.angleX / 45f).coerceIn(-1f, 1f) * maxOffset
        val bubbleRawY = (-reading.angleY / 45f).coerceIn(-1f, 1f) * maxOffset

        val dist = sqrt(bubbleRawX * bubbleRawX + bubbleRawY * bubbleRawY)
        val clampedX: Float
        val clampedY: Float
        if (dist > maxOffset) {
            clampedX = bubbleRawX / dist * maxOffset
            clampedY = bubbleRawY / dist * maxOffset
        } else {
            clampedX = bubbleRawX
            clampedY = bubbleRawY
        }

        val bubbleCenter = Offset(centerX + clampedX, centerY + clampedY)

        drawCircle(
            color = bubbleColor.copy(alpha = 0.25f), radius = 24.dp.toPx(), center = bubbleCenter
        )
        drawCircle(
            color = bubbleColor, radius = 20.dp.toPx(), center = bubbleCenter
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.4f),
            radius = 8.dp.toPx(),
            center = Offset(bubbleCenter.x - 5.dp.toPx(), bubbleCenter.y - 5.dp.toPx())
        )
    }
}
