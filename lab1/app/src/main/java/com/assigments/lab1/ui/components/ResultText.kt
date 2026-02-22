package com.assigments.lab.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp

@Composable
fun ResultText(
    text: String, selectedFont: String
) {
    Text(
        text = text, fontSize = 22.sp, fontFamily = when (selectedFont) {
            "sans" -> FontFamily.SansSerif
            "serif" -> FontFamily.Serif
            "mono" -> FontFamily.Monospace
            else -> FontFamily.Default
        }
    )
}