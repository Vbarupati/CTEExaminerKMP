package com.cte.examiner.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cte.examiner.domain.model.Examinee
import com.cte.examiner.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanResultScreen(
    examinee: Examinee,
    onScanNext: () -> Unit,
    onBack: () -> Unit
) {
    val verified = examinee.isAllowed
    val notFound = examinee.status.equals("No record", ignoreCase = true) ||
            (examinee.ownerName.isNullOrBlank() && examinee.ownerANum.isNullOrBlank())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan Result", color = Navy, fontWeight = FontWeight.Bold) }
            )
        },
        bottomBar = {
            Button(
                onClick = onScanNext,
                colors = ButtonDefaults.buttonColors(containerColor = NavyDark),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp)
            ) {
                Icon(Icons.Filled.QrCodeScanner, null, tint = androidx.compose.ui.graphics.Color.White)
                Spacer(Modifier.width(8.dp))
                Text("Scan Next", color = androidx.compose.ui.graphics.Color.White,
                    fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Brand row + status chip
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("trakaid", style = MaterialTheme.typography.headlineSmall,
                        color = Navy, fontWeight = FontWeight.Bold)
                    Text("Digital Trail of Your Assets",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusChip(verified)
            }

            Spacer(Modifier.height(16.dp))

            if (verified) VerifiedCard() else NotVerifiedCard()

            Spacer(Modifier.height(20.dp))

            if (notFound) {
                NotFoundBlock(examinee.assetQualityCode)
            } else {
                DetailsBlock(examinee)
            }
        }
    }
}

@Composable
private fun StatusChip(verified: Boolean) {
    val bg = if (verified) GreenBg else RedBg
    val fg = if (verified) GreenChip else RedChip
    val label = if (verified) "Authentic & Active" else "Not Verified"
    Surface(color = bg, shape = RoundedCornerShape(50)) {
        Text("● $label", color = fg, style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
    }
}

@Composable
private fun VerifiedCard() {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(20.dp)) {
            Text("● CERTIFICATE VERIFIED", color = Orange,
                style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Text("This asset is", style = MaterialTheme.typography.headlineMedium,
                color = Navy, fontWeight = FontWeight.Bold)
            Text("genuine.", style = MaterialTheme.typography.headlineMedium,
                color = Orange, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Text("This page confirms the asset below has been verified through the " +
                    "Trakaid Digital Trust Network.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun NotVerifiedCard() {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(20.dp)) {
            Text("● NOT VERIFIED", color = RedChip,
                style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Text("This asset is not verified by the Trakaid Digital Trust Network.",
                style = MaterialTheme.typography.headlineSmall,
                color = Navy, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun NotFoundBlock(code: String) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(color = RedBg, shape = androidx.compose.foundation.shape.CircleShape,
            modifier = Modifier.size(96.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.ErrorOutline, null, tint = RedChip, modifier = Modifier.size(48.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("Asset Not Found", style = MaterialTheme.typography.headlineSmall,
            color = Navy, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("No asset is linked to this code. Please check the code and scan again.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(20.dp))
        OutlinedCard(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text("ASSET QUALITY CODE", style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Text(code, style = MaterialTheme.typography.titleLarge,
                    color = Navy, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DetailsBlock(e: Examinee) {
    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("CERTIFICATE OF AUTHENTICITY", color = Orange,
                style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            DetailRow("Asset number", e.assetQualityCode)
            DetailRow("Owner", e.ownerName)
            DetailRow("Owner #", e.ownerANum)
            DetailRow("Product", e.product)
            DetailRow("Batch", e.batchNumber)
            DetailRow("Batch date", e.batchDate)
            DetailRow("Expiry", e.expiryDate)
            DetailRow("Status", e.status)
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String?) {
    if (value.isNullOrBlank()) return
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(label, Modifier.width(120.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}