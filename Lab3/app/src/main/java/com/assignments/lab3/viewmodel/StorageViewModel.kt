package com.assignments.lab3.viewmodel

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

data class StorageUiState(
    val records: List<SavedResultEntity> = emptyList(),
    val editingRecord: SavedResultEntity? = null,
    val editText: String = "",
    val editFont: FontOption? = null
)

class StorageViewModel(private val repository: SavedResultsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(StorageUiState())
    val uiState: StateFlow<StorageUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAll().collect { records ->
                _uiState.update { it.copy(records = records) }
            }
        }
    }

    fun delete(record: SavedResultEntity) {
        viewModelScope.launch {
            repository.delete(record)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }

    fun startEditing(record: SavedResultEntity) {
        _uiState.update {
            it.copy(
                editingRecord = record,
                editText = record.text,
                editFont = FontOption.fromKey(record.selectedFont)
            )
        }
    }

    fun updateEditText(text: String) {
        _uiState.update { it.copy(editText = text) }
    }

    fun updateEditFont(font: FontOption) {
        _uiState.update { it.copy(editFont = font) }
    }

    fun cancelEditing() {
        _uiState.update {
            it.copy(editingRecord = null, editText = "", editFont = null)
        }
    }

    fun saveEdit() {
        val state = _uiState.value
        val record = state.editingRecord ?: return
        val font = state.editFont ?: return
        if (state.editText.isBlank()) return

        viewModelScope.launch {
            repository.update(
                record.copy(text = state.editText, selectedFont = font.key)
            )
            _uiState.update {
                it.copy(editingRecord = null, editText = "", editFont = null)
            }
        }
    }

    class Factory(private val repository: SavedResultsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StorageViewModel(repository) as T
        }
    }
}
