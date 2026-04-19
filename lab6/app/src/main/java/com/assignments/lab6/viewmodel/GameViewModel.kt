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

enum class TargetType { Normal, Bonus }

data class ScorePopup(
    val id: Int,
    val x: Float,
    val y: Float,
    val text: String,
    val isBonus: Boolean = false
)

data class GameState(
    val score: Int = 0,
    val bestScore: Int = 0,
    val timeLeft: Int = GAME_DURATION,
    val status: GameStatus = GameStatus.Idle,
    val targetX: Float = 0f,
    val targetY: Float = 0f,
    val targetSize: Float = MAX_TARGET_SIZE,
    val targetKey: Int = 0,
    val targetType: TargetType = TargetType.Normal,
    val combo: Int = 0,
    val totalTaps: Int = 0,
    val bestCombo: Int = 0,
    val isNewRecord: Boolean = false,
    val popups: List<ScorePopup> = emptyList()
) {
    companion object {
        const val GAME_DURATION = 30
        const val MAX_TARGET_SIZE = 160f
        const val MIN_TARGET_SIZE = 60f
        const val BONUS_CHANCE = 0.2f
        const val COMBO_WINDOW_MS = 1500L
    }
}

class GameViewModel(private val storage: BestScoreStorage) : ViewModel() {

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var comboJob: Job? = null
    private var fieldWidth: Float = 0f
    private var fieldHeight: Float = 0f
    private var popupIdCounter: Int = 0

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

    private fun targetSizeForScore(score: Int): Float {
        val shrinkPerTap = (GameState.MAX_TARGET_SIZE - GameState.MIN_TARGET_SIZE) / 30f
        return (GameState.MAX_TARGET_SIZE - score * shrinkPerTap)
            .coerceIn(GameState.MIN_TARGET_SIZE, GameState.MAX_TARGET_SIZE)
    }

    private fun nextTargetType(): TargetType {
        return if (Random.nextFloat() < GameState.BONUS_CHANCE) TargetType.Bonus else TargetType.Normal
    }

    fun startGame() {
        timerJob?.cancel()
        comboJob?.cancel()
        popupIdCounter = 0

        val size = targetSizeForScore(0)
        _state.value = _state.value.copy(
            score = 0,
            timeLeft = GameState.GAME_DURATION,
            status = GameStatus.Running,
            targetSize = size,
            targetKey = 0,
            targetType = TargetType.Normal,
            targetX = randomX(size),
            targetY = randomY(size),
            combo = 0,
            totalTaps = 0,
            bestCombo = 0,
            isNewRecord = false,
            popups = emptyList()
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

        val current = _state.value
        val newCombo = current.combo + 1
        val comboMultiplier = if (newCombo >= 3) newCombo / 2 else 1
        val basePoints = if (current.targetType == TargetType.Bonus) 3 else 1
        val points = basePoints * comboMultiplier

        val popupText = buildString {
            append("+$points")
            if (current.targetType == TargetType.Bonus) append(" BONUS")
            if (newCombo >= 3) append(" x$comboMultiplier")
        }

        val popup = ScorePopup(
            id = popupIdCounter++,
            x = current.targetX + current.targetSize / 2f,
            y = current.targetY,
            text = popupText,
            isBonus = current.targetType == TargetType.Bonus || newCombo >= 3
        )

        val newScore = current.score + points
        val newSize = targetSizeForScore(newScore)
        val newBestCombo = maxOf(current.bestCombo, newCombo)

        _state.value = current.copy(
            score = newScore,
            targetSize = newSize,
            targetKey = current.targetKey + 1,
            targetType = nextTargetType(),
            targetX = randomX(newSize),
            targetY = randomY(newSize),
            combo = newCombo,
            totalTaps = current.totalTaps + 1,
            bestCombo = newBestCombo,
            popups = current.popups + popup
        )

        resetComboTimer()
        schedulePopupRemoval(popup.id)
    }

    fun removePopup(id: Int) {
        val current = _state.value
        _state.value = current.copy(popups = current.popups.filter { it.id != id })
    }

    private fun resetComboTimer() {
        comboJob?.cancel()
        comboJob = viewModelScope.launch {
            delay(GameState.COMBO_WINDOW_MS)
            _state.value = _state.value.copy(combo = 0)
        }
    }

    private fun schedulePopupRemoval(id: Int) {
        viewModelScope.launch {
            delay(800L)
            removePopup(id)
        }
    }

    private fun endGame() {
        timerJob?.cancel()
        comboJob?.cancel()
        val current = _state.value
        val newBest = maxOf(current.score, current.bestScore)
        val isNew = current.score > current.bestScore

        _state.value = current.copy(
            status = GameStatus.Finished,
            bestScore = newBest,
            isNewRecord = isNew,
            combo = 0,
            popups = emptyList()
        )

        if (isNew) {
            viewModelScope.launch { storage.saveBestScore(newBest) }
        }
    }

    private fun randomX(size: Float = _state.value.targetSize): Float {
        val maxX = (fieldWidth - size).coerceAtLeast(0f)
        return Random.nextFloat() * maxX
    }

    private fun randomY(size: Float = _state.value.targetSize): Float {
        val maxY = (fieldHeight - size).coerceAtLeast(0f)
        return Random.nextFloat() * maxY
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        comboJob?.cancel()
    }

    companion object {
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
