package com.assigments.lab1.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun FontSelector(
    selectedFont: String, onFontSelected: (String) -> Unit
) {
    Column {
        Text("Оберіть шрифт:")

        FontRadio("Sans Serif", "sans", selectedFont, onFontSelected)
        FontRadio("Serif", "serif", selectedFont, onFontSelected)
        FontRadio("Monospace", "mono", selectedFont, onFontSelected)
    }
}