package com.assigments.lab1.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.assigments.lab.ui.components.*
import com.assigments.lab1.ui.components.ActionButtons
import com.assigments.lab1.ui.components.FontSelector

@Composable
fun MyScreen() {

    val context = LocalContext.current

    var inputText by remember { mutableStateOf("") }
    var selectedFont by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {

        FontSelector(
            selectedFont = selectedFont,
            onFontSelected = { selectedFont = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        InputField(
            value = inputText,
            onValueChange = { inputText = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        ActionButtons(
            onOkClick = {
                if (inputText.isEmpty() || selectedFont.isEmpty()) {
                    Toast.makeText(
                        context,
                        "Будь ласка, введіть текст і оберіть шрифт",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    resultText = inputText
                }
            },
            onCancelClick = {
                inputText = ""
                resultText = ""
                selectedFont = ""
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        ResultText(
            text = resultText,
            selectedFont = selectedFont
        )
    }
}