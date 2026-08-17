package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import com.example.ui.viewmodel.TimerMode
import com.example.data.model.InvocationRecord
import com.example.data.reference.EnochianData
import com.example.ui.theme.CelestialCyan
import com.example.ui.theme.ElementalGreen
import com.example.ui.theme.ElementalRed
import com.example.ui.theme.EnochianGold
import com.example.ui.theme.EnochianGoldLight
import com.example.ui.theme.GoldOutline
import com.example.ui.theme.MysticViolet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InvocationTrackerScreen(
    invocations: List<InvocationRecord>,
    totalInvocationsCount: Int,
    totalDurationSeconds: Int,
    isTimerRunning: Boolean,
    timerSeconds: Int,
    timerMode: TimerMode = TimerMode.STOPWATCH,
    countdownTargetSeconds: Int = 300,
    isCountdownFinished: Boolean = false,
    laps: List<Pair<Int, Int>> = emptyList(),
    vibrationCount: Int,
    selectedCall: String,
    onSelectCall: (String) -> Unit,
    onSetTimerMode: (TimerMode) -> Unit = {},
    onSetCountdownTargetSeconds: (Int) -> Unit = {},
    onRecordLap: () -> Unit = {},
    onStartTimer: () -> Unit,
    onPauseTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onIncrementVibration: () -> Unit,
    onVibrateTone: (Float) -> Unit,
    onSaveInvocation: (String, String, String) -> Unit,
    onDeleteInvocation: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var isShowSaveDialog by remember { mutableStateOf(false) }
    var isShowCustomCountdownDialog by remember { mutableStateOf(false) }
    var customMinutesInput by remember { mutableStateOf("10") }
    var notesInput by remember { mutableStateOf("") }
    var watchtowerInput by remember { mutableStateOf("Watchtower of East (Air)") }

    var isCallDropdownExpanded by remember { mutableStateOf(false) }

    val watchtowerOptions = listOf(
        "Watchtower of East (Air)",
        "Watchtower of South (Fire)",
        "Watchtower of West (Water)",
        "Watchtower of North (Earth)",
        "Tablet of Union (Spirit)"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Invocation Progress Tracker",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = EnochianGold
        )
        Text(
            text = "Track ritual invocations, chant vibrations, time & streaks",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats Summary Card
            item {
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
                                Icon(
                                    Icons.Default.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = ElementalRed,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Ritual Streak: ${calculateStreak(invocations)} Days",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "PRACTICE ACTIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EnochianGold,
                                modifier = Modifier
                                    .background(GoldOutline.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatBox("Invocations", "$totalInvocationsCount", EnochianGold)
                            StatBox("Total Duration", "${totalDurationSeconds / 60}m", CelestialCyan)
                            StatBox("Vibrations", "${invocations.sumOf { it.vibrationCount }}", MysticViolet)
                        }
                    }
                }
            }

            // Active Ritual Timer & Vibration Pad
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, EnochianGold, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ACTIVE RITUAL SESSION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EnochianGold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Timer Mode Switcher (Stopwatch vs Countdown)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            val isStopwatch = timerMode == TimerMode.STOPWATCH
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isStopwatch) EnochianGold else Color.Transparent)
                                    .clickable { onSetTimerMode(TimerMode.STOPWATCH) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = if (isStopwatch) Color.Black else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "⏱️ Stopwatch",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isStopwatch) Color.Black else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            val isCountdown = timerMode == TimerMode.COUNTDOWN
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isCountdown) EnochianGold else Color.Transparent)
                                    .clickable { onSetTimerMode(TimerMode.COUNTDOWN) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.HourglassTop,
                                        contentDescription = null,
                                        tint = if (isCountdown) Color.Black else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "⏳ Countdown",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCountdown) Color.Black else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        // Countdown Presets Row
                        if (timerMode == TimerMode.COUNTDOWN) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Preset Ritual Durations:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val presets = listOf(
                                    Pair("3m", 180),
                                    Pair("5m", 300),
                                    Pair("10m", 600),
                                    Pair("15m", 900),
                                    Pair("30m", 1800)
                                )
                                items(presets) { (label, secs) ->
                                    val isSelected = countdownTargetSeconds == secs
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { onSetCountdownTargetSeconds(secs) },
                                        label = { Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = EnochianGold,
                                            selectedLabelColor = Color.Black
                                        )
                                    )
                                }
                                item {
                                    FilterChip(
                                        selected = !presets.any { it.second == countdownTargetSeconds },
                                        onClick = { isShowCustomCountdownDialog = true },
                                        label = { Text("Custom...", fontSize = 12.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = EnochianGold,
                                            selectedLabelColor = Color.Black
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Call Selector Dropdown
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, GoldOutline, RoundedCornerShape(8.dp))
                                .clickable { isCallDropdownExpanded = true }
                                .padding(12.dp)
                        ) {
                            Text(
                                text = selectedCall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            DropdownMenu(
                                expanded = isCallDropdownExpanded,
                                onDismissRequest = { isCallDropdownExpanded = false }
                            ) {
                                EnochianData.CALLS.forEach { call ->
                                    DropdownMenuItem(
                                        text = { Text(call.title) },
                                        onClick = {
                                            onSelectCall(call.title)
                                            isCallDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Circular Gauge Progress Ring & Display
                        Box(
                            modifier = Modifier.size(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val strokeWidth = 10.dp.toPx()
                                val diameter = size.minDimension - strokeWidth
                                val topLeftOffset = androidx.compose.ui.geometry.Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
                                val arcSize = androidx.compose.ui.geometry.Size(diameter, diameter)

                                // Track background ring
                                drawArc(
                                    color = GoldOutline.copy(alpha = 0.3f),
                                    startAngle = -90f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    topLeft = topLeftOffset,
                                    size = arcSize,
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                                )

                                // Active progress sweep
                                val sweepAngle = if (timerMode == TimerMode.COUNTDOWN) {
                                    val progress = (timerSeconds.toFloat() / countdownTargetSeconds.toFloat()).coerceIn(0f, 1f)
                                    360f * (1f - progress)
                                } else {
                                    val secondsInMin = (timerSeconds % 60) / 60f
                                    if (secondsInMin == 0f && timerSeconds > 0) 360f else 360f * secondsInMin
                                }

                                val ringColor = if (isCountdownFinished) ElementalGreen else if (isTimerRunning) EnochianGold else CelestialCyan

                                drawArc(
                                    color = ringColor,
                                    startAngle = -90f,
                                    sweepAngle = sweepAngle,
                                    useCenter = false,
                                    topLeft = topLeftOffset,
                                    size = arcSize,
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                val displayTimeStr = if (timerMode == TimerMode.COUNTDOWN) {
                                    val remaining = maxOf(0, countdownTargetSeconds - timerSeconds)
                                    formatTime(remaining)
                                } else {
                                    formatTime(timerSeconds)
                                }

                                Text(
                                    text = displayTimeStr,
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCountdownFinished) ElementalGreen else EnochianGold,
                                    modifier = Modifier.testTag("invocation_timer_display")
                                )

                                Text(
                                    text = if (timerMode == TimerMode.COUNTDOWN) {
                                        if (isCountdownFinished) "COUNTDOWN COMPLETED" else "TIME REMAINING"
                                    } else {
                                        "STOPWATCH ELAPSED"
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                if (timerMode == TimerMode.COUNTDOWN) {
                                    Text(
                                        text = "Target: ${countdownTargetSeconds / 60}m ${countdownTargetSeconds % 60}s",
                                        fontSize = 11.sp,
                                        color = CelestialCyan
                                    )
                                }
                            }
                        }

                        // Countdown Finished Alert Banner
                        if (isCountdownFinished) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(2.dp, EnochianGold, RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(
                                    containerColor = EnochianGold.copy(alpha = 0.15f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = EnochianGold)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            "✨ RITUAL COUNTDOWN COMPLETED ✨",
                                            fontWeight = FontWeight.Bold,
                                            color = EnochianGold,
                                            fontSize = 13.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "528Hz Solfeggio Vibration Tone Sounded",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = { isShowSaveDialog = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = EnochianGold, contentColor = Color.Black)
                                        ) {
                                            Text("Log Ritual Session", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                        OutlinedButton(onClick = { onVibrateTone(528f) }) {
                                            Text("Chime", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Chant Vibration Count: $vibrationCount",
                            fontSize = 13.sp,
                            color = MysticViolet,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Big Vibration Pulse Button
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(EnochianGold)
                                .clickable {
                                    onIncrementVibration()
                                    onVibrateTone(528f)
                                }
                                .testTag("vibrate_chant_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Chant",
                                    tint = Color.Black,
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = "Vibrate (+1)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Controls Row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!isTimerRunning) {
                                Button(
                                    onClick = onStartTimer,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = EnochianGold,
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Start")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Start", fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = onPauseTimer,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MysticViolet,
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Pause, contentDescription = "Pause")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Pause", fontWeight = FontWeight.Bold)
                                }
                            }

                            if (timerMode == TimerMode.STOPWATCH && isTimerRunning) {
                                OutlinedButton(
                                    onClick = onRecordLap,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Flag, contentDescription = "Lap", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Split")
                                }
                            }

                            OutlinedButton(
                                onClick = onResetTimer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Reset")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Reset")
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Blue Log Button in a dedicated row below
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = { isShowSaveDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CelestialCyan,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Save, contentDescription = "Log", tint = Color.Black)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Log", fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }

                        // Laps / Splits List
                        if (timerMode == TimerMode.STOPWATCH && laps.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Text("Recorded Splits / Markers:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EnochianGold)
                                Spacer(modifier = Modifier.height(4.dp))
                                laps.forEach { (lapNum, lapSec) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Split #$lapNum", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                        Text(formatTime(lapSec), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CelestialCyan)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Invocation History Header
            item {
                Text(
                    text = "Ritual Invocations History",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EnochianGold
                )
            }

            if (invocations.isEmpty()) {
                item {
                    Text(
                        text = "No recorded invocations yet. Use the timer above to complete and log a session.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(invocations, key = { it.id }) { item ->
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
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Done",
                                    tint = EnochianGold
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.callTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${formatDate(item.timestamp)} • ${item.durationSeconds / 60}m ${item.durationSeconds % 60}s • ${item.vibrationCount} chants",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EnochianGoldLight
                                )
                                Text(
                                    text = item.watchtower,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MysticViolet
                                )
                            }

                            IconButton(onClick = { onDeleteInvocation(item.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ElementalRed)
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
            title = { Text("Log Ritual Invocation", color = EnochianGold, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Watchtower Element:")
                    var isWtExpanded by remember { mutableStateOf(false) }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, GoldOutline, RoundedCornerShape(8.dp))
                            .clickable { isWtExpanded = true }
                            .padding(12.dp)
                    ) {
                        Text(watchtowerInput)
                        DropdownMenu(
                            expanded = isWtExpanded,
                            onDismissRequest = { isWtExpanded = false }
                        ) {
                            watchtowerOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt) },
                                    onClick = {
                                        watchtowerInput = opt
                                        isWtExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = notesInput,
                        onValueChange = { notesInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Ritual Notes / Experience") },
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
                        onSaveInvocation(selectedCall, watchtowerInput, notesInput)
                        notesInput = ""
                        isShowSaveDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EnochianGold, contentColor = Color.Black)
                ) {
                    Text("Save Record", fontWeight = FontWeight.Bold)
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

    // Custom Countdown Duration Dialog
    if (isShowCustomCountdownDialog) {
        AlertDialog(
            onDismissRequest = { isShowCustomCountdownDialog = false },
            title = { Text("Set Custom Countdown", color = EnochianGold, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter duration in minutes for your ritual countdown:", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = customMinutesInput,
                        onValueChange = { customMinutesInput = it.filter { char -> char.isDigit() } },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Minutes") },
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
                        val mins = customMinutesInput.toIntOrNull() ?: 5
                        val targetSecs = (mins * 60).coerceAtLeast(10)
                        onSetCountdownTargetSeconds(targetSecs)
                        isShowCustomCountdownDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EnochianGold, contentColor = Color.Black)
                ) {
                    Text("Set Duration", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { isShowCustomCountdownDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun StatBox(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

fun calculateStreak(invocations: List<InvocationRecord>): Int {
    if (invocations.isEmpty()) return 0
    // Simplified streak based on distinct dates
    val dates = invocations.map {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it.timestamp))
    }.distinct().sortedDescending()

    return dates.size
}

fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
}

fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(millis))
}
