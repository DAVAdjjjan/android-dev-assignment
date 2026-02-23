package com.assignments.lab3.viewmodel

import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.assignments.lab3.data.local.SavedResultEntity
import com.assignments.lab3.data.repository.SavedResultsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class FontOption(val label: String, val fontFamily: FontFamily, val key: String) {
    SANS_SERIF("Sans Serif", FontFamily.SansSerif, "sans"),
    SERIF("Serif", FontFamily.Serif, "serif"),
    MONOSPACE("Monospace", FontFamily.Monospace, "mono");

    companion object {
        fun fromKey(key: String): FontOption = entries.first { it.key == key }
    }
}

data class UiState(
    val inputText: String = "",
    val selectedFont: FontOption? = null,
    val confirmedText: String? = null,
    val confirmedFont: FontOption? = null,
    val saveMessage: String? = null
) {
    val isOkEnabled: Boolean
        get() = inputText.isNotBlank() && selectedFont != null &&
                (inputText != confirmedText || selectedFont != confirmedFont)
}

class MainViewModel(private val repository: SavedResultsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun updateFont(font: FontOption) {
        _uiState.update { it.copy(selectedFont = font) }
    }

    fun confirmAndSave() {
        val state = _uiState.value
        val text = state.inputText
        val font = state.selectedFont ?: return

        _uiState.update {
            it.copy(confirmedText = text, confirmedFont = font)
        }

        viewModelScope.launch {
            try {
                repository.insert(
                    SavedResultEntity(
                        text = text,
                        selectedFont = font.key,
                        createdAt = System.currentTimeMillis()
                    )
                )
                _uiState.update { it.copy(saveMessage = "Saved successfully") }
            } catch (e: Exception) {
                _uiState.update { it.copy(saveMessage = "Failed to save: ${e.message}") }
            }
        }
    }

    fun clearSaveMessage() {
        _uiState.update { it.copy(saveMessage = null) }
    }

    fun reset() {
        _uiState.update { UiState() }
    }

    class Factory(private val repository: SavedResultsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(repository) as T
        }
    }
}
