package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.EnochianDatabase
import com.example.data.EnochianRepository
import com.example.data.model.CharacterMasteryEntity
import com.example.data.model.InvocationRecord
import com.example.data.model.JournalEntry
import com.example.data.model.SavedSigil
import com.example.utils.ToneGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.data.GeminiSentimentRepository
import com.example.data.model.RitualSentimentAnalysisResult
import com.example.data.api.AstronomicalLunarRepository
import com.example.utils.DetailedLunarPhase
import com.example.utils.EsotericUtils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class TimerMode {
    STOPWATCH,
    COUNTDOWN
}

class EnochianViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EnochianRepository
    private val geminiRepository = GeminiSentimentRepository()
    private val toneGenerator = ToneGenerator()

    // Astronomical Lunar API State
    private val _lunarPhaseState = MutableStateFlow(EsotericUtils.getDetailedLunarPhase())
    val lunarPhaseState: StateFlow<DetailedLunarPhase> = _lunarPhaseState.asStateFlow()

    private val _isFetchingLunarApi = MutableStateFlow(false)
    val isFetchingLunarApi: StateFlow<Boolean> = _isFetchingLunarApi.asStateFlow()

    init {
        val database = EnochianDatabase.getDatabase(application)
        repository = EnochianRepository(
            database.invocationDao(),
            database.journalDao(),
            database.sigilDao(),
            database.enochianKeyDao(),
            database.characterMasteryDao()
        )
        viewModelScope.launch {
            repository.initializeDefaultKeysIfNeeded()
            repository.initializeCharacterMasteriesIfNeeded()
        }
        refreshAstronomicalLunarData()
    }

    fun refreshAstronomicalLunarData() {
        viewModelScope.launch {
            _isFetchingLunarApi.value = true
            val phase = AstronomicalLunarRepository.fetchCurrentLunarPhase()
            _lunarPhaseState.value = phase
            _isFetchingLunarApi.value = false
        }
    }

    // Character Mastery Flashcards
    val characterMasteries: StateFlow<List<CharacterMasteryEntity>> = (repository.allCharacterMasteries ?: flowOf(emptyList()))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun recordFlashcardReview(letterName: String, newMasteryLevel: Int, isCorrect: Boolean) {
        viewModelScope.launch {
            repository.recordFlashcardReview(letterName, newMasteryLevel, isCorrect)
        }
    }

    fun resetFlashcardProgress() {
        viewModelScope.launch {
            repository.resetFlashcardProgress()
        }
    }

    // Invocations state
    val allInvocations: StateFlow<List<InvocationRecord>> = repository.allInvocations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val invocationCount: StateFlow<Int> = repository.invocationCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalDurationSeconds: StateFlow<Int?> = repository.totalDurationSeconds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Journal state
    private val _journalSearchQuery = MutableStateFlow("")
    val journalSearchQuery: StateFlow<String> = _journalSearchQuery.asStateFlow()

    val journalEntries: StateFlow<List<JournalEntry>> = combine(
        repository.allJournalEntries,
        _journalSearchQuery
    ) { entries, query ->
        if (query.isBlank()) {
            entries
        } else {
            val sdfShort = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val sdfWithTime = SimpleDateFormat("yyyy-MM-dd, HH:mm:ss", Locale.getDefault())
            val sdfIso = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val sdfUs = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            val sdfMonthFull = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            val sdfMonthShort = SimpleDateFormat("MMM yyyy", Locale.getDefault())

            entries.filter { entry ->
                val dateObj = Date(entry.timestamp)
                val formattedShort = sdfShort.format(dateObj)
                val formattedWithTime = sdfWithTime.format(dateObj)
                val formattedIso = sdfIso.format(dateObj)
                val formattedUs = sdfUs.format(dateObj)
                val formattedMonthFull = sdfMonthFull.format(dateObj)
                val formattedMonthShort = sdfMonthShort.format(dateObj)

                entry.title.contains(query, ignoreCase = true) ||
                entry.intention.contains(query, ignoreCase = true) ||
                entry.outcomeNotes.contains(query, ignoreCase = true) ||
                entry.insights.contains(query, ignoreCase = true) ||
                entry.keyOrCallUsed.contains(query, ignoreCase = true) ||
                entry.mood.contains(query, ignoreCase = true) ||
                formattedShort.contains(query, ignoreCase = true) ||
                formattedWithTime.contains(query, ignoreCase = true) ||
                formattedIso.contains(query, ignoreCase = true) ||
                formattedUs.contains(query, ignoreCase = true) ||
                formattedMonthFull.contains(query, ignoreCase = true) ||
                formattedMonthShort.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Saved Sigils state
    val savedSigils: StateFlow<List<SavedSigil>> = repository.allSavedSigils
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Timer state
    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _timerSeconds = MutableStateFlow(0)
    val timerSeconds: StateFlow<Int> = _timerSeconds.asStateFlow()

    private val _timerMode = MutableStateFlow(TimerMode.STOPWATCH)
    val timerMode: StateFlow<TimerMode> = _timerMode.asStateFlow()

    private val _countdownTargetSeconds = MutableStateFlow(300) // Default 5 mins
    val countdownTargetSeconds: StateFlow<Int> = _countdownTargetSeconds.asStateFlow()

    private val _isCountdownFinished = MutableStateFlow(false)
    val isCountdownFinished: StateFlow<Boolean> = _isCountdownFinished.asStateFlow()

    private val _laps = MutableStateFlow<List<Pair<Int, Int>>>(emptyList())
    val laps: StateFlow<List<Pair<Int, Int>>> = _laps.asStateFlow()

    private val _activeVibrationCount = MutableStateFlow(0)
    val activeVibrationCount: StateFlow<Int> = _activeVibrationCount.asStateFlow()

    private val _selectedCallForRitual = MutableStateFlow("1st Key: Divinity")
    val selectedCallForRitual: StateFlow<String> = _selectedCallForRitual.asStateFlow()

    // Cloud Sync status
    private val _isSyncingCloud = MutableStateFlow(false)
    val isSyncingCloud: StateFlow<Boolean> = _isSyncingCloud.asStateFlow()

    private val _lastCloudSyncTime = MutableStateFlow<Long?>(System.currentTimeMillis())
    val lastCloudSyncTime: StateFlow<Long?> = _lastCloudSyncTime.asStateFlow()

    // Gemini Sentiment Analysis State
    private val _isAnalyzingSentiment = MutableStateFlow(false)
    val isAnalyzingSentiment: StateFlow<Boolean> = _isAnalyzingSentiment.asStateFlow()

    private val _sentimentAnalysisResult = MutableStateFlow<RitualSentimentAnalysisResult?>(null)
    val sentimentAnalysisResult: StateFlow<RitualSentimentAnalysisResult?> = _sentimentAnalysisResult.asStateFlow()

    fun analyzeJournalSentiments() {
        viewModelScope.launch {
            _isAnalyzingSentiment.value = true
            val currentEntries = journalEntries.value
            val result = geminiRepository.analyzeJournalHistory(currentEntries)
            _sentimentAnalysisResult.value = result
            _isAnalyzingSentiment.value = false
        }
    }

    fun updateJournalSearchQuery(query: String) {
        _journalSearchQuery.value = query
    }

    fun setSelectedCallForRitual(callName: String) {
        _selectedCallForRitual.value = callName
    }

    fun setTimerMode(mode: TimerMode) {
        if (_isTimerRunning.value) {
            pauseTimer()
        }
        _timerMode.value = mode
        _timerSeconds.value = 0
        _isCountdownFinished.value = false
    }

    fun setCountdownTargetSeconds(targetSec: Int) {
        _countdownTargetSeconds.value = targetSec
        if (_timerMode.value == TimerMode.COUNTDOWN) {
            _timerSeconds.value = 0
            _isCountdownFinished.value = false
        }
    }

    fun recordLap() {
        val currentSec = _timerSeconds.value
        val currentList = _laps.value
        val lapNum = currentList.size + 1
        _laps.value = currentList + Pair(lapNum, currentSec)
    }

    fun startTimer() {
        if (_isTimerRunning.value) return
        _isTimerRunning.value = true
        _isCountdownFinished.value = false
        viewModelScope.launch {
            while (_isTimerRunning.value) {
                kotlinx.coroutines.delay(1000)
                if (_isTimerRunning.value) {
                    _timerSeconds.value += 1
                    if (_timerMode.value == TimerMode.COUNTDOWN && _timerSeconds.value >= _countdownTargetSeconds.value) {
                        _isTimerRunning.value = false
                        _isCountdownFinished.value = true
                        vibrateTone(528f)
                        break
                    }
                }
            }
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
    }

    fun resetTimer() {
        _isTimerRunning.value = false
        _timerSeconds.value = 0
        _activeVibrationCount.value = 0
        _isCountdownFinished.value = false
        _laps.value = emptyList()
    }

    fun incrementVibrationCount() {
        _activeVibrationCount.value += 1
    }

    fun vibrateTone(frequencyHz: Float) {
        toneGenerator.playTone(frequencyHz, durationMs = 2500L)
    }

    fun saveInvocationRecord(
        callTitle: String,
        watchtower: String,
        notes: String
    ) {
        val duration = _timerSeconds.value
        val count = _activeVibrationCount.value
        viewModelScope.launch {
            repository.insertInvocation(
                InvocationRecord(
                    callNumber = extractCallNumber(callTitle),
                    callTitle = callTitle,
                    durationSeconds = duration,
                    vibrationCount = count,
                    watchtower = watchtower,
                    notes = notes
                )
            )
            resetTimer()
        }
    }

    fun deleteInvocation(id: Long) {
        viewModelScope.launch {
            repository.deleteInvocation(id)
        }
    }

    fun saveJournalEntry(
        title: String,
        keyOrCallUsed: String,
        planetaryHour: String,
        moonPhase: String,
        intention: String,
        outcomeNotes: String,
        insights: String,
        rating: Int,
        mood: String = "Serene 🕯️"
    ) {
        viewModelScope.launch {
            repository.insertJournalEntry(
                JournalEntry(
                    title = title,
                    keyOrCallUsed = keyOrCallUsed,
                    planetaryHour = planetaryHour,
                    moonPhase = moonPhase,
                    intention = intention,
                    outcomeNotes = outcomeNotes,
                    insights = insights,
                    rating = rating,
                    mood = mood,
                    isCloudSynced = true
                )
            )
            _lastCloudSyncTime.value = System.currentTimeMillis()
        }
    }

    fun deleteJournalEntry(id: Long) {
        viewModelScope.launch {
            repository.deleteJournalEntry(id)
        }
    }

    fun saveSigil(
        title: String,
        originalPhrase: String,
        eNochianLetters: String,
        sigilMethod: String,
        pointsJson: String,
        colorHex: String
    ) {
        viewModelScope.launch {
            repository.saveSigil(
                SavedSigil(
                    title = title,
                    originalPhrase = originalPhrase,
                    eNochianLetters = eNochianLetters,
                    sigilMethod = sigilMethod,
                    pointsJson = pointsJson,
                    colorHex = colorHex
                )
            )
        }
    }

    fun deleteSigil(id: Long) {
        viewModelScope.launch {
            repository.deleteSigil(id)
        }
    }

    fun triggerCloudSync() {
        viewModelScope.launch {
            _isSyncingCloud.value = true
            kotlinx.coroutines.delay(2000) // Simulate cloud encryption and synchronization
            _isSyncingCloud.value = false
            _lastCloudSyncTime.value = System.currentTimeMillis()
        }
    }

    private fun extractCallNumber(title: String): Int {
        val digits = title.filter { it.isDigit() }
        return digits.toIntOrNull() ?: 1
    }

    override fun onCleared() {
        super.onCleared()
        toneGenerator.stopTone()
    }
}
