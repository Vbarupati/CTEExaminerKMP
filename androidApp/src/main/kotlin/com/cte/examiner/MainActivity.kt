package com.cte.examiner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cte.examiner.ui.home.HomeScreen
import com.cte.examiner.ui.home.HomeViewModel
import com.cte.examiner.ui.login.LoginScreen
import com.cte.examiner.ui.login.LoginViewModel
import com.cte.examiner.ui.theme.CteTheme
import androidx.compose.runtime.getValue      // ← add
import androidx.compose.runtime.setValue      // ← add

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as CteApplication).container
        setContent {
            CteTheme {
                var loggedIn by remember { mutableStateOf(false) }
                if (!loggedIn) {
                    val vm: LoginViewModel = viewModel { LoginViewModel(container) }
                    LoginScreen(viewModel = vm, onLoggedIn = { loggedIn = true })
                } else {
                    val vm: HomeViewModel = viewModel { HomeViewModel(container) }
                    HomeScreen(viewModel = vm, onLoggedOut = { loggedIn = false })
                }
            }
        }
    }
}