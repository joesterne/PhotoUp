package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.ui.screens.CameraScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.StyleMorphViewModel

enum class AppScreen {
    HOME,
    CAMERA
}

class MainActivity : ComponentActivity() {

    private val viewModel: StyleMorphViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                var currentScreen by remember { mutableStateOf(AppScreen.HOME) }

                when (currentScreen) {
                    AppScreen.HOME -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToCamera = { currentScreen = AppScreen.CAMERA }
                        )
                    }
                    AppScreen.CAMERA -> {
                        CameraScreen(
                            viewModel = viewModel,
                            onNavigateBack = { currentScreen = AppScreen.HOME }
                        )
                    }
                }
            }
        }
    }
}
