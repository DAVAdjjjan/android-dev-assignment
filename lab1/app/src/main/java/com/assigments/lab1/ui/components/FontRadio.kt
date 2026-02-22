package com.assigments.lab1.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable

@Composable
fun FontRadio(
    label: String, value: String, selectedFont: String, onFontSelected: (String) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selectedFont == value, onClick = { onFontSelected(value) })
        Text(label)
    }
}