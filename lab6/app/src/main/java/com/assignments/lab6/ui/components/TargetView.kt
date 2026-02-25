package com.assignments.lab6.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.assignments.lab6.viewmodel.GameViewModel

@Composable
fun TargetView(
    x: Float,
    y: Float,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sizeDp = with(LocalDensity.current) { GameViewModel.TARGET_SIZE.toDp() }

    Canvas(
        modifier = modifier
            .offset { IntOffset(x.toInt(), y.toInt()) }
            .size(sizeDp)
            .pointerInput(Unit) {
                detectTapGestures { onTap() }
            }
    ) {
        val radius = size.minDimension / 2f
        val center = Offset(radius, radius)

        drawCircle(color = Color(0xFFE53935), radius = radius, center = center)
        drawCircle(color = Color.White, radius = radius * 0.7f, center = center)
        drawCircle(color = Color(0xFFE53935), radius = radius * 0.4f, center = center)
    }
}
