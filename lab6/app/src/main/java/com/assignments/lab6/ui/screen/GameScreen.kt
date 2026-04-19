package com.assignments.lab6.ui.screen

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.assignments.lab6.ui.components.GameControls
import com.assignments.lab6.ui.components.GameHeader
import com.assignments.lab6.ui.components.TargetView
import com.assignments.lab6.viewmodel.GameStatus
import com.assignments.lab6.viewmodel.GameViewModel
import com.assignments.lab6.viewmodel.ScorePopup
import kotlinx.coroutines.launch

@Composable
fun GameScreen(viewModel: GameViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsState()
    val view = LocalView.current

    Column(modifier = modifier.fillMaxSize()) {
        GameHeader(
            score = state.score,
            bestScore = state.bestScore,
            timeLeft = state.timeLeft,
            combo = state.combo
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(12.dp)
                )
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp)
                )
                .onSizeChanged { size ->
                    viewModel.setFieldSize(size.width.toFloat(), size.height.toFloat())
                }
        ) {
            if (state.status == GameStatus.Running) {
                TargetView(
                    x = state.targetX,
                    y = state.targetY,
                    size = state.targetSize,
                    targetKey = state.targetKey,
                    targetType = state.targetType,
                    onTap = {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        viewModel.onTargetTapped()
                    }
                )

                state.popups.forEach { popup ->
                    FloatingScorePopup(popup = popup)
                }
            }

            if (state.status == GameStatus.Idle) {
                Text(
                    text = "Press Start to begin!",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        GameControls(
            status = state.status,
            score = state.score,
            totalTaps = state.totalTaps,
            bestCombo = state.bestCombo,
            isNewRecord = state.isNewRecord,
            onStartGame = { viewModel.startGame() }
        )
    }
}

@Composable
fun FloatingScorePopup(popup: ScorePopup) {
    val density = LocalDensity.current
    val alphaAnim = remember(popup.id) { Animatable(1f) }
    val offsetAnim = remember(popup.id) { Animatable(0f) }

    LaunchedEffect(popup.id) {
        launch {
            offsetAnim.animateTo(
                targetValue = -120f,
                animationSpec = tween(700)
            )
        }
        launch {
            alphaAnim.animateTo(
                targetValue = 0f,
                animationSpec = tween(700)
            )
        }
    }

    Text(
        text = popup.text,
        fontSize = if (popup.isBonus) 22.sp else 18.sp,
        fontWeight = FontWeight.ExtraBold,
        color = if (popup.isBonus) Color(0xFFFFB300) else Color(0xFFE53935),
        modifier = Modifier
            .offset {
                IntOffset(
                    x = popup.x.toInt() - with(density) { 20.dp.toPx() }.toInt(),
                    y = (popup.y + offsetAnim.value).toInt()
                )
            }
            .alpha(alphaAnim.value)
    )
}
