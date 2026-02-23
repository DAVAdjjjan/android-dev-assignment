package com.assignments.lab2.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.assignments.lab2.ui.screen.InputScreen
import com.assignments.lab2.ui.screen.ResultScreen
import com.assignments.lab2.viewmodel.MainViewModel

object Routes {
    const val INPUT = "input"
    const val RESULT = "result"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Routes.INPUT,
        modifier = modifier
    ) {
        composable(Routes.INPUT) {
            InputScreen(
                inputText = uiState.inputText,
                selectedFont = uiState.selectedFont,
                isOkEnabled = uiState.isOkEnabled,
                onTextChange = viewModel::updateInputText,
                onFontChange = viewModel::updateFont,
                onOkClick = {
                    viewModel.confirm()
                    navController.navigate(Routes.RESULT)
                }
            )
        }
        composable(Routes.RESULT) {
            ResultScreen(
                confirmedText = uiState.confirmedText.orEmpty(),
                confirmedFont = uiState.confirmedFont,
                onCancelClick = {
                    viewModel.reset()
                    navController.popBackStack(Routes.INPUT, inclusive = false)
                }
            )
        }
    }
}
