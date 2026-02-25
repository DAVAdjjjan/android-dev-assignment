package com.assignments.lab5.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.assignments.lab5.sensor.LevelReading

@Composable
fun SensorStatusCard(reading: LevelReading, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusDot(isActive = reading.sensorAvailable)
                Text(
                    text = if (reading.sensorAvailable) "Accelerometer active"
                    else "Sensor unavailable", style = MaterialTheme.typography.bodyMedium
                )
            }

            val levelColor = if (reading.isLevel) Color(0xFF4CAF50) else Color(0xFFFF5722)
            Text(
                text = if (reading.isLevel) "Level" else "Not level",
                style = MaterialTheme.typography.titleMedium,
                color = levelColor
            )
        }
    }
}

@Composable
private fun StatusDot(isActive: Boolean) {
    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(if (isActive) Color(0xFF4CAF50) else Color(0xFFBDBDBD))
    )
}
