package com.assignments.lab6.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.assignments.lab6.ui.components.GameControls
import com.assignments.lab6.ui.components.GameHeader
import com.assignments.lab6.ui.components.TargetView
import com.assignments.lab6.viewmodel.GameStatus
import com.assignments.lab6.viewmodel.GameViewModel

@Composable
fun GameScreen(viewModel: GameViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        GameHeader(
            score = state.score, bestScore = state.bestScore, timeLeft = state.timeLeft
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
                }) {
            if (state.status == GameStatus.Running) {
                TargetView(
                    x = state.targetX, y = state.targetY, onTap = { viewModel.onTargetTapped() })
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
            status = state.status, score = state.score, onStartGame = { viewModel.startGame() })
    }
}
