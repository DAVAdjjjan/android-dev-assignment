package com.assigments.lab1.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ActionButtons(
    onOkClick: () -> Unit, onCancelClick: () -> Unit
) {
    Column {

        Button(onClick = onOkClick) {
            Text("OK")
        }

        Button(onClick = onCancelClick) {
            Text("Cancel")
        }
    }
}