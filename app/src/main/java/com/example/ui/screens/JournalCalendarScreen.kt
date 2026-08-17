package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JournalEntry
import com.example.data.reference.EnochianData
import com.example.ui.theme.CelestialCyan
import com.example.ui.theme.ElementalRed
import com.example.ui.theme.EnochianGold
import com.example.ui.theme.EnochianGoldLight
import com.example.ui.theme.GoldOutline
import com.example.ui.theme.MysticViolet
import com.example.utils.EsotericUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

enum class CalendarViewMode(val label: String) {
    DAY("Day"),
    WEEK("Week"),
    MONTH("Month")
}

@Composable
fun JournalCalendarScreen(
    journalEntries: List<JournalEntry>,
    onSaveJournalEntry: (
        title: String,
        keyOrCallUsed: String,
        planetaryHour: String,
        moonPhase: String,
        intention: String,
        outcomeNotes: String,
        insights: String,
        rating: Int,
        mood: String
    ) -> Unit,
    onDeleteJournalEntry: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Core Calendar State
    var selectedCalendar by remember { mutableStateOf(Calendar.getInstance()) }
    var activeViewMode by remember { mutableStateOf(CalendarViewMode.MONTH) }

    // Dialog state for Export .ics
    var showExportIcsDialog by remember { mutableStateOf(false) }

    // Dialog state for adding a new entry on the selected date
    var showAddEntryDialog by remember { mutableStateOf(false) }

    // ICS File Save Launcher
    val exportIcsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/calendar")
    ) { uri ->
        uri?.let { targetUri ->
            try {
                val csData = generateIcsString(journalEntries)
                context.contentResolver.openOutputStream(targetUri)?.use { output ->
                    output.write(csData.toByteArray())
                }
                Toast.makeText(context, "Exported .ics calendar successfully!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error exporting .ics: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val todayCalendar = Calendar.getInstance()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Screen Title Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Journal Calendar",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = EnochianGold
                )
                Text(
                    text = "Quick view by date & .ics schedule export",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Export .ics Button
                Button(
                    onClick = { showExportIcsDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = EnochianGold, contentColor = Color.Black),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("export_ics_button")
                ) {
                    Icon(Icons.Default.Download, contentDescription = "Export .ics", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ICS", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Segmented Control for View Mode (Day, Week, Month)
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            CalendarViewMode.entries.forEachIndexed { index, mode ->
                SegmentedButton(
                    selected = activeViewMode == mode,
                    onClick = { activeViewMode = mode },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = CalendarViewMode.entries.size),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = EnochianGold,
                        activeContentColor = Color.Black,
                        inactiveContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("calendar_view_${mode.name.lowercase()}")
                ) {
                    Text(mode.label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Date Stepper & Today Action Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                .border(1.dp, GoldOutline, RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Previous Time Step Button
                IconButton(
                    onClick = {
                        val newCal = selectedCalendar.clone() as Calendar
                        when (activeViewMode) {
                            CalendarViewMode.DAY -> newCal.add(Calendar.DAY_OF_MONTH, -1)
                            CalendarViewMode.WEEK -> newCal.add(Calendar.WEEK_OF_YEAR, -1)
                            CalendarViewMode.MONTH -> newCal.add(Calendar.MONTH, -1)
                        }
                        selectedCalendar = newCal
                    },
                    modifier = Modifier.testTag("calendar_prev_button")
                ) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous", tint = EnochianGold)
                }

                // Current Date Label
                Text(
                    text = getFormattedHeaderDate(selectedCalendar, activeViewMode),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = EnochianGold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                // Next Time Step Button
                IconButton(
                    onClick = {
                        val newCal = selectedCalendar.clone() as Calendar
                        when (activeViewMode) {
                            CalendarViewMode.DAY -> newCal.add(Calendar.DAY_OF_MONTH, 1)
                            CalendarViewMode.WEEK -> newCal.add(Calendar.WEEK_OF_YEAR, 1)
                            CalendarViewMode.MONTH -> newCal.add(Calendar.MONTH, 1)
                        }
                        selectedCalendar = newCal
                    },
                    modifier = Modifier.testTag("calendar_next_button")
                ) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next", tint = EnochianGold)
                }
            }

            // Reset to "Today" Action Button
            AssistChip(
                onClick = { selectedCalendar = Calendar.getInstance() },
                label = { Text("Today", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                leadingIcon = {
                    Icon(Icons.Default.Today, contentDescription = "Today", tint = EnochianGold, modifier = Modifier.size(14.dp))
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = EnochianGold
                ),
                border = AssistChipDefaults.assistChipBorder(borderColor = GoldOutline, enabled = true),
                modifier = Modifier.testTag("calendar_today_button")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Active View Mode Rendering
        when (activeViewMode) {
            CalendarViewMode.DAY -> {
                DayCalendarView(
                    selectedCalendar = selectedCalendar,
                    journalEntries = journalEntries,
                    onDeleteJournalEntry = onDeleteJournalEntry,
                    onOpenAddEntry = { showAddEntryDialog = true }
                )
            }
            CalendarViewMode.WEEK -> {
                WeekCalendarView(
                    selectedCalendar = selectedCalendar,
                    onSelectDate = { newCal -> selectedCalendar = newCal },
                    journalEntries = journalEntries,
                    onDeleteJournalEntry = onDeleteJournalEntry,
                    onOpenAddEntry = { showAddEntryDialog = true }
                )
            }
            CalendarViewMode.MONTH -> {
                MonthCalendarView(
                    selectedCalendar = selectedCalendar,
                    todayCalendar = todayCalendar,
                    onSelectDate = { newCal -> selectedCalendar = newCal },
                    journalEntries = journalEntries,
                    onDeleteJournalEntry = onDeleteJournalEntry,
                    onOpenAddEntry = { showAddEntryDialog = true }
                )
            }
        }
    }

    // Export .ics Dialog
    if (showExportIcsDialog) {
        ExportIcsDialog(
            journalEntries = journalEntries,
            onDismiss = { showExportIcsDialog = false },
            onExportToFile = {
                showExportIcsDialog = false
                val defaultFileName = "enochian_journal_calendar_${SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())}.ics"
                exportIcsLauncher.launch(defaultFileName)
            }
        )
    }

    // Add Entry Dialog for Selected Date
    if (showAddEntryDialog) {
        AddEntryForDateDialog(
            targetCalendar = selectedCalendar,
            onDismiss = { showAddEntryDialog = false },
            onSave = { title, call, planet, moon, intent, outcome, insights, rating, mood ->
                onSaveJournalEntry(title, call, planet, moon, intent, outcome, insights, rating, mood)
                showAddEntryDialog = false
            }
        )
    }
}

// ==================== DAY VIEW ====================

@Composable
fun DayCalendarView(
    selectedCalendar: Calendar,
    journalEntries: List<JournalEntry>,
    onDeleteJournalEntry: (Long) -> Unit,
    onOpenAddEntry: () -> Unit
) {
    val entriesForDay = remember(selectedCalendar, journalEntries) {
        filterEntriesForDay(journalEntries, selectedCalendar)
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rituals logged on ${formatDateFull(selectedCalendar.timeInMillis)} (${entriesForDay.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = EnochianGold
                )

                Button(
                    onClick = onOpenAddEntry,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = EnochianGold),
                    modifier = Modifier.testTag("day_add_entry_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Entry", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (entriesForDay.isEmpty()) {
            item {
                EmptyDateStateBox(
                    dateLabel = formatDateFull(selectedCalendar.timeInMillis),
                    onOpenAddEntry = onOpenAddEntry
                )
            }
        } else {
            items(entriesForDay, key = { it.id }) { entry ->
                JournalEntryCalendarCard(
                    entry = entry,
                    onDeleteJournalEntry = onDeleteJournalEntry
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ==================== WEEK VIEW ====================

@Composable
fun WeekCalendarView(
    selectedCalendar: Calendar,
    onSelectDate: (Calendar) -> Unit,
    journalEntries: List<JournalEntry>,
    onDeleteJournalEntry: (Long) -> Unit,
    onOpenAddEntry: () -> Unit
) {
    val weekDays = remember(selectedCalendar) {
        get7DaysOfWeek(selectedCalendar)
    }

    val entriesForSelectedDay = remember(selectedCalendar, journalEntries) {
        filterEntriesForDay(journalEntries, selectedCalendar)
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Horizontal 7-Day Week Strip
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                weekDays.forEach { dayCal ->
                    val isSelected = isSameDay(dayCal, selectedCalendar)
                    val isToday = isSameDay(dayCal, Calendar.getInstance())
                    val dayEntriesCount = filterEntriesForDay(journalEntries, dayCal).size

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) EnochianGold else GoldOutline.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { onSelectDate(dayCal) }
                            .testTag("week_day_card_${dayCal.get(Calendar.DAY_OF_MONTH)}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) EnochianGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = SimpleDateFormat("EEE", Locale.getDefault()).format(dayCal.time).uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) EnochianGold else MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isToday) EnochianGold else Color.Transparent
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${dayCal.get(Calendar.DAY_OF_MONTH)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isToday) Color.Black else (if (isSelected) EnochianGold else MaterialTheme.colorScheme.onSurface)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Indicator dots for entries count
                            if (dayEntriesCount > 0) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    repeat(dayEntriesCount.coerceAtMost(3)) {
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(EnochianGold)
                                        )
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rituals for ${formatDateShort(selectedCalendar.timeInMillis)} (${entriesForSelectedDay.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = EnochianGold
                )

                Button(
                    onClick = onOpenAddEntry,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = EnochianGold),
                    modifier = Modifier.testTag("week_add_entry_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Entry", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (entriesForSelectedDay.isEmpty()) {
            item {
                EmptyDateStateBox(
                    dateLabel = formatDateShort(selectedCalendar.timeInMillis),
                    onOpenAddEntry = onOpenAddEntry
                )
            }
        } else {
            items(entriesForSelectedDay, key = { it.id }) { entry ->
                JournalEntryCalendarCard(
                    entry = entry,
                    onDeleteJournalEntry = onDeleteJournalEntry
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ==================== MONTH VIEW ====================

@Composable
fun MonthCalendarView(
    selectedCalendar: Calendar,
    todayCalendar: Calendar,
    onSelectDate: (Calendar) -> Unit,
    journalEntries: List<JournalEntry>,
    onDeleteJournalEntry: (Long) -> Unit,
    onOpenAddEntry: () -> Unit
) {
    val monthGridDays = remember(selectedCalendar) {
        getMonthGrid42Days(selectedCalendar)
    }

    val entriesForSelectedDay = remember(selectedCalendar, journalEntries) {
        filterEntriesForDay(journalEntries, selectedCalendar)
    }

    val currentMonthIndex = selectedCalendar.get(Calendar.MONTH)

    Column(modifier = Modifier.fillMaxSize()) {
        // Weekday Name Headers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val daysOfWeekLabels = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            daysOfWeekLabels.forEach { dayLabel ->
                Text(
                    text = dayLabel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EnochianGold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // 42-Cell Month Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.height(240.dp)
        ) {
            items(monthGridDays) { dayCal ->
                val isSelected = isSameDay(dayCal, selectedCalendar)
                val isToday = isSameDay(dayCal, todayCalendar)
                val isCurrentMonth = dayCal.get(Calendar.MONTH) == currentMonthIndex
                val dayEntriesCount = filterEntriesForDay(journalEntries, dayCal).size

                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when {
                                isSelected -> EnochianGold.copy(alpha = 0.25f)
                                isToday -> MaterialTheme.colorScheme.surfaceVariant
                                else -> MaterialTheme.colorScheme.surface
                            }
                        )
                        .border(
                            width = if (isSelected) 1.5.dp else 0.5.dp,
                            color = if (isSelected) EnochianGold else GoldOutline.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onSelectDate(dayCal) }
                        .testTag("month_day_cell_${dayCal.get(Calendar.MONTH)}_${dayCal.get(Calendar.DAY_OF_MONTH)}"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${dayCal.get(Calendar.DAY_OF_MONTH)}",
                            fontSize = 12.sp,
                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                            color = when {
                                isToday -> EnochianGold
                                isSelected -> EnochianGold
                                isCurrentMonth -> MaterialTheme.colorScheme.onSurface
                                else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            }
                        )

                        if (dayEntriesCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(EnochianGold)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Selected Date Header & Journal Entries List
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Entries for ${formatDateShort(selectedCalendar.timeInMillis)} (${entriesForSelectedDay.size})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = EnochianGold
            )

            Button(
                onClick = onOpenAddEntry,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = EnochianGold),
                modifier = Modifier.testTag("month_add_entry_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Entry", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (entriesForSelectedDay.isEmpty()) {
            EmptyDateStateBox(
                dateLabel = formatDateShort(selectedCalendar.timeInMillis),
                onOpenAddEntry = onOpenAddEntry
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(entriesForSelectedDay, key = { it.id }) { entry ->
                    JournalEntryCalendarCard(
                        entry = entry,
                        onDeleteJournalEntry = onDeleteJournalEntry
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// ==================== SHARED UI COMPONENTS ====================

@Composable
fun JournalEntryCalendarCard(
    entry: JournalEntry,
    onDeleteJournalEntry: (Long) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GoldOutline, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = EnochianGold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = { onDeleteJournalEntry(entry.id) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ElementalRed, modifier = Modifier.size(18.dp))
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.keyOrCallUsed,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MysticViolet,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
                Text(
                    text = "${entry.moonPhase} • ${entry.planetaryHour}",
                    fontSize = 10.sp,
                    color = EnochianGoldLight
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = { },
                    label = { Text(entry.mood, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    leadingIcon = {
                        Icon(Icons.Default.SentimentSatisfiedAlt, contentDescription = null, tint = EnochianGold, modifier = Modifier.size(12.dp))
                    },
                    colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, labelColor = EnochianGold),
                    border = AssistChipDefaults.assistChipBorder(borderColor = GoldOutline, enabled = true)
                )

                AssistChip(
                    onClick = { },
                    label = { Text(formatTimeOnly(entry.timestamp), fontSize = 10.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = CelestialCyan, modifier = Modifier.size(12.dp))
                    },
                    colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, labelColor = MaterialTheme.colorScheme.onSurfaceVariant),
                    border = AssistChipDefaults.assistChipBorder(borderColor = GoldOutline, enabled = true)
                )
            }

            if (entry.outcomeNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Outcome: ${entry.outcomeNotes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (entry.insights.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Insights: ${entry.insights}",
                    style = MaterialTheme.typography.bodySmall,
                    color = CelestialCyan,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

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
                            modifier = Modifier.size(14.dp)
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

@Composable
fun EmptyDateStateBox(
    dateLabel: String,
    onOpenAddEntry: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GoldOutline.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.CalendarMonth,
                contentDescription = "No entries",
                tint = EnochianGold.copy(alpha = 0.5f),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No rituals recorded for $dateLabel",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tap below to log a new ritual working or insight for this day.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onOpenAddEntry,
                colors = ButtonDefaults.buttonColors(containerColor = EnochianGold, contentColor = Color.Black),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("empty_state_add_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Log Ritual Entry", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

// ==================== DIALOGS ====================

@Composable
fun ExportIcsDialog(
    journalEntries: List<JournalEntry>,
    onDismiss: () -> Unit,
    onExportToFile: () -> Unit
) {
    val context = LocalContext.current
    val icsData = remember(journalEntries) { generateIcsString(journalEntries) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = EnochianGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export iCalendar (.ics)", color = EnochianGold, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text(
                    text = "Export ${journalEntries.size} journal entries to standard .ics format compatible with Google Calendar, Apple Calendar, and Outlook.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        .border(1.dp, GoldOutline, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    LazyColumn {
                        item {
                            Text(
                                text = icsData,
                                fontSize = 10.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Copy to Clipboard
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Enochian Journal Calendar .ics", icsData)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Copied .ics to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy", fontSize = 11.sp)
                    }

                    // Share Intent
                    OutlinedButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/calendar"
                                putExtra(Intent.EXTRA_SUBJECT, "Enochian Grimoire Journal Schedule")
                                putExtra(Intent.EXTRA_TEXT, icsData)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Calendar Schedule"))
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share", fontSize = 11.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onExportToFile,
                colors = ButtonDefaults.buttonColors(containerColor = EnochianGold, contentColor = Color.Black),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save .ics File", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
fun AddEntryForDateDialog(
    targetCalendar: Calendar,
    onDismiss: () -> Unit,
    onSave: (title: String, call: String, planet: String, moon: String, intent: String, outcome: String, insights: String, rating: Int, mood: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCall by remember { mutableStateOf(EnochianData.CALLS.first().title) }
    var outcomeNotes by remember { mutableStateOf("") }
    var insights by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("Serene 🕯️") }

    val formattedDate = formatDateFull(targetCalendar.timeInMillis)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Log Ritual Entry for $formattedDate", color = EnochianGold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title / Working Name") },
                    placeholder = { Text("e.g. Scrying 1st Aethyr LIL") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = outcomeNotes,
                    onValueChange = { outcomeNotes = it },
                    label = { Text("Ritual Outcome") },
                    placeholder = { Text("Describe results & observations...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(8.dp))

                val insightsWordCount = if (insights.isBlank()) 0 else insights.trim().split(Regex("\\s+")).size
                OutlinedTextField(
                    value = insights,
                    onValueChange = { insights = it },
                    label = { Text("Insights") },
                    placeholder = { Text("Record spiritual revelations...") },
                    supportingText = {
                        Text(
                            text = "$insightsWordCount ${if (insightsWordCount == 1) "word" else "words"}",
                            fontSize = 11.sp,
                            color = EnochianGold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val computedTitle = if (title.isNotBlank()) title else if (outcomeNotes.length > 25) outcomeNotes.take(25) + "..." else "Ritual Working"
                    onSave(
                        computedTitle,
                        selectedCall,
                        EsotericUtils.getCurrentPlanetaryHour(),
                        EsotericUtils.getCurrentMoonPhase(),
                        "Calendar Scheduled Working",
                        outcomeNotes,
                        insights,
                        5,
                        selectedMood
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = EnochianGold, contentColor = Color.Black)
            ) {
                Text("Save Entry", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

// ==================== CALENDAR HELPERS ====================

fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
            cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
}

fun filterEntriesForDay(entries: List<JournalEntry>, targetCal: Calendar): List<JournalEntry> {
    val entryCal = Calendar.getInstance()
    return entries.filter { entry ->
        entryCal.timeInMillis = entry.timestamp
        isSameDay(entryCal, targetCal)
    }.sortedByDescending { it.timestamp }
}

fun get7DaysOfWeek(targetCal: Calendar): List<Calendar> {
    val cal = targetCal.clone() as Calendar
    cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
    val days = mutableListOf<Calendar>()
    for (i in 0..6) {
        days.add(cal.clone() as Calendar)
        cal.add(Calendar.DAY_OF_MONTH, 1)
    }
    return days
}

fun getMonthGrid42Days(targetCal: Calendar): List<Calendar> {
    val cal = targetCal.clone() as Calendar
    cal.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeekIndex = cal.get(Calendar.DAY_OF_WEEK) - 1 // 0-based index
    cal.add(Calendar.DAY_OF_MONTH, -firstDayOfWeekIndex)

    val gridDays = mutableListOf<Calendar>()
    for (i in 0..41) {
        gridDays.add(cal.clone() as Calendar)
        cal.add(Calendar.DAY_OF_MONTH, 1)
    }
    return gridDays
}

fun getFormattedHeaderDate(cal: Calendar, mode: CalendarViewMode): String {
    return when (mode) {
        CalendarViewMode.DAY -> SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault()).format(cal.time)
        CalendarViewMode.WEEK -> {
            val days = get7DaysOfWeek(cal)
            val start = SimpleDateFormat("MMM dd", Locale.getDefault()).format(days.first().time)
            val end = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(days.last().time)
            "$start – $end"
        }
        CalendarViewMode.MONTH -> SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.time)
    }
}

fun formatDateFull(millis: Long): String {
    return SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault()).format(Date(millis))
}

fun formatTimeOnly(millis: Long): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(millis))
}

fun generateIcsString(entries: List<JournalEntry>): String {
    val sb = StringBuilder()
    val utcFormat = SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    sb.append("BEGIN:VCALENDAR\r\n")
    sb.append("VERSION:2.0\r\n")
    sb.append("PRODID:-//Enochian Magic App//Journal Calendar//EN\r\n")
    sb.append("CALSCALE:GREGORIAN\r\n")
    sb.append("METHOD:PUBLISH\r\n")

    entries.forEach { entry ->
        val dtStamp = utcFormat.format(Date(entry.timestamp))
        val dtStart = dtStamp
        val dtEnd = utcFormat.format(Date(entry.timestamp + 3600000)) // 1 hour duration default

        val descriptionText = buildString {
            append("Key/Call: ${entry.keyOrCallUsed}\\n")
            append("Moon Phase: ${entry.moonPhase}\\n")
            append("Planetary Hour: ${entry.planetaryHour}\\n")
            if (entry.outcomeNotes.isNotBlank()) append("Outcome: ${escapeIcsText(entry.outcomeNotes)}\\n")
            if (entry.insights.isNotBlank()) append("Insights: ${escapeIcsText(entry.insights)}\\n")
            append("Mood: ${escapeIcsText(entry.mood)}")
        }

        sb.append("BEGIN:VEVENT\r\n")
        sb.append("UID:journal-${entry.id}-${entry.timestamp}@enochianmagic.app\r\n")
        sb.append("DTSTAMP:$dtStamp\r\n")
        sb.append("DTSTART:$dtStart\r\n")
        sb.append("DTEND:$dtEnd\r\n")
        sb.append("SUMMARY:${escapeIcsText(entry.title)}\r\n")
        sb.append("DESCRIPTION:$descriptionText\r\n")
        sb.append("STATUS:CONFIRMED\r\n")
        sb.append("END:VEVENT\r\n")
    }

    sb.append("END:VCALENDAR\r\n")
    return sb.toString()
}

fun escapeIcsText(text: String): String {
    return text.replace("\\", "\\\\")
        .replace(";", "\\;")
        .replace(",", "\\,")
        .replace("\n", "\\n")
        .replace("\r", "")
}
