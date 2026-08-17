package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JournalEntry
import com.example.data.reference.EnochianData
import com.example.ui.theme.CelestialCyan
import com.example.ui.theme.ElementalGreen
import com.example.ui.theme.ElementalRed
import com.example.ui.theme.EnochianGold
import com.example.ui.theme.EnochianGoldLight
import com.example.ui.theme.GoldOutline
import com.example.ui.theme.MysticViolet
import com.example.utils.EsotericUtils
import com.example.utils.PdfExportUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import com.example.data.model.RitualSentimentAnalysisResult
import com.example.data.model.EntrySentimentSummary

@Composable
fun RitualJournalScreen(
    journalEntries: List<JournalEntry>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSyncingCloud: Boolean,
    lastCloudSyncTime: Long?,
    onTriggerCloudSync: () -> Unit,
    isAnalyzingSentiment: Boolean = false,
    sentimentAnalysisResult: RitualSentimentAnalysisResult? = null,
    onAnalyzeSentiments: () -> Unit = {},
    onSaveJournalEntry: (String, String, String, String, String, String, String, Int, String) -> Unit,
    onDeleteJournalEntry: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val pdfExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { os ->
                    val success = PdfExportUtils.writeJournalPdfToStream(journalEntries, os)
                    if (success) {
                        Toast.makeText(context, "Ritual Journal PDF exported successfully!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Failed to generate PDF document.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Export error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    val availableMoods = remember {
        listOf("All Moods", "Exalted 🌟", "Focused 👁️", "Mystic ⚡", "Purified 🌿", "Reflective 🌒", "Serene 🕯️", "Tranquil 🌌")
    }

    var selectedMoodFilter by remember { mutableStateOf("All Moods") }
    var selectedMoodForNewEntry by remember { mutableStateOf("Serene 🕯️") }

    // State for the inline "New entry" box below search box
    var inlineOutcomeNotes by remember { mutableStateOf("") }
    var inlineInsights by remember { mutableStateOf("") }
    var inlineMood by remember { mutableStateOf("Serene 🕯️") }

    var isShowNewEntryDialog by remember { mutableStateOf(false) }

    var entryTitle by remember { mutableStateOf("") }
    var selectedCall by remember { mutableStateOf(EnochianData.CALLS.first().title) }
    var planetaryHour by remember { mutableStateOf(EsotericUtils.getCurrentPlanetaryHour()) }
    var moonPhase by remember { mutableStateOf(EsotericUtils.getCurrentMoonPhase()) }
    var intention by remember { mutableStateOf("") }
    var outcomeNotes by remember { mutableStateOf("") }
    var insights by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(5) }

    var isCallDropdownExpanded by remember { mutableStateOf(false) }

    val sdfShort = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val sdfWithTime = remember { SimpleDateFormat("yyyy-MM-dd, HH:mm:ss", Locale.getDefault()) }
    val sdfIso = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val sdfUs = remember { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()) }
    val sdfMonthFull = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }
    val sdfMonthShort = remember { SimpleDateFormat("MMM yyyy", Locale.getDefault()) }

    val filteredEntries = journalEntries.filter { entry ->
        val dateObj = Date(entry.timestamp)
        val formattedShort = sdfShort.format(dateObj)
        val formattedWithTime = sdfWithTime.format(dateObj)
        val formattedIso = sdfIso.format(dateObj)
        val formattedUs = sdfUs.format(dateObj)
        val formattedMonthFull = sdfMonthFull.format(dateObj)
        val formattedMonthShort = sdfMonthShort.format(dateObj)

        val matchesSearch = searchQuery.isBlank() ||
                entry.title.contains(searchQuery, ignoreCase = true) ||
                entry.intention.contains(searchQuery, ignoreCase = true) ||
                entry.outcomeNotes.contains(searchQuery, ignoreCase = true) ||
                entry.insights.contains(searchQuery, ignoreCase = true) ||
                entry.keyOrCallUsed.contains(searchQuery, ignoreCase = true) ||
                entry.mood.contains(searchQuery, ignoreCase = true) ||
                formattedShort.contains(searchQuery, ignoreCase = true) ||
                formattedWithTime.contains(searchQuery, ignoreCase = true) ||
                formattedIso.contains(searchQuery, ignoreCase = true) ||
                formattedUs.contains(searchQuery, ignoreCase = true) ||
                formattedMonthFull.contains(searchQuery, ignoreCase = true) ||
                formattedMonthShort.contains(searchQuery, ignoreCase = true)

        val matchesMood = selectedMoodFilter == "All Moods" || entry.mood == selectedMoodFilter

        matchesSearch && matchesMood
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Ritual Outcome Journal",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = EnochianGold
                    )
                    Text(
                        text = "Secure cloud-synced grimoire of insights & outcomes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(EnochianGold)
                        .clickable { isShowNewEntryDialog = true }
                        .padding(12.dp)
                        .testTag("add_journal_entry_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Entry", tint = Color.Black)
                }
            }
        }

        item {
            // Search Bar at Top
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth().testTag("journal_search_input"),
                placeholder = { Text("Filter entries by title, date (e.g. 2026-08-09, Aug 09), mood...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = EnochianGold) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EnochianGold,
                    unfocusedBorderColor = GoldOutline,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        item {
            // "New entry" Box below search box
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("new_entry_box_card")
                    .border(1.5.dp, EnochianGold, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "New Entry",
                                tint = EnochianGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "New Entry",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = EnochianGold
                            )
                        }

                        // Automatically generated Date of Ritual in YYYY-MM-DD, HH:MM:SS
                        val ritualDateFormatted = formatDateWithTime(System.currentTimeMillis())
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
                                .border(1.dp, GoldOutline, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Date: $ritualDateFormatted",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EnochianGold
                            )
                        }
                    }

                    OutlinedTextField(
                        value = inlineOutcomeNotes,
                        onValueChange = { inlineOutcomeNotes = it },
                        modifier = Modifier.fillMaxWidth().testTag("inline_outcome_input"),
                        label = { Text("Ritual Outcome & Observations") },
                        placeholder = { Text("Describe ritual manifestations, elemental shifts, or results...") },
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EnochianGold,
                            unfocusedBorderColor = GoldOutline
                        ),
                        supportingText = {
                            Text(
                                text = "Words: ${inlineOutcomeNotes.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )

                    OutlinedTextField(
                        value = inlineInsights,
                        onValueChange = { inlineInsights = it },
                        modifier = Modifier.fillMaxWidth().testTag("inline_insights_input"),
                        label = { Text("Spiritual Insights & Revelations") },
                        placeholder = { Text("Record esoteric interpretations, inner reflections...") },
                        minLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EnochianGold,
                            unfocusedBorderColor = GoldOutline
                        ),
                        supportingText = {
                            Text(
                                text = "Words: ${inlineInsights.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )

                    // Mood Selector
                    Column {
                        Text(
                            text = "Ritual Mood & State:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EnochianGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(availableMoods.filter { it != "All Moods" }) { moodOption ->
                                FilterChip(
                                    selected = inlineMood == moodOption,
                                    onClick = { inlineMood = moodOption },
                                    label = { Text(moodOption, fontSize = 11.sp) },
                                    modifier = Modifier.testTag("inline_mood_${moodOption.replace(" ", "_")}"),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EnochianGold,
                                        selectedLabelColor = Color.Black,
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        labelColor = MaterialTheme.colorScheme.onSurface
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderColor = GoldOutline,
                                        selectedBorderColor = EnochianGold,
                                        enabled = true,
                                        selected = inlineMood == moodOption
                                    )
                                )
                            }
                        }
                    }

                    // Save Entry Button
                    Button(
                        onClick = {
                            if (inlineOutcomeNotes.isNotBlank() || inlineInsights.isNotBlank()) {
                                val computedTitle = if (inlineOutcomeNotes.length > 30) {
                                    inlineOutcomeNotes.take(30) + "..."
                                } else if (inlineOutcomeNotes.isNotBlank()) {
                                    inlineOutcomeNotes
                                } else {
                                    "Ritual Working"
                                }

                                onSaveJournalEntry(
                                    computedTitle,
                                    EnochianData.CALLS.first().title,
                                    EsotericUtils.getCurrentPlanetaryHour(),
                                    EsotericUtils.getCurrentMoonPhase(),
                                    "Ritual Outcome Logging",
                                    inlineOutcomeNotes,
                                    inlineInsights,
                                    5,
                                    inlineMood
                                )

                                inlineOutcomeNotes = ""
                                inlineInsights = ""
                                inlineMood = "Serene 🕯️"
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("save_new_entry_box_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = EnochianGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save to Ritual Log", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            // Cloud Sync Status Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldOutline, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Encrypted",
                            tint = EnochianGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Cloud Backup: Encrypted Active",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (lastCloudSyncTime != null) "Last synced: ${formatDateShort(lastCloudSyncTime)}" else "Sync pending",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (isSyncingCloud) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = EnochianGold,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { onTriggerCloudSync() }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("sync_cloud_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CloudSync, contentDescription = "Sync", tint = EnochianGold, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sync Now", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EnochianGold)
                            }
                        }
                    }
                }
            }
        }

        item {
            // PDF Export Document Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("export_pdf_card")
                    .border(1.dp, GoldOutline, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "PDF Export",
                            tint = EnochianGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Export Journal as PDF",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Formatted grimoire document (${journalEntries.size} entries)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Export via Document Picker
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(EnochianGold)
                            .clickable {
                                if (journalEntries.isEmpty()) {
                                    Toast.makeText(context, "No ritual journal entries to export.", Toast.LENGTH_SHORT).show()
                                } else {
                                    val defaultFileName = "ritual_journal_${SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())}.pdf"
                                    pdfExportLauncher.launch(defaultFileName)
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("export_pdf_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = "Export PDF", tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }
        }

        item {
            // Gemini AI Sentiment Analysis Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldOutline, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MysticViolet.copy(alpha = 0.25f))
                                    .border(1.dp, MysticViolet, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = "Gemini AI Sentiment",
                                    tint = EnochianGold,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Gemini Emotional Sentiment AI",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = EnochianGold
                                )
                                Text(
                                    text = "Parses ritual logs & tracks psychological growth",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = onAnalyzeSentiments,
                            enabled = !isAnalyzingSentiment && journalEntries.isNotEmpty(),
                            modifier = Modifier.testTag("analyze_sentiment_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MysticViolet,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            if (isAnalyzingSentiment) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Analyze", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    if (isAnalyzingSentiment) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = EnochianGold, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Gemini is analyzing your spiritual trajectory...",
                                    fontSize = 12.sp,
                                    color = EnochianGold
                                )
                            }
                        }
                    }

                    if (sentimentAnalysisResult != null && !isAnalyzingSentiment) {
                        Spacer(modifier = Modifier.height(14.dp))

                        // Resonance Score & Mindstate Banner
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                                .border(1.dp, GoldOutline, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "DOMINANT SPIRITUAL STATE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = sentimentAnalysisResult.dominantState,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EnochianGold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = sentimentAnalysisResult.progressTrend,
                                    fontSize = 11.sp,
                                    color = CelestialCyan
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(EnochianGold.copy(alpha = 0.15f))
                                    .border(1.5.dp, EnochianGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${sentimentAnalysisResult.overallScore}%",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EnochianGold
                                    )
                                    Text(
                                        text = "Score",
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Emotional Dimension Meters
                        Text(
                            text = "EMOTIONAL DIMENSION METERS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EnochianGold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            SentimentMeter(label = "Devotion & Focus", percentage = sentimentAnalysisResult.devotionPercent, color = EnochianGold)
                            SentimentMeter(label = "Clarity & Vision", percentage = sentimentAnalysisResult.clarityPercent, color = CelestialCyan)
                            SentimentMeter(label = "Tranquility & Peace", percentage = sentimentAnalysisResult.tranquilityPercent, color = ElementalGreen)
                            SentimentMeter(label = "Spiritual Intensity", percentage = sentimentAnalysisResult.intensityPercent, color = MysticViolet)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Emotional Progress Chart Visualizer
                        EmotionalProgressChart(
                            journalSentiments = sentimentAnalysisResult.journalSentiments,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Gemini Summary
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MysticViolet.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                .border(1.dp, MysticViolet, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Psychology, contentDescription = null, tint = MysticViolet, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "GEMINI ESOTERIC TRAJECTORY SUMMARY",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MysticViolet
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = sentimentAnalysisResult.esotericSummary,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "RECOMMENDED NEXT PRACTICE:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EnochianGold
                                )
                                Text(
                                    text = sentimentAnalysisResult.recommendedNextWorking,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CelestialCyan
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            // M3 Filters: Mood & Category Tags
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Mood Filter
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = EnochianGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Filter by Mood:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EnochianGold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(availableMoods) { moodOption ->
                            FilterChip(
                                selected = selectedMoodFilter == moodOption,
                                onClick = { selectedMoodFilter = moodOption },
                                label = {
                                    Text(
                                        text = moodOption,
                                        fontSize = 11.sp,
                                        fontWeight = if (selectedMoodFilter == moodOption) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                modifier = Modifier.testTag("mood_filter_${moodOption.replace(" ", "_")}"),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EnochianGold,
                                    selectedLabelColor = Color.Black,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = MaterialTheme.colorScheme.onSurface
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = GoldOutline,
                                    selectedBorderColor = EnochianGold,
                                    enabled = true,
                                    selected = selectedMoodFilter == moodOption
                                )
                            )
                        }
                    }
                }
            }
        }

        // Journal List
        if (filteredEntries.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = "Empty",
                            tint = EnochianGold.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (journalEntries.isEmpty()) "Your Grimoire Journal is empty" else "No matching entries found",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (journalEntries.isEmpty()) "Tap the '+' button to log ritual outcomes, planetary hours, and spiritual insights." else "Try adjusting your search query or mood filter.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredEntries, key = { it.id }) { entry ->
                val matchedSentiment = sentimentAnalysisResult?.journalSentiments?.find { it.journalId == entry.id }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GoldOutline, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = entry.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = EnochianGold
                            )

                            IconButton(onClick = { onDeleteJournalEntry(entry.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ElementalRed)
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = entry.keyOrCallUsed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MysticViolet,
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                            Text(
                                text = "${entry.moonPhase} • ${entry.planetaryHour}",
                                fontSize = 11.sp,
                                color = EnochianGoldLight
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // M3 Chips for Mood & Timestamp
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AssistChip(
                                onClick = { },
                                label = { Text(entry.mood, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.SentimentSatisfiedAlt,
                                        contentDescription = "Mood",
                                        tint = EnochianGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = EnochianGold
                                ),
                                border = AssistChipDefaults.assistChipBorder(borderColor = GoldOutline, enabled = true)
                            )

                            AssistChip(
                                onClick = { },
                                label = { Text(formatDateWithTime(entry.timestamp), fontSize = 10.sp) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Schedule,
                                        contentDescription = "Timestamp",
                                        tint = CelestialCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                border = AssistChipDefaults.assistChipBorder(borderColor = GoldOutline, enabled = true)
                            )
                        }

                        if (matchedSentiment != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(MysticViolet.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                    .border(1.dp, MysticViolet, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = MysticViolet,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Gemini Sentiment: ${matchedSentiment.sentimentTag} (${matchedSentiment.sentimentScore}%) • ${matchedSentiment.emotionalTone}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MysticViolet
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Intention: ${entry.intention}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Outcome & Reflections:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EnochianGold
                        )
                        Text(
                            text = entry.outcomeNotes,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (entry.insights.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Spiritual Insights:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CelestialCyan
                            )
                            Text(
                                text = entry.insights,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row {
                                for (i in 1..5) {
                                    Icon(
                                        imageVector = if (i <= entry.rating) Icons.Default.Star else Icons.Default.StarOutline,
                                        contentDescription = "Rating",
                                        tint = EnochianGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Text(
                                text = formatDateShort(entry.timestamp),
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // New Entry Dialog
    if (isShowNewEntryDialog) {
        AlertDialog(
            onDismissRequest = { isShowNewEntryDialog = false },
            title = { Text("Log Ritual Outcome", color = EnochianGold, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = entryTitle,
                            onValueChange = { entryTitle = it },
                            modifier = Modifier.fillMaxWidth().testTag("journal_title_input"),
                            label = { Text("Ritual Title") },
                            placeholder = { Text("e.g. Invocation of Bataivah") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EnochianGold,
                                unfocusedBorderColor = GoldOutline
                            )
                        )
                    }

                    item {
                        Text("Key / Call Invoked:", fontSize = 12.sp, color = EnochianGold)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, GoldOutline, RoundedCornerShape(8.dp))
                                .clickable { isCallDropdownExpanded = true }
                                .padding(12.dp)
                        ) {
                            Text(selectedCall, fontSize = 13.sp)
                            DropdownMenu(
                                expanded = isCallDropdownExpanded,
                                onDismissRequest = { isCallDropdownExpanded = false }
                            ) {
                                EnochianData.CALLS.forEach { call ->
                                    DropdownMenuItem(
                                        text = { Text(call.title) },
                                        onClick = {
                                            selectedCall = call.title
                                            isCallDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = planetaryHour,
                                onValueChange = { planetaryHour = it },
                                modifier = Modifier.weight(1f),
                                label = { Text("Planetary Hour") },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EnochianGold, unfocusedBorderColor = GoldOutline)
                            )
                            OutlinedTextField(
                                value = moonPhase,
                                onValueChange = { moonPhase = it },
                                modifier = Modifier.weight(1f),
                                label = { Text("Moon Phase") },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EnochianGold, unfocusedBorderColor = GoldOutline)
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = intention,
                            onValueChange = { intention = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Ritual Intention") },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EnochianGold, unfocusedBorderColor = GoldOutline)
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = outcomeNotes,
                            onValueChange = { outcomeNotes = it },
                            modifier = Modifier.fillMaxWidth().testTag("journal_outcome_input"),
                            label = { Text("Outcome & Observations") },
                            placeholder = { Text("Record results, feelings, sensory occurrences...") },
                            minLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EnochianGold, unfocusedBorderColor = GoldOutline)
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = insights,
                            onValueChange = { insights = it },
                            modifier = Modifier.fillMaxWidth().testTag("journal_insights_input"),
                            label = { Text("Spiritual Insights & Visions") },
                            placeholder = { Text("Record esoteric insights, visions...") },
                            minLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = EnochianGold, unfocusedBorderColor = GoldOutline)
                        )
                    }

                    item {
                        Text("Ritual Mood & Emotional State:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EnochianGold)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(availableMoods.filter { it != "All Moods" }) { moodOption ->
                                FilterChip(
                                    selected = selectedMoodForNewEntry == moodOption,
                                    onClick = { selectedMoodForNewEntry = moodOption },
                                    label = { Text(moodOption, fontSize = 11.sp) },
                                    modifier = Modifier.testTag("new_entry_mood_${moodOption.replace(" ", "_")}"),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EnochianGold,
                                        selectedLabelColor = Color.Black,
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        labelColor = MaterialTheme.colorScheme.onSurface
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        borderColor = GoldOutline,
                                        selectedBorderColor = EnochianGold,
                                        enabled = true,
                                        selected = selectedMoodForNewEntry == moodOption
                                    )
                                )
                            }
                        }
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                .border(1.dp, GoldOutline, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccessTime, contentDescription = "Timestamp", tint = EnochianGold, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Logged Timestamp:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EnochianGold)
                                }
                                Text(
                                    text = formatDateWithTime(System.currentTimeMillis()),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    item {
                        Text("Ritual Resonance Rating (1-5 Stars):", fontSize = 12.sp, color = EnochianGold)
                        Row {
                            for (i in 1..5) {
                                IconButton(onClick = { rating = i }) {
                                    Icon(
                                        imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarOutline,
                                        contentDescription = "Star $i",
                                        tint = EnochianGold
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (entryTitle.isNotBlank()) {
                            onSaveJournalEntry(
                                entryTitle,
                                selectedCall,
                                planetaryHour,
                                moonPhase,
                                intention,
                                outcomeNotes,
                                insights,
                                rating,
                                selectedMoodForNewEntry
                            )
                            entryTitle = ""
                            intention = ""
                            outcomeNotes = ""
                            insights = ""
                            selectedMoodForNewEntry = "Serene 🕯️"
                            isShowNewEntryDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_journal_entry_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = EnochianGold, contentColor = Color.Black)
                ) {
                    Text("Save to Grimoire", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { isShowNewEntryDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

fun formatDateShort(millis: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(millis))
}

fun formatDateWithTime(millis: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd, HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(millis))
}

@Composable
fun SentimentMeter(
    label: String,
    percentage: Int,
    color: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(text = "$percentage%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth((percentage.coerceIn(0, 100)) / 100f)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
fun EmotionalProgressChart(
    journalSentiments: List<EntrySentimentSummary>,
    modifier: Modifier = Modifier
) {
    if (journalSentiments.isEmpty()) return

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ShowChart,
                    contentDescription = null,
                    tint = EnochianGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "EMOTIONAL TRAJECTORY VISUALIZER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EnochianGold
                )
            }
            Text(
                text = "${journalSentiments.size} Rituals Charted",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                .border(1.dp, GoldOutline, RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            val width = size.width
            val height = size.height

            // Horizontal grid lines
            for (i in 0..2) {
                val y = height * (i / 2f)
                drawLine(
                    color = GoldOutline.copy(alpha = 0.25f),
                    start = androidx.compose.ui.geometry.Offset(0f, y),
                    end = androidx.compose.ui.geometry.Offset(width, y),
                    strokeWidth = 1f
                )
            }

            val count = journalSentiments.size
            val spacing = if (count > 1) width / (count - 1) else width / 2f

            val points = mutableListOf<androidx.compose.ui.geometry.Offset>()

            val reversedSentiments = journalSentiments.reversed()

            reversedSentiments.forEachIndexed { index, item ->
                val x = if (count == 1) width / 2f else index * spacing
                val scoreNorm = (item.sentimentScore / 100f).coerceIn(0.1f, 1.0f)
                val y = height * (1f - scoreNorm)
                points.add(androidx.compose.ui.geometry.Offset(x, y))

                // Point marker
                drawCircle(
                    color = when {
                        item.sentimentScore >= 80 -> EnochianGold
                        item.sentimentScore >= 60 -> CelestialCyan
                        else -> MysticViolet
                    },
                    radius = 5.dp.toPx(),
                    center = androidx.compose.ui.geometry.Offset(x, y)
                )

                drawCircle(
                    color = Color.White,
                    radius = 2.dp.toPx(),
                    center = androidx.compose.ui.geometry.Offset(x, y)
                )
            }

            // Connecting trend lines
            if (points.size > 1) {
                for (i in 0 until points.size - 1) {
                    drawLine(
                        color = EnochianGoldLight,
                        start = points[i],
                        end = points[i + 1],
                        strokeWidth = 2.5.dp.toPx()
                    )
                }
            }
        }
    }
}

