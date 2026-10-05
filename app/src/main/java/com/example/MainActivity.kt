package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.YSeoStudioTheme
import com.example.ui.viewmodel.StudioViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YSeoStudioTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: StudioViewModel = viewModel()
                    val uiState by viewModel.uiState.collectAsState()
                    var showWithdrawDialog by remember { mutableStateOf(false) }

                    when (uiState.currentScreen) {
                        "home" -> HomeScreen(
                            viewModel = viewModel,
                            onNavigateToTool = { toolId -> viewModel.navigateTo("tool_result", toolId) },
                            onNavigateToSaved = { viewModel.navigateTo("saved_items") },
                            onOpenWithdrawDialog = { showWithdrawDialog = true }
                        )
                        "video_detail" -> VideoDetailScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateTo("home") }
                        )
                        "tool_result" -> ToolScreen(
                            viewModel = viewModel,
                            toolId = uiState.activeToolName,
                            onBack = { viewModel.navigateTo("home") }
                        )
                        "saved_items" -> SavedItemsScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateTo("home") }
                        )
                    }

                    if (showWithdrawDialog) {
                        WithdrawDialog(
                            onDismiss = { showWithdrawDialog = false }
                        )
                    }
                }
            }
        }
    }
}
