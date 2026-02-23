package com.assignments.lab2.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.assignments.lab2.ui.components.FontRadioGroup
import com.assignments.lab2.viewmodel.FontOption

@Composable
fun InputScreen(
    inputText: String,
    selectedFont: FontOption?,
    isOkEnabled: Boolean,
    onTextChange: (String) -> Unit,
    onFontChange: (FontOption) -> Unit,
    onOkClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = inputText,
            onValueChange = onTextChange,
            label = { Text("Enter text") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        FontRadioGroup(
            selectedFont = selectedFont,
            onFontSelected = onFontChange,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onOkClick,
            enabled = isOkEnabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("OK")
        }
    }
}
