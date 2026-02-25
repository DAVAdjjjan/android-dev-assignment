package com.assignments.lab6.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.assignments.lab6.data.BestScoreStorage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class GameStatus { Idle, Running, Finished }

data class GameState(
    val score: Int = 0,
    val bestScore: Int = 0,
    val timeLeft: Int = GAME_DURATION,
    val status: GameStatus = GameStatus.Idle,
    val targetX: Float = 0f,
    val targetY: Float = 0f
) {
    companion object {
        const val GAME_DURATION = 30
    }
}

class GameViewModel(private val storage: BestScoreStorage) : ViewModel() {

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var fieldWidth: Float = 0f
    private var fieldHeight: Float = 0f

    init {
        viewModelScope.launch {
            val saved = storage.bestScore.first()
            _state.value = _state.value.copy(bestScore = saved)
        }
    }

    fun setFieldSize(width: Float, height: Float) {
        fieldWidth = width
        fieldHeight = height
    }

    fun startGame() {
        timerJob?.cancel()

        _state.value = _state.value.copy(
            score = 0,
            timeLeft = GameState.GAME_DURATION,
            status = GameStatus.Running,
            targetX = randomX(),
            targetY = randomY()
        )

        timerJob = viewModelScope.launch {
            while (_state.value.timeLeft > 0) {
                delay(1000L)
                _state.value = _state.value.copy(timeLeft = _state.value.timeLeft - 1)
            }
            endGame()
        }
    }

    fun onTargetTapped() {
        if (_state.value.status != GameStatus.Running) return

        _state.value = _state.value.copy(
            score = _state.value.score + 1,
            targetX = randomX(),
            targetY = randomY()
        )
    }

    private fun endGame() {
        timerJob?.cancel()
        val current = _state.value
        val newBest = maxOf(current.score, current.bestScore)

        _state.value = current.copy(
            status = GameStatus.Finished,
            bestScore = newBest
        )

        if (newBest > current.bestScore) {
            viewModelScope.launch { storage.saveBestScore(newBest) }
        }
    }

    private fun randomX(): Float {
        val maxX = (fieldWidth - TARGET_SIZE).coerceAtLeast(0f)
        return Random.nextFloat() * maxX
    }

    private fun randomY(): Float {
        val maxY = (fieldHeight - TARGET_SIZE).coerceAtLeast(0f)
        return Random.nextFloat() * maxY
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    companion object {
        const val TARGET_SIZE = 160f

        fun factory(storage: BestScoreStorage): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return GameViewModel(storage) as T
                }
            }
        }
    }
}
