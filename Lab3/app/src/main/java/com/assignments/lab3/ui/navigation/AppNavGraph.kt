package com.assignments.lab3.ui.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.assignments.lab3.StorageActivity
import com.assignments.lab3.ui.screen.InputScreen
import com.assignments.lab3.ui.screen.ResultScreen
import com.assignments.lab3.viewmodel.MainViewModel

object Routes {
    const val INPUT = "input"
    const val RESULT = "result"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val openStorage = {
        context.startActivity(Intent(context, StorageActivity::class.java))
    }

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
                    viewModel.confirmAndSave()
                    navController.navigate(Routes.RESULT)
                },
                onOpenClick = openStorage
            )
        }
        composable(Routes.RESULT) {
            ResultScreen(
                confirmedText = uiState.confirmedText.orEmpty(),
                confirmedFont = uiState.confirmedFont,
                onCancelClick = {
                    viewModel.reset()
                    navController.popBackStack(Routes.INPUT, inclusive = false)
                },
                onOpenClick = openStorage
            )
        }
    }
}
