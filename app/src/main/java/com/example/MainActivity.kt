package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.DatabaseScreen
import com.example.ui.screens.InvocationTrackerScreen
import com.example.ui.screens.JournalCalendarScreen
import com.example.ui.screens.LunarCalendarScreen
import com.example.ui.screens.RitualJournalScreen
import com.example.ui.screens.SigilGeneratorScreen
import com.example.ui.screens.BackupScreen
import com.example.ui.screens.exportBackupDataToJson
import com.example.ui.components.NoteHeaderBar
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.example.ui.screens.parseAndImportBackupJson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.ui.theme.EnochianGold
import com.example.ui.theme.EnochianMagicTheme
import com.example.ui.theme.GoldOutline
import com.example.ui.viewmodel.EnochianViewModel

enum class NavigationScreen(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
    DATABASE("database", "Database", Icons.Default.MenuBook, "nav_database"),
    SIGIL("sigil", "Sigil Gen", Icons.Default.AutoAwesome, "nav_sigils"),
    LUNAR("lunar", "Lunar Phase", Icons.Default.NightlightRound, "nav_lunar"),
    TRACKER("tracker", "Invocations", Icons.Default.Timer, "nav_tracker"),
    JOURNAL("journal", "Journal", Icons.Default.NoteAdd, "nav_journal"),
    CALENDAR("calendar", "Calendar", Icons.Default.CalendarMonth, "nav_calendar"),
    BACKUP("backup", "Backup", Icons.Default.CloudSync, "nav_backup")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EnochianMagicTheme {
                val viewModel: EnochianViewModel = viewModel()
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: EnochianViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavigationScreen.DATABASE.route

    // ViewModel State Observers
    val invocations by viewModel.allInvocations.collectAsStateWithLifecycle()
    val invocationCount by viewModel.invocationCount.collectAsStateWithLifecycle()
    val totalDurationSeconds by viewModel.totalDurationSeconds.collectAsStateWithLifecycle()

    val journalEntries by viewModel.journalEntries.collectAsStateWithLifecycle()
    val journalSearchQuery by viewModel.journalSearchQuery.collectAsStateWithLifecycle()

    val savedSigils by viewModel.savedSigils.collectAsStateWithLifecycle()

    val isTimerRunning by viewModel.isTimerRunning.collectAsStateWithLifecycle()
    val timerSeconds by viewModel.timerSeconds.collectAsStateWithLifecycle()
    val timerMode by viewModel.timerMode.collectAsStateWithLifecycle()
    val countdownTargetSeconds by viewModel.countdownTargetSeconds.collectAsStateWithLifecycle()
    val isCountdownFinished by viewModel.isCountdownFinished.collectAsStateWithLifecycle()
    val laps by viewModel.laps.collectAsStateWithLifecycle()
    val vibrationCount by viewModel.activeVibrationCount.collectAsStateWithLifecycle()
    val selectedCallForRitual by viewModel.selectedCallForRitual.collectAsStateWithLifecycle()

    val isSyncingCloud by viewModel.isSyncingCloud.collectAsStateWithLifecycle()
    val lastCloudSyncTime by viewModel.lastCloudSyncTime.collectAsStateWithLifecycle()

    val isAnalyzingSentiment by viewModel.isAnalyzingSentiment.collectAsStateWithLifecycle()
    val sentimentAnalysisResult by viewModel.sentimentAnalysisResult.collectAsStateWithLifecycle()

    val characterMasteries by viewModel.characterMasteries.collectAsStateWithLifecycle()

    var activeSigilIntention by remember { androidx.compose.runtime.mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val currentDateStr = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }
    val defaultFilename = "backup-EnochianGrimoire-$currentDateStr.JSON"

    val exportedJsonText = remember(journalEntries, invocations, savedSigils) {
        exportBackupDataToJson(journalEntries, invocations, savedSigils)
    }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(exportedJsonText.toByteArray(Charsets.UTF_8))
                }
                Toast.makeText(context, "Backup exported to Downloads successfully!", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to save backup: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                }
                if (!jsonString.isNullOrBlank()) {
                    val result = parseAndImportBackupJson(
                        jsonText = jsonString,
                        onSaveJournalEntry = { title, call, planet, moon, intent, outcome, insights, rating, mood ->
                            viewModel.saveJournalEntry(title, call, planet, moon, intent, outcome, insights, rating, mood)
                        },
                        onSaveInvocation = { callTitle, watchtower, notes ->
                            viewModel.saveInvocationRecord(callTitle, watchtower, notes)
                        },
                        onSaveSigil = { title, phrase, letters, method, json, color ->
                            viewModel.saveSigil(title, phrase, letters, method, json, color)
                        }
                    )
                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Backup file is empty", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to import backup: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    val launchExportJson = {
        try {
            createDocumentLauncher.launch(defaultFilename)
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to open file saver: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    val launchImportJson = {
        try {
            openDocumentLauncher.launch(arrayOf("application/json"))
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to open file picker: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    val navigateToRoute: (String) -> Unit = { route ->
        if (currentRoute != route) {
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            NoteHeaderBar(
                title = "Enochian Grimoire",
                onExportJson = { launchExportJson() },
                onImportJson = { launchImportJson() },
                onNavigateToScreen = navigateToRoute
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                contentColor = EnochianGold,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 2.dp)
                ) {
                    NavigationScreen.entries.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { navigateToRoute(screen.route) },
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EnochianGold,
                                selectedTextColor = EnochianGold,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = GoldOutline.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .width(78.dp)
                                .testTag(screen.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationScreen.DATABASE.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavigationScreen.DATABASE.route) {
                DatabaseScreen(
                    characterMasteries = characterMasteries,
                    onRecordFlashcardReview = { letterName, newLevel, isCorrect ->
                        viewModel.recordFlashcardReview(letterName, newLevel, isCorrect)
                    },
                    onResetFlashcardProgress = {
                        viewModel.resetFlashcardProgress()
                    },
                    onVibrateCall = { frequency ->
                        viewModel.vibrateTone(frequency)
                    },
                    onStartCallRitual = { callTitle ->
                        viewModel.setSelectedCallForRitual(callTitle)
                        navigateToRoute(NavigationScreen.TRACKER.route)
                    },
                    onTraceSigilInGenerator = { phrase ->
                        activeSigilIntention = phrase
                        navigateToRoute(NavigationScreen.SIGIL.route)
                    }
                )
            }

            composable(NavigationScreen.SIGIL.route) {
                SigilGeneratorScreen(
                    savedSigils = savedSigils,
                    onSaveSigil = { title, phrase, letters, method, json, color ->
                        viewModel.saveSigil(title, phrase, letters, method, json, color)
                    },
                    onDeleteSigil = { id ->
                        viewModel.deleteSigil(id)
                    },
                    initialIntention = activeSigilIntention
                )
            }

            composable(NavigationScreen.LUNAR.route) {
                LunarCalendarScreen(
                    onNavigateToTracker = { navigateToRoute(NavigationScreen.TRACKER.route) },
                    onNavigateToSigils = { navigateToRoute(NavigationScreen.SIGIL.route) },
                    viewModel = viewModel
                )
            }


            composable(NavigationScreen.TRACKER.route) {
                InvocationTrackerScreen(
                    invocations = invocations,
                    totalInvocationsCount = invocationCount,
                    totalDurationSeconds = totalDurationSeconds ?: 0,
                    isTimerRunning = isTimerRunning,
                    timerSeconds = timerSeconds,
                    timerMode = timerMode,
                    countdownTargetSeconds = countdownTargetSeconds,
                    isCountdownFinished = isCountdownFinished,
                    laps = laps,
                    vibrationCount = vibrationCount,
                    selectedCall = selectedCallForRitual,
                    onSelectCall = { callName ->
                        viewModel.setSelectedCallForRitual(callName)
                    },
                    onSetTimerMode = { mode -> viewModel.setTimerMode(mode) },
                    onSetCountdownTargetSeconds = { targetSec -> viewModel.setCountdownTargetSeconds(targetSec) },
                    onRecordLap = { viewModel.recordLap() },
                    onStartTimer = { viewModel.startTimer() },
                    onPauseTimer = { viewModel.pauseTimer() },
                    onResetTimer = { viewModel.resetTimer() },
                    onIncrementVibration = { viewModel.incrementVibrationCount() },
                    onVibrateTone = { freq -> viewModel.vibrateTone(freq) },
                    onSaveInvocation = { call, wt, notes ->
                        viewModel.saveInvocationRecord(call, wt, notes)
                    },
                    onDeleteInvocation = { id ->
                        viewModel.deleteInvocation(id)
                    }
                )
            }

            composable(NavigationScreen.JOURNAL.route) {
                RitualJournalScreen(
                    journalEntries = journalEntries,
                    searchQuery = journalSearchQuery,
                    onSearchQueryChange = { query ->
                        viewModel.updateJournalSearchQuery(query)
                    },
                    isSyncingCloud = isSyncingCloud,
                    lastCloudSyncTime = lastCloudSyncTime,
                    onTriggerCloudSync = { viewModel.triggerCloudSync() },
                    isAnalyzingSentiment = isAnalyzingSentiment,
                    sentimentAnalysisResult = sentimentAnalysisResult,
                    onAnalyzeSentiments = { viewModel.analyzeJournalSentiments() },
                    onSaveJournalEntry = { title, call, planet, moon, intent, outcome, insights, rating, mood ->
                        viewModel.saveJournalEntry(title, call, planet, moon, intent, outcome, insights, rating, mood)
                    },
                    onDeleteJournalEntry = { id ->
                        viewModel.deleteJournalEntry(id)
                    }
                )
            }

            composable(NavigationScreen.CALENDAR.route) {
                JournalCalendarScreen(
                    journalEntries = journalEntries,
                    onSaveJournalEntry = { title, call, planet, moon, intent, outcome, insights, rating, mood ->
                        viewModel.saveJournalEntry(title, call, planet, moon, intent, outcome, insights, rating, mood)
                    },
                    onDeleteJournalEntry = { id ->
                        viewModel.deleteJournalEntry(id)
                    }
                )
            }

            composable(NavigationScreen.BACKUP.route) {
                BackupScreen(
                    journalEntries = journalEntries,
                    invocations = invocations,
                    savedSigils = savedSigils,
                    isSyncingCloud = isSyncingCloud,
                    lastCloudSyncTime = lastCloudSyncTime,
                    onTriggerCloudSync = { viewModel.triggerCloudSync() },
                    onSaveJournalEntry = { title, call, planet, moon, intent, outcome, insights, rating, mood ->
                        viewModel.saveJournalEntry(title, call, planet, moon, intent, outcome, insights, rating, mood)
                    },
                    onSaveInvocation = { callTitle, watchtower, notes ->
                        viewModel.saveInvocationRecord(callTitle, watchtower, notes)
                    },
                    onSaveSigil = { title, phrase, letters, method, json, color ->
                        viewModel.saveSigil(title, phrase, letters, method, json, color)
                    },
                    onExportJsonFile = { launchExportJson() },
                    onImportJsonFile = { launchImportJson() }
                )
            }
        }
    }
}

