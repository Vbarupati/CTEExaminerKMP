package com.cte.examiner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cte.examiner.ui.home.HomeScreen
import com.cte.examiner.ui.home.HomeViewModel
import com.cte.examiner.ui.login.LoginScreen
import com.cte.examiner.ui.login.LoginViewModel
import com.cte.examiner.ui.settings.SettingsScreen
import com.cte.examiner.ui.settings.SettingsViewModel
import com.cte.examiner.ui.theme.CteTheme

private enum class Screen { Login, Settings, Home }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as CteApplication).container
        setContent {
            var darkTheme by remember { mutableStateOf(false) }
            var screen by remember { mutableStateOf(Screen.Login) }
            var cameFromHome by remember { mutableStateOf(false) }

            CteTheme(darkTheme = darkTheme) {
                when (screen) {
                    Screen.Login -> {
                        val vm: LoginViewModel = viewModel { LoginViewModel(container) }
                        LoginScreen(
                            viewModel = vm,
                            onLoggedIn = { screen = Screen.Home },
                            onOpenSettings = { cameFromHome = false; screen = Screen.Settings }
                        )
                    }
                    Screen.Settings -> {
                        val vm: SettingsViewModel = viewModel { SettingsViewModel(container) }
                        SettingsScreen(
                            viewModel = vm,
                            darkTheme = darkTheme,
                            onToggleDark = { darkTheme = it },
                            onBack = { screen = if (cameFromHome) Screen.Home else Screen.Login }
                        )
                    }
                    Screen.Home -> {
                        val vm: HomeViewModel = viewModel { HomeViewModel(container) }
                        HomeScreen(
                            viewModel = vm,
                            onLoggedOut = { screen = Screen.Login },
                            onOpenSettings = { cameFromHome = true; screen = Screen.Settings }
                        )
                    }
                }
            }
        }
    }
}