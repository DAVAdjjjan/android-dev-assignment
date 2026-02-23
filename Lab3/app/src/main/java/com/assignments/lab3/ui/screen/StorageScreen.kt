package com.assignments.lab3.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.assignments.lab3.data.local.SavedResultEntity
import com.assignments.lab3.ui.components.FontRadioGroup
import com.assignments.lab3.viewmodel.FontOption
import com.assignments.lab3.viewmodel.StorageUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageScreen(
    uiState: StorageUiState,
    onDelete: (SavedResultEntity) -> Unit,
    onDeleteAll: () -> Unit,
    onStartEdit: (SavedResultEntity) -> Unit,
    onEditTextChange: (String) -> Unit,
    onEditFontChange: (FontOption) -> Unit,
    onSaveEdit: () -> Unit,
    onCancelEdit: () -> Unit,
    onBack: () -> Unit
) {
    var showClearAllDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Records") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (uiState.records.isNotEmpty()) {
                        TextButton(onClick = { showClearAllDialog = true }) {
                            Text("Clear All")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.records.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No saved data",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.records, key = { it.id }) { record ->
                    RecordItem(
                        record = record,
                        onEdit = { onStartEdit(record) },
                        onDelete = { onDelete(record) }
                    )
                }
            }
        }
    }

    if (showClearAllDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllDialog = false },
            title = { Text("Clear All Records") },
            text = { Text("Are you sure you want to delete all saved records?") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteAll()
                    showClearAllDialog = false
                }) {
                    Text("Delete All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (uiState.editingRecord != null) {
        AlertDialog(
            onDismissRequest = onCancelEdit,
            title = { Text("Edit Record") },
            text = {
                Column {
                    OutlinedTextField(
                        value = uiState.editText,
                        onValueChange = onEditTextChange,
                        label = { Text("Text") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    FontRadioGroup(
                        selectedFont = uiState.editFont,
                        onFontSelected = onEditFontChange
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onSaveEdit,
                    enabled = uiState.editText.isNotBlank() && uiState.editFont != null
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = onCancelEdit) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun RecordItem(
    record: SavedResultEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val fontFamily = when (record.selectedFont) {
        "sans" -> FontFamily.SansSerif
        "serif" -> FontFamily.Serif
        "mono" -> FontFamily.Monospace
        else -> FontFamily.Default
    }
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = record.text,
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = fontFamily
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Font: ${record.selectedFont}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = dateFormat.format(Date(record.createdAt)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}
