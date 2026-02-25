package com.assignments.lab5.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.assignments.lab5.sensor.LevelReading
import com.assignments.lab5.sensor.LevelSensorManager
import kotlinx.coroutines.flow.StateFlow

class LevelViewModel(application: Application) : AndroidViewModel(application) {

    private val sensorManager = LevelSensorManager(application)

    val reading: StateFlow<LevelReading> = sensorManager.reading

    fun startListening() = sensorManager.start()

    fun stopListening() = sensorManager.stop()

    fun calibrate() = sensorManager.calibrate()

    fun resetCalibration() = sensorManager.resetCalibration()

    override fun onCleared() {
        super.onCleared()
        sensorManager.stop()
    }
}
