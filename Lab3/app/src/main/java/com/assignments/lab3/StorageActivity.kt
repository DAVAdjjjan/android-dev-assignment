package com.assignments.lab3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.assignments.lab3.data.local.AppDatabase
import com.assignments.lab3.data.repository.SavedResultsRepository
import com.assignments.lab3.ui.screen.StorageScreen
import com.assignments.lab3.ui.theme.Lab3Theme
import com.assignments.lab3.viewmodel.StorageViewModel

class StorageActivity : ComponentActivity() {

    private val viewModel: StorageViewModel by viewModels {
        val db = AppDatabase.getInstance(applicationContext)
        val repository = SavedResultsRepository(db.savedResultDao())
        StorageViewModel.Factory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab3Theme {
                val uiState by viewModel.uiState.collectAsState()
                StorageScreen(
                    uiState = uiState,
                    onDelete = viewModel::delete,
                    onDeleteAll = viewModel::deleteAll,
                    onStartEdit = viewModel::startEditing,
                    onEditTextChange = viewModel::updateEditText,
                    onEditFontChange = viewModel::updateEditFont,
                    onSaveEdit = viewModel::saveEdit,
                    onCancelEdit = viewModel::cancelEditing,
                    onBack = { finish() }
                )
            }
        }
    }
}
