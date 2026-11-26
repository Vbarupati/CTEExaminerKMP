package com.cte.examiner.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cte.examiner.R
import com.cte.examiner.domain.model.Examinee

private val LabelGrey = Color(0xFF4A5A6A)
private val CardBorder = Color(0xFFE2E6EC)
private val BrandBlue = Color(0xFF172D70)
private val BrandOrange = Color(0xFFFF9800)
private val PendingAmber = Color(0xFFB26A00)
private val PageBackground = Color(0xFFF5F7FB)
private val ErrorRed = Color(0xFFD32F2F)       // ← add
private val SuccessGreen = Color(0xFF1E8E3E)   // ← add

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanResultScreen(
    examinee: Examinee,
    onScanNext: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        containerColor = PageBackground,
        topBar = {
            TopAppBar(
                title = { Text("Scan Result", fontWeight = FontWeight.Bold, color = BrandBlue) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = BrandBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            )
        },
        bottomBar = {
            Surface(color = Color.White, shadowElevation = 8.dp) {
                Button(
                    onClick = onScanNext,
                    modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp).height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                ) {
                    Icon(Icons.Default.QrCodeScanner, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Scan Next", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        },
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            val assetNotFound =
                examinee.ownerName.isNullOrBlank() &&
                        examinee.product.isNullOrBlank() &&
                        examinee.batchNumber.isNullOrBlank() &&
                        examinee.filledAt.isNullOrBlank()

            if (assetNotFound) {
                AssetNotFoundContent(examinee.assetQualityCode)
            } else {
                ResultContent(examinee)
            }
        }
    }
}

// ---------------- Asset Not Found ----------------

@Composable
private fun AssetNotFoundContent(code: String?) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            BrandLogo()
            VerificationPill(false)
        }

        WhiteCard {
            Text("● NOT VERIFIED", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp, color = ErrorRed)
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = BrandBlue)) { append("This asset is ") }
                    withStyle(SpanStyle(color = ErrorRed)) { append("not verified") }
                    withStyle(SpanStyle(color = BrandBlue)) { append(" by the Trakaid Digital Trust Network.") }
                },
                fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.ExtraBold,
            )
        }

        Column(
            Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(Modifier.size(80.dp), shape = RoundedCornerShape(50),
                color = ErrorRed.copy(alpha = 0.10f)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.ErrorOutline, null, tint = ErrorRed, modifier = Modifier.size(42.dp))
                }
            }
            Spacer(Modifier.height(14.dp))
            Text("Asset Not Found", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = BrandBlue)
            Spacer(Modifier.height(8.dp))
            Text("No asset is linked to this code. Please check the code and scan again.",
                fontSize = 13.sp, lineHeight = 19.sp, color = LabelGrey, textAlign = TextAlign.Center)
            if (!code.isNullOrBlank()) {
                Spacer(Modifier.height(16.dp))
                Surface(shape = RoundedCornerShape(12.dp), color = Color.White,
                    border = BorderStroke(1.dp, CardBorder)) {
                    Column(Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        CardLabel("Asset Quality Code")
                        Spacer(Modifier.height(4.dp))
                        Text(code, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = BrandBlue)
                    }
                }
            }
        }
    }
}

// ---------------- Full certificate ----------------

@Composable
private fun ResultContent(info: Examinee) {
    val verified = info.isAllowed
    val assetNo = info.ownerANum?.takeIf { it.isNotBlank() } ?: "—"

    Column(
        modifier = Modifier.fillMaxSize().background(PageBackground)
            .verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            BrandLogo()
            VerificationPill(verified)
        }

        WhiteCard {
            Text(if (verified) "● CERTIFICATE VERIFIED" else "● NOT VERIFIED",
                fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.5.sp,
                color = if (verified) BrandOrange else ErrorRed)
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = BrandBlue)) { append("This asset is ") }
                    withStyle(SpanStyle(color = if (verified) BrandOrange else ErrorRed)) {
                        append(if (verified) "genuine." else "not verified.")
                    }
                },
                fontSize = 34.sp, lineHeight = 38.sp, fontWeight = FontWeight.ExtraBold,
            )
            Text(
                "This page confirms that the asset below has been verified through Trakaid Assetotrak. " +
                        "The certificate links the asset identity, product details, batch information and " +
                        "delivery status into one digital trust record.",
                fontSize = 14.sp, lineHeight = 23.sp, color = LabelGrey,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TrustTag("Non-fakeable identity")
                TrustTag("Batch-linked origin")
            }
        }

        WhiteCard {
            Text("CERTIFICATE OF AUTHENTICITY", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp, color = BrandOrange)
            Text("Verified by the Trakaid Digital Trust Network",
                fontSize = 27.sp, lineHeight = 32.sp, fontWeight = FontWeight.ExtraBold, color = BrandBlue)
            Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                color = Color(0xFFF5F8FC), border = BorderStroke(1.dp, CardBorder)) {
                Column(Modifier.padding(16.dp)) {
                    CardLabel("Asset Number")
                    Spacer(Modifier.height(8.dp))
                    Text(assetNo, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = BrandBlue)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(Modifier.size(68.dp), shape = RoundedCornerShape(50),
                    color = if (verified) BrandOrange else ErrorRed) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(if (verified) "GENUINE\nCERTIFIED" else "NOT\nVERIFIED",
                            fontSize = 10.sp, fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center, color = Color.White)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    if (verified) "Certificate data has been matched against the product information."
                    else "This asset could not be matched against the product records. Please treat it with caution.",
                    fontSize = 12.sp, lineHeight = 18.sp,
                    color = if (verified) LabelGrey else ErrorRed,
                )
            }
        }

        Text("ASSET INFORMATION", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.5.sp, color = BrandOrange)
        InfoCardRow("Owner name", info.ownerName, "Product", info.product)
        InfoCardRow("Filled at", info.filledAt, "Batch number", info.batchNumber)
        InfoCardRow("Batch date", info.batchDate, "Expiry date", info.expiryDate)
        InfoCard("Asset quality code", info.assetQualityCode, Modifier.fillMaxWidth())

        DeliveryStatusCard(info.status)

        WhiteCard {
            Text("DIGITAL TRUST PROOF", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp, color = BrandOrange)
            Text("What this certificate confirms", fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold, color = BrandBlue)
            CheckLine("The asset number is linked to a specific product and owner record.")
            CheckLine("The batch details and fill location are visible to the customer.")
            CheckLine("The delivery status is presented as part of the certificate record.")
            CheckLine("The certificate creates a customer-facing proof point for authenticity and trust.")
        }

        WhiteCard {
            Text("DIGITAL TRAIL", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp, color = BrandOrange)
            Text("Asset verification flow", fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold, color = BrandBlue)
            Step(1, "Asset identified", "Asset number $assetNo is attached to the physical item.", false)
            Step(2, "Batch recorded", "Batch ${info.batchNumber ?: "—"} is linked to the product record.", false)
            Step(3, "Certificate generated", "The customer can scan the QR code to view the certificate page.", false)
            Step(4, "Delivery status", info.status?.takeIf { it.isNotBlank() } ?: "Status not available", true)
        }

        Text("Trakaid Assetotrak · Certificate of Authenticity", fontSize = 12.sp,
            fontWeight = FontWeight.Bold, color = BrandBlue)
        Text("Protected digital trust record · support@trakaid.com", fontSize = 12.sp, color = LabelGrey)
        Spacer(Modifier.height(4.dp))
    }
}

// ---------------- helpers ----------------

@Composable
private fun BrandLogo() {
    Image(
        painter = painterResource(R.drawable.trakaid_logo_vector),
        contentDescription = "trakaid — Digital Trail of Your Assets",
        contentScale = ContentScale.Fit,
        modifier = Modifier.height(56.dp),
    )
}

@Composable
private fun VerificationPill(verified: Boolean) {
    val c = if (verified) SuccessGreen else ErrorRed
    Surface(shape = RoundedCornerShape(30.dp),
        color = if (verified) Color(0xFFE8F7EE) else ErrorRed.copy(alpha = 0.10f),
        border = BorderStroke(1.dp, if (verified) Color(0xFFB8E3C8) else ErrorRed.copy(alpha = 0.4f))) {
        Text(if (verified) "● Authentic & Active" else "● Not Verified",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
            fontSize = 10.sp, fontWeight = FontWeight.Bold, color = c)
    }
}

@Composable
private fun DeliveryStatusCard(status: String?) {
    val text = status?.takeIf { it.isNotBlank() } ?: "Status not available"
    val lower = text.lowercase()
    val delivered = lower.contains("deliver") && !lower.contains("not")
    val pending = lower.contains("not yet") || lower.contains("pending") ||
            lower.contains("not deliver") || lower.contains("awaiting")
    val accent = when {
        delivered -> SuccessGreen
        pending -> PendingAmber
        else -> BrandBlue
    }
    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        color = accent.copy(alpha = 0.08f), border = BorderStroke(1.dp, accent.copy(alpha = 0.3f))) {
        Column(Modifier.padding(18.dp)) {
            CardLabel("Delivery Status")
            Spacer(Modifier.height(10.dp))
            Text(text, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = accent)
        }
    }
}

@Composable
private fun WhiteCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
        color = Color.White, border = BorderStroke(1.dp, CardBorder)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
    }
}

@Composable
private fun TrustTag(text: String) {
    Surface(color = Color(0xFFEBF0FF), shape = RoundedCornerShape(30.dp)) {
        Text(text, modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
            fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandBlue)
    }
}

@Composable
private fun InfoCardRow(label1: String, value1: String?, label2: String, value2: String?) {
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        InfoCard(label1, value1, Modifier.weight(1f).fillMaxHeight())
        InfoCard(label2, value2, Modifier.weight(1f).fillMaxHeight())
    }
}

@Composable
private fun InfoCard(label: String, value: String?, modifier: Modifier = Modifier) {
    Surface(modifier, shape = RoundedCornerShape(16.dp), color = Color.White, border = BorderStroke(1.dp, CardBorder)) {
        Column(Modifier.padding(16.dp)) {
            CardLabel(label)
            Spacer(Modifier.height(10.dp))
            Text(value?.takeIf { it.isNotBlank() } ?: "—", fontSize = 17.sp, lineHeight = 23.sp,
                fontWeight = FontWeight.ExtraBold, color = Color(0xFF14213D))
        }
    }
}

@Composable
private fun CheckLine(text: String) {
    Row(Modifier.padding(top = 4.dp)) {
        Box(Modifier.size(20.dp).clip(CircleShape).background(Color(0xFFDCE6F7)),
            contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Check, null, tint = BrandBlue, modifier = Modifier.size(13.dp))
        }
        Spacer(Modifier.width(10.dp))
        Text(text, fontSize = 14.sp, lineHeight = 20.sp, color = Color(0xFF44506B))
    }
}

@Composable
private fun Step(n: Int, title: String, body: String, last: Boolean) {
    Row(Modifier.padding(top = 4.dp)) {
        Box(Modifier.size(26.dp).clip(CircleShape).background(if (last) BrandOrange else BrandBlue),
            contentAlignment = Alignment.Center) {
            Text("$n", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BrandBlue)
            Text(body, fontSize = 13.sp, lineHeight = 18.sp, color = LabelGrey)
        }
    }
}

@Composable
private fun CardLabel(text: String) {
    Text(text.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.3.sp, color = LabelGrey)
}