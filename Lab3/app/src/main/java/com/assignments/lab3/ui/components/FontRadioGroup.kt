package com.assignments.lab3.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.assignments.lab3.viewmodel.FontOption

@Composable
fun FontRadioGroup(
    selectedFont: FontOption?,
    onFontSelected: (FontOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.selectableGroup()) {
        Text(
            text = "Select Font",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        FontOption.entries.forEach { font ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = font == selectedFont,
                        onClick = { onFontSelected(font) },
                        role = Role.RadioButton
                    )
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = font == selectedFont,
                    onClick = null
                )
                Text(
                    text = font.label,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }
    }
}
