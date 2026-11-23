package com.cte.examiner.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cte.examiner.domain.model.Examinee
import com.cte.examiner.ui.theme.Navy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onLoggedOut: () -> Unit = {}
) {
    val state by viewModel.store.state.collectAsStateWithLifecycle()
    val store = viewModel.store
    val context = LocalContext.current
    var code by remember { mutableStateOf("") }

    if (state.loggedOut) onLoggedOut()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.userInfo?.fullName ?: "CTE Examiner") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Navy,
                    titleContentColor = Color.White
                ),
                actions = {
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
                            code = scanned          // show it in the field
                            store.onScan(scanned)   // and look it up
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
                state.examinee != null -> {
                    ExamineeCard(state.examinee!!)
                }
            }
        }
    }
}

@Composable
private fun ExamineeCard(examinee: Examinee) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            val verified = examinee.isAllowed
            Surface(
                color = if (verified) Navy else MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (verified) "VERIFIED" else "NOT VERIFIED",
                    modifier = Modifier.padding(12.dp),
                    color = if (verified) Color.White else MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(Modifier.height(12.dp))

            InfoRow("Code", examinee.assetQualityCode)
            InfoRow("Owner", examinee.ownerName)
            InfoRow("Owner #", examinee.ownerANum)
            InfoRow("Product", examinee.product)
            InfoRow("Batch", examinee.batchNumber)
            InfoRow("Batch date", examinee.batchDate)
            InfoRow("Expiry", examinee.expiryDate)
            InfoRow("Status", examinee.status)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String?) {
    if (value.isNullOrBlank()) return
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text("$label:", modifier = Modifier.width(110.dp), style = MaterialTheme.typography.labelLarge)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}