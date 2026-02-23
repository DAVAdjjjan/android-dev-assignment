package com.assignments.lab2.viewmodel

import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class FontOption(val label: String, val fontFamily: FontFamily) {
    SANS_SERIF("Sans Serif", FontFamily.SansSerif),
    SERIF("Serif", FontFamily.Serif),
    MONOSPACE("Monospace", FontFamily.Monospace)
}

data class UiState(
    val inputText: String = "",
    val selectedFont: FontOption? = null,
    val confirmedText: String? = null,
    val confirmedFont: FontOption? = null
) {
    val isOkEnabled: Boolean
        get() = inputText.isNotBlank() && selectedFont != null && (inputText != confirmedText || selectedFont != confirmedFont)
}

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun updateFont(font: FontOption) {
        _uiState.update { it.copy(selectedFont = font) }
    }

    fun confirm() {
        _uiState.update {
            it.copy(
                confirmedText = it.inputText, confirmedFont = it.selectedFont
            )
        }
    }

    fun reset() {
        _uiState.update {
            UiState()
        }
    }
}
