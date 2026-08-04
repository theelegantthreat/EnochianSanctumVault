package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SavedSigil
import com.example.data.reference.EnochianData
import com.example.data.reference.EnochianLetter
import com.example.ui.theme.CelestialCyan
import com.example.ui.theme.ElementalGreen
import com.example.ui.theme.ElementalRed
import com.example.ui.theme.EnochianGold
import com.example.ui.theme.GoldOutline
import com.example.ui.theme.MysticViolet
import com.example.utils.SigilExportUtils
import kotlin.math.cos
import kotlin.math.sin

data class ExportSigilData(
    val title: String,
    val phrase: String,
    val method: String,
    val colorHex: String
)

@Composable
fun SigilGeneratorScreen(
    savedSigils: List<SavedSigil>,
    onSaveSigil: (String, String, String, String, String, String) -> Unit,
    onDeleteSigil: (Long) -> Unit,
    initialIntention: String? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) } // 0 = Generator, 1 = Saved Library

    var intentionText by remember { mutableStateOf(initialIntention?.ifEmpty { "PROTECTION OF BATAIVAH" } ?: "PROTECTION OF BATAIVAH") }

    var strokeWidth by remember { mutableStateOf(6f) }
    var selectedColorIndex by remember { mutableStateOf(0) }
    var isShowSaveDialog by remember { mutableStateOf(false) }
    var sigilTitleInput by remember { mutableStateOf("") }

    val colorPalette = listOf(
        EnochianGold to "#FFD54F",
        MysticViolet to "#B388FF",
        CelestialCyan to "#80D8FF",
        ElementalRed to "#FF5252",
        ElementalGreen to "#69F0AE"
    )

    val activeColorPair = colorPalette[selectedColorIndex]

    // Create Document Launcher for custom location save
    var pendingExportParams by remember { mutableStateOf<ExportSigilData?>(null) }
    val customPngLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("image/png")
    ) { uri: Uri? ->
        if (uri != null && pendingExportParams != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { os ->
                    val data = pendingExportParams!!
                    val bitmap = SigilExportUtils.createSigilBitmap(data.title, data.phrase, data.method, data.colorHex)
                    val success = SigilExportUtils.writeSigilBitmapToStream(bitmap, os)
                    if (success) {
                        Toast.makeText(context, "Sigil PNG exported successfully for ritual printing!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Failed to export Sigil PNG image.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Export error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Enochian Sigil Generator",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = EnochianGold
        )
        Text(
            text = "Generate geometric sigils from intentions using the Enochian Rose Wheel",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs: Generator vs Library
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = EnochianGold,
            divider = {}
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Generator", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Generator") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Saved Library (${savedSigils.size})", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Bookmark, contentDescription = "Library") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTab == 0) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    // Intention Input
                    OutlinedTextField(
                        value = intentionText,
                        onValueChange = { intentionText = it.uppercase() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sigil_intention_input"),
                        label = { Text("Intention / Divine Name / Word") },
                        placeholder = { Text("ENTER WORD OR INTENTION...") },
                        trailingIcon = {
                            IconButton(onClick = { intentionText = "" }) {
                                Icon(Icons.Default.Refresh, contentDescription = "Clear")
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EnochianGold,
                            unfocusedBorderColor = GoldOutline,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    // Controls: Colors & Thickness
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Color Palette:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            colorPalette.forEachIndexed { idx, pair ->
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(pair.first)
                                        .border(
                                            2.dp,
                                            if (selectedColorIndex == idx) Color.White else Color.Transparent,
                                            CircleShape
                                        )
                                        .clickable { selectedColorIndex = idx }
                                )
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Stroke: ${strokeWidth.toInt()}px",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.width(80.dp)
                        )
                        Slider(
                            value = strokeWidth,
                            onValueChange = { strokeWidth = it },
                            valueRange = 2f..16f,
                            colors = SliderDefaults.colors(
                                thumbColor = EnochianGold,
                                activeTrackColor = EnochianGold
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    // Interactive Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, GoldOutline, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        EnochianWheelCanvas(
                            phrase = intentionText,
                            lineColor = activeColorPair.first,
                            strokeWidthPx = strokeWidth
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                sigilTitleInput = "Sigil of ${intentionText.take(16)}"
                                isShowSaveDialog = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("save_sigil_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EnochianGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "Save Sigil")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Sigil", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Button(
                            onClick = {
                                val title = "Sigil of ${intentionText.take(16)}"
                                val exportedUri = SigilExportUtils.exportSigilToMediaStore(
                                    context = context,
                                    title = title,
                                    intentionPhrase = intentionText,
                                    sigilMethod = "Enochian Rose Wheel",
                                    lineColorHex = activeColorPair.second
                                )
                                if (exportedUri != null) {
                                    Toast.makeText(context, "Exported Sigil PNG to Pictures/EnochianSigils!", Toast.LENGTH_LONG).show()
                                } else {
                                    val defaultFileName = "sigil_${intentionText.take(8).lowercase()}.png"
                                    pendingExportParams = ExportSigilData(title, intentionText, "Enochian Rose Wheel", activeColorPair.second)
                                    customPngLauncher.launch(defaultFileName)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("export_sigil_png_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = EnochianGold
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldOutline)
                        ) {
                            Icon(Icons.Default.Image, contentDescription = "Export PNG", tint = EnochianGold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export PNG", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        } else {
            // Saved Sigils Library
            if (savedSigils.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Bookmark,
                            contentDescription = "Empty",
                            tint = EnochianGold.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No saved sigils yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Use the generator tab to trace and save custom Enochian sigils.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(savedSigils, key = { it.id }) { sigil ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, GoldOutline, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    EnochianWheelCanvas(
                                        phrase = sigil.originalPhrase,
                                        lineColor = parseHexColor(sigil.colorHex),
                                        strokeWidthPx = 4f
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = sigil.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = EnochianGold
                                    )
                                    Text(
                                        text = "Intention: ${sigil.originalPhrase}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Method: ${sigil.sigilMethod}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    IconButton(
                                        onClick = {
                                            val exportedUri = SigilExportUtils.exportSigilToMediaStore(
                                                context = context,
                                                title = sigil.title,
                                                intentionPhrase = sigil.originalPhrase,
                                                sigilMethod = sigil.sigilMethod,
                                                lineColorHex = sigil.colorHex
                                            )
                                            if (exportedUri != null) {
                                                Toast.makeText(context, "Exported Sigil PNG to Pictures/EnochianSigils!", Toast.LENGTH_LONG).show()
                                            } else {
                                                val defaultFileName = "sigil_${sigil.title.take(8).lowercase().replace(" ", "_")}.png"
                                                pendingExportParams = ExportSigilData(sigil.title, sigil.originalPhrase, sigil.sigilMethod, sigil.colorHex)
                                                customPngLauncher.launch(defaultFileName)
                                            }
                                        },
                                        modifier = Modifier.testTag("export_saved_sigil_${sigil.id}")
                                    ) {
                                        Icon(
                                            Icons.Default.Download,
                                            contentDescription = "Export PNG",
                                            tint = EnochianGold
                                        )
                                    }

                                    IconButton(
                                        onClick = { onDeleteSigil(sigil.id) }
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = ElementalRed
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Save Dialog
    if (isShowSaveDialog) {
        AlertDialog(
            onDismissRequest = { isShowSaveDialog = false },
            title = { Text("Save Custom Sigil", color = EnochianGold, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter a title for your saved sigil in your grimoire library:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = sigilTitleInput,
                        onValueChange = { sigilTitleInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EnochianGold,
                            unfocusedBorderColor = GoldOutline
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSaveSigil(
                            sigilTitleInput,
                            intentionText,
                            intentionText,
                            "Enochian Rose Wheel",
                            "",
                            activeColorPair.second
                        )
                        isShowSaveDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EnochianGold, contentColor = Color.Black)
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { isShowSaveDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun EnochianWheelCanvas(
    phrase: String,
    lineColor: Color,
    strokeWidthPx: Float
) {
    val letters = EnochianData.ENNOCHIAN_LETTERS

    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val outerRadius = (size.width.coerceAtMost(size.height) / 2f) * 0.85f
        val innerRadius = outerRadius * 0.45f

        // Draw concentric wheel circles
        drawCircle(
            color = GoldOutline.copy(alpha = 0.4f),
            radius = outerRadius,
            center = Offset(centerX, centerY),
            style = Stroke(width = 2f)
        )
        drawCircle(
            color = GoldOutline.copy(alpha = 0.3f),
            radius = innerRadius,
            center = Offset(centerX, centerY),
            style = Stroke(width = 1.5f)
        )

        // Map letters onto wheel nodes
        val nodeOffsets = mutableMapOf<Char, Offset>()

        letters.forEachIndexed { index, letter ->
            val angleRad = Math.toRadians(letter.wheelAngleDegrees.toDouble() - 90.0)
            val radius = if (index % 2 == 0) outerRadius else innerRadius
            val x = centerX + (radius * cos(angleRad)).toFloat()
            val y = centerY + (radius * sin(angleRad)).toFloat()

            val nodeOffset = Offset(x, y)
            nodeOffsets[letter.englishChar] = nodeOffset

            // Draw small wheel node
            drawCircle(
                color = EnochianGold.copy(alpha = 0.5f),
                radius = 4f,
                center = nodeOffset
            )
        }

        // Trace intention phrase path across nodes
        val cleanPhrase = phrase.uppercase().filter { it in 'A'..'Z' }
        if (cleanPhrase.isNotEmpty()) {
            val path = Path()
            val matchedPoints = mutableListOf<Offset>()

            cleanPhrase.forEach { char ->
                nodeOffsets[char]?.let { point ->
                    matchedPoints.add(point)
                }
            }

            if (matchedPoints.isNotEmpty()) {
                path.moveTo(matchedPoints.first().x, matchedPoints.first().y)

                for (i in 1 until matchedPoints.size) {
                    path.lineTo(matchedPoints[i].x, matchedPoints[i].y)
                }

                // Draw glowing path line
                drawPath(
                    path = path,
                    color = lineColor,
                    style = Stroke(
                        width = strokeWidthPx,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // Start node ring
                drawCircle(
                    color = lineColor,
                    radius = strokeWidthPx * 1.8f,
                    center = matchedPoints.first(),
                    style = Stroke(width = 2f)
                )

                // End node terminal cross/bullet
                drawCircle(
                    color = lineColor,
                    radius = strokeWidthPx * 1.2f,
                    center = matchedPoints.last()
                )
            }
        }
    }
}

fun parseHexColor(hex: String): Color {
    return try {
        val colorInt = android.graphics.Color.parseColor(hex)
        Color(colorInt)
    } catch (e: Exception) {
        EnochianGold
    }
}
