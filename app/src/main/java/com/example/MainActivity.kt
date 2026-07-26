package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
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
import com.example.ui.screens.LunarCalendarScreen
import com.example.ui.screens.RitualJournalScreen
import com.example.ui.screens.SigilGeneratorScreen
import com.example.ui.theme.EnochianGold
import com.example.ui.theme.EnochianMagicTheme
import com.example.ui.theme.GoldOutline
import com.example.ui.theme.MysticViolet
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
    JOURNAL("journal", "Journal", Icons.Default.NoteAdd, "nav_journal")
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
    val vibrationCount by viewModel.activeVibrationCount.collectAsStateWithLifecycle()
    val selectedCallForRitual by viewModel.selectedCallForRitual.collectAsStateWithLifecycle()

    val isSyncingCloud by viewModel.isSyncingCloud.collectAsStateWithLifecycle()
    val lastCloudSyncTime by viewModel.lastCloudSyncTime.collectAsStateWithLifecycle()

    val isAnalyzingSentiment by viewModel.isAnalyzingSentiment.collectAsStateWithLifecycle()
    val sentimentAnalysisResult by viewModel.sentimentAnalysisResult.collectAsStateWithLifecycle()

    val characterMasteries by viewModel.characterMasteries.collectAsStateWithLifecycle()

    var activeSigilIntention by remember { androidx.compose.runtime.mutableStateOf<String?>(null) }

    Scaffold(

        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = EnochianGold,
                tonalElevation = 8.dp
            ) {
                NavigationScreen.entries.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
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
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EnochianGold,
                            selectedTextColor = EnochianGold,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = GoldOutline.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.testTag(screen.testTag)
                    )
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
                    onTraceSigilInGenerator = { phrase ->
                        activeSigilIntention = phrase
                        navController.navigate(NavigationScreen.SIGIL.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
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
                    onNavigateToTracker = {
                        navController.navigate(NavigationScreen.TRACKER.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToSigils = {
                        navController.navigate(NavigationScreen.SIGIL.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }


            composable(NavigationScreen.TRACKER.route) {
                InvocationTrackerScreen(
                    invocations = invocations,
                    totalInvocationsCount = invocationCount,
                    totalDurationSeconds = totalDurationSeconds ?: 0,
                    isTimerRunning = isTimerRunning,
                    timerSeconds = timerSeconds,
                    vibrationCount = vibrationCount,
                    selectedCall = selectedCallForRitual,
                    onSelectCall = { callName ->
                        viewModel.setSelectedCallForRitual(callName)
                    },
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
        }
    }
}
