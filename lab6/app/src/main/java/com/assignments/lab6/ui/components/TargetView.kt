package com.assignments.lab6.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.assignments.lab6.viewmodel.TargetType

@Composable
fun TargetView(
    x: Float,
    y: Float,
    size: Float,
    targetKey: Int,
    targetType: TargetType,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sizeDp = with(LocalDensity.current) { size.toDp() }
    val isBonus = targetType == TargetType.Bonus

    val ringColor = if (isBonus) Color(0xFFFFD700) else MaterialTheme.colorScheme.error
    val middleColor = if (isBonus) Color(0xFFFFF8E1) else MaterialTheme.colorScheme.surface
    val centerColor = if (isBonus) Color(0xFFFFB300) else MaterialTheme.colorScheme.error

    val scaleAnim = remember { Animatable(0f) }

    LaunchedEffect(targetKey) {
        scaleAnim.snapTo(0f)
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    val rotation = if (isBonus) {
        val transition = rememberInfiniteTransition(label = "bonusSpin")
        val angle by transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation"
        )
        angle
    } else {
        0f
    }

    Canvas(
        modifier = modifier
            .offset { IntOffset(x.toInt(), y.toInt()) }
            .size(sizeDp)
            .pointerInput(Unit) {
                detectTapGestures { onTap() }
            }
    ) {
        scale(scaleAnim.value) {
            rotate(rotation) {
                val radius = this@Canvas.size.minDimension / 2f
                val center = Offset(radius, radius)

                drawCircle(color = ringColor, radius = radius, center = center)
                drawCircle(color = middleColor, radius = radius * 0.7f, center = center)
                drawCircle(color = centerColor, radius = radius * 0.4f, center = center)

                if (isBonus) {
                    val armWidth = radius * 0.12f
                    drawRect(
                        color = ringColor,
                        topLeft = Offset(center.x - armWidth, center.y - radius * 0.85f),
                        size = androidx.compose.ui.geometry.Size(armWidth * 2, radius * 1.7f)
                    )
                    drawRect(
                        color = ringColor,
                        topLeft = Offset(center.x - radius * 0.85f, center.y - armWidth),
                        size = androidx.compose.ui.geometry.Size(radius * 1.7f, armWidth * 2)
                    )
                }
            }
        }
    }
}
