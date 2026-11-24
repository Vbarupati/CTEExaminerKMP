package com.cte.examiner.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cte.examiner.ui.theme.Navy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onLoggedOut: () -> Unit = {},
    onOpenSettings: () -> Unit = {}
) {
    val state by viewModel.store.state.collectAsStateWithLifecycle()
    val store = viewModel.store
    val context = LocalContext.current
    var code by remember { mutableStateOf("") }

    // Navigate back to login after logout
    LaunchedEffect(state.loggedOut) {
        if (state.loggedOut) onLoggedOut()
    }

    // When we have a result, show the full Scan Result page
    if (state.examinee != null) {
        ScanResultScreen(
            examinee = state.examinee!!,
            onScanNext = {
                code = ""
                store.clearResult()
            },
            onBack = {
                code = ""
                store.clearResult()
            }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.userInfo?.fullName ?: "CTE Examiner") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Navy,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                    IconButton(onClick = store::logout) {
                        Icon(Icons.Filled.Logout, contentDescription = "Logout", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Signed-in info
            state.userInfo?.let { user ->
                Text("Signed in as ${user.userName}", style = MaterialTheme.typography.bodyMedium)
                if (user.companyName.isNotBlank()) {
                    Text(user.companyName, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(16.dp))
            }

            // Manual lookup field
            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text("Enter / scan code") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // Look up typed code
            Button(
                onClick = { store.onScan(code) },
                enabled = code.isNotBlank() && !state.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Navy),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Filled.QrCodeScanner, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("Look up", color = Color.White)
            }

            Spacer(Modifier.height(12.dp))

            // Scan with camera
            OutlinedButton(
                onClick = {
                    scanBarcode(
                        context = context,
                        onResult = { scanned ->
                            code = scanned
                            store.onScan(scanned)
                        }
                    )
                },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Icon(Icons.Filled.CameraAlt, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Scan with camera")
            }

            Spacer(Modifier.height(20.dp))

            when {
                state.isLoading -> {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Navy)
                    }
                }
                state.errorMessage != null -> {
                    Text(state.errorMessage!!, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}