package com.assignments.lab5.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt

class LevelSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _reading = MutableStateFlow(
        LevelReading(sensorAvailable = accelerometer != null)
    )
    val reading: StateFlow<LevelReading> = _reading.asStateFlow()

    private var filteredX = 0f
    private var filteredY = 0f
    private var filteredZ = 0f
    private var filterInitialized = false

    private var offsetX = 0f
    private var offsetY = 0f

    companion object {
        private const val ALPHA = 0.15f
        private const val LEVEL_THRESHOLD = 2f
    }

    fun start() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    fun calibrate() {
        val current = _reading.value
        offsetX += current.angleX
        offsetY += current.angleY
    }

    fun resetCalibration() {
        offsetX = 0f
        offsetY = 0f
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val rawX = event.values[0]
        val rawY = event.values[1]
        val rawZ = event.values[2]

        if (!filterInitialized) {
            filteredX = rawX
            filteredY = rawY
            filteredZ = rawZ
            filterInitialized = true
        } else {
            filteredX = ALPHA * rawX + (1 - ALPHA) * filteredX
            filteredY = ALPHA * rawY + (1 - ALPHA) * filteredY
            filteredZ = ALPHA * rawZ + (1 - ALPHA) * filteredZ
        }

        val angleX = atan2(
            filteredX.toDouble(), sqrt((filteredY * filteredY + filteredZ * filteredZ).toDouble())
        ).toFloat() * (180f / PI.toFloat()) - offsetX

        val angleY = atan2(
            filteredY.toDouble(), sqrt((filteredX * filteredX + filteredZ * filteredZ).toDouble())
        ).toFloat() * (180f / PI.toFloat()) - offsetY

        _reading.value = LevelReading(
            angleX = angleX,
            angleY = angleY,
            isLevel = abs(angleX) < LEVEL_THRESHOLD && abs(angleY) < LEVEL_THRESHOLD,
            sensorAvailable = true
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
