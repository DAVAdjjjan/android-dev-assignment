package com.assignments.lab5.sensor

data class LevelReading(
    val angleX: Float = 0f,
    val angleY: Float = 0f,
    val isLevel: Boolean = false,
    val sensorAvailable: Boolean = false
)
