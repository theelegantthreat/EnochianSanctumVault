package com.example.data

import com.example.BuildConfig
import com.example.data.api.GeminiApiService
import com.example.data.api.GeminiContent
import com.example.data.api.GeminiPart
import com.example.data.api.GeminiRequest
import com.example.data.model.EntrySentimentSummary
import com.example.data.model.JournalEntry
import com.example.data.model.RitualSentimentAnalysisResult
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class GeminiSentimentRepository {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val apiService = retrofit.create(GeminiApiService::class.java)

    suspend fun analyzeJournalHistory(entries: List<JournalEntry>): RitualSentimentAnalysisResult {
        if (entries.isEmpty()) {
            return RitualSentimentAnalysisResult(
                overallScore = 50,
                dominantState = "Awaiting First Grimoire Entry 📜",
                progressTrend = "Neutral Base",
                devotionPercent = 25,
                clarityPercent = 25,
                tranquilityPercent = 25,
                intensityPercent = 25,
                esotericSummary = "No ritual journal entries recorded yet. Begin logging invocations, intentions, and outcome insights to reveal your spiritual progression.",
                recommendedNextWorking = "Perform First Key Invocation during Mercury or Sun Planetary Hour.",
                journalSentiments = emptyList()
            )
        }

        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return generateEsotericFallbackAnalysis(entries)
        }

        return try {
            val promptText = buildPromptForEntries(entries)
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(GeminiPart(text = promptText))
                    )
                )
            )

            val response = apiService.generateContent(apiKey, request)
            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

            if (rawText != null) {
                parseGeminiTextResponse(rawText, entries)
            } else {
                generateEsotericFallbackAnalysis(entries)
            }
        } catch (e: Exception) {
            generateEsotericFallbackAnalysis(entries)
        }
    }

    private fun buildPromptForEntries(entries: List<JournalEntry>): String {
        val entryDetails = entries.mapIndexed { idx, entry ->
            """
            Entry #${idx + 1} (ID: ${entry.id}):
            Title: ${entry.title}
            Key/Call: ${entry.keyOrCallUsed}
            Planetary Hour: ${entry.planetaryHour} | Moon Phase: ${entry.moonPhase}
            Intention: ${entry.intention}
            Outcome Notes: ${entry.outcomeNotes}
            Insights: ${entry.insights}
            Rating: ${entry.rating}/5
            Timestamp: ${entry.timestamp}
            """.trimIndent()
        }.joinToString("\n---\n")

        return """
            You are an expert mystical sentiment analyzer specializing in Enochian Magic and Hermetic ritual journals.
            Analyze the following ritual journal entries and evaluate the practitioner's emotional progress, spiritual alignment, devotion, clarity, tranquility, and intensity.

            JOURNAL ENTRIES TO ANALYZE:
            $entryDetails

            Provide an analysis output matching the following exact format with labels:

            OVERALL_SCORE: [integer 0 to 100]
            DOMINANT_STATE: [short phrase e.g. "Ascending Illumination 🌌"]
            PROGRESS_TREND: [short phrase e.g. "+22% Emotional Clarity over last 3 rituals"]
            DEVOTION: [integer 0 to 100]
            CLARITY: [integer 0 to 100]
            TRANQUILITY: [integer 0 to 100]
            INTENSITY: [integer 0 to 100]
            SUMMARY: [2-3 sentences summarizing the practitioner's emotional trajectory, spiritual breakthroughs, and mindstate evolution]
            RECOMMENDED_WORKING: [1 sentence recommending the next Enochian call, moon phase or planetary hour for spiritual growth]
            ENTRY_SENTIMENTS:
            [For each entry ID, output: ID|Score|Tag|EmotionalTone]
        """.trimIndent()
    }

    private fun parseGeminiTextResponse(rawText: String, entries: List<JournalEntry>): RitualSentimentAnalysisResult {
        var overallScore = 75
        var dominantState = "Illuminated Equanimity 🌌"
        var progressTrend = "Positive Spiritual Resonance"
        var devotion = 70
        var clarity = 75
        var tranquility = 80
        var intensity = 65
        var summary = ""
        var recommendedWorking = ""
        val entrySentiments = mutableListOf<EntrySentimentSummary>()

        val lines = rawText.lines()
        var parsingEntries = false

        for (line in lines) {
            val trimmed = line.trim()
            when {
                trimmed.startsWith("OVERALL_SCORE:") -> overallScore = trimmed.substringAfter(":").trim().toIntOrNull()?.coerceIn(0, 100) ?: 75
                trimmed.startsWith("DOMINANT_STATE:") -> dominantState = trimmed.substringAfter(":").trim().ifEmpty { "Illuminated Serenity 🌌" }
                trimmed.startsWith("PROGRESS_TREND:") -> progressTrend = trimmed.substringAfter(":").trim().ifEmpty { "Steadfast Expansion" }
                trimmed.startsWith("DEVOTION:") -> devotion = trimmed.substringAfter(":").trim().toIntOrNull()?.coerceIn(0, 100) ?: 70
                trimmed.startsWith("CLARITY:") -> clarity = trimmed.substringAfter(":").trim().toIntOrNull()?.coerceIn(0, 100) ?: 75
                trimmed.startsWith("TRANQUILITY:") -> tranquility = trimmed.substringAfter(":").trim().toIntOrNull()?.coerceIn(0, 100) ?: 80
                trimmed.startsWith("INTENSITY:") -> intensity = trimmed.substringAfter(":").trim().toIntOrNull()?.coerceIn(0, 100) ?: 65
                trimmed.startsWith("SUMMARY:") -> summary = trimmed.substringAfter(":").trim()
                trimmed.startsWith("RECOMMENDED_WORKING:") -> recommendedWorking = trimmed.substringAfter(":").trim()
                trimmed.startsWith("ENTRY_SENTIMENTS:") -> parsingEntries = true
                parsingEntries && trimmed.contains("|") -> {
                    val parts = trimmed.split("|")
                    if (parts.size >= 4) {
                        val id = parts[0].trim().toLongOrNull() ?: 0L
                        val score = parts[1].trim().toIntOrNull() ?: 75
                        val tag = parts[2].trim()
                        val tone = parts[3].trim()

                        val matchedEntry = entries.find { it.id == id }
                        val title = matchedEntry?.title ?: "Ritual $id"
                        entrySentiments.add(EntrySentimentSummary(id, title, score, tag, tone))
                    }
                }
            }
        }

        if (summary.isBlank()) {
            summary = rawText.take(250).replace("\n", " ")
        }
        if (recommendedWorking.isBlank()) {
            recommendedWorking = "Invoke the 1st Key during the Sun Planetary Hour for maximum spiritual clarity."
        }

        if (entrySentiments.isEmpty()) {
            entries.forEach { entry ->
                entrySentiments.add(
                    EntrySentimentSummary(
                        journalId = entry.id,
                        title = entry.title,
                        sentimentScore = (entry.rating * 18 + 10).coerceIn(20, 100),
                        sentimentTag = if (entry.rating >= 4) "Elevated ✨" else "Seeking 🕯️",
                        emotionalTone = if (entry.rating >= 4) "Serene Focus" else "Reflective Intention"
                    )
                )
            }
        }

        return RitualSentimentAnalysisResult(
            overallScore = overallScore,
            dominantState = dominantState,
            progressTrend = progressTrend,
            devotionPercent = devotion,
            clarityPercent = clarity,
            tranquilityPercent = tranquility,
            intensityPercent = intensity,
            esotericSummary = summary,
            recommendedNextWorking = recommendedWorking,
            journalSentiments = entrySentiments
        )
    }

    private fun generateEsotericFallbackAnalysis(entries: List<JournalEntry>): RitualSentimentAnalysisResult {
        val avgRating = entries.map { it.rating }.average()
        val score = (avgRating * 18 + 10).roundToInt().coerceIn(10, 100)

        val positiveKeywords = listOf("divine", "peace", "light", "clarity", "angel", "vision", "powerful", "joy", "serene", "focus")
        var positiveCount = 0
        var totalWords = 0

        entries.forEach { entry ->
            val text = "${entry.intention} ${entry.outcomeNotes} ${entry.insights}".lowercase()
            totalWords += text.split("\\s+".toRegex()).size
            positiveKeywords.forEach { kw ->
                if (text.contains(kw)) positiveCount++
            }
        }

        val devotion = (score * 0.95).roundToInt().coerceIn(20, 98)
        val clarity = (score * 0.90 + positiveCount * 3).roundToInt().coerceIn(25, 95)
        val tranquility = (score * 0.85 + 10).roundToInt().coerceIn(30, 95)
        val intensity = (score * 1.05).roundToInt().coerceIn(20, 100)

        val dominantState = when {
            score >= 85 -> "Divine Illumination & Exaltation 🌟"
            score >= 70 -> "Mystic Harmony & Clear Vision 🌌"
            score >= 50 -> "Equanimity & Focused Seeking 🕯️"
            else -> "Purification & Inner Grounding 🌒"
        }

        val trend = if (entries.size > 1) {
            val firstRating = entries.last().rating
            val recentRating = entries.first().rating
            val diff = recentRating - firstRating
            if (diff > 0) "+${diff * 20}% Resonance Growth across entries"
            else if (diff < 0) "${diff * 15}% Reflective Calibration"
            else "Steady Harmonic Equilibrium"
        } else {
            "Initial Grimoire Baseline Established"
        }

        val summary = "Analysis of ${entries.size} grimoire entries indicates a high degree of ${if (score >= 70) "spiritual resonance and mental alignment" else "dedicated purification and focused practice"}. The emotional trajectory shows deepening clarity during ritual invocations, with consistent focus across lunar phases."

        val recommendedWorking = when {
            score >= 80 -> "Perform First & Second Key invocations under the Full Moon to crystallize higher Aethyr visions."
            score >= 60 -> "Perform Watchtower of Air invocations during Mercury Planetary Hour to boost mental clarity."
            else -> "Consecrate a Protection Sigil during Saturn Planetary Hour to anchor ritual boundaries."
        }

        val entrySummaries = entries.map { entry ->
            val eScore = (entry.rating * 18 + 10).coerceIn(20, 100)
            EntrySentimentSummary(
                journalId = entry.id,
                title = entry.title,
                sentimentScore = eScore,
                sentimentTag = when {
                    eScore >= 80 -> "Exalted ✨"
                    eScore >= 60 -> "Harmonic 🌌"
                    else -> "Grounding 🕯️"
                },
                emotionalTone = when {
                    eScore >= 80 -> "Awe & Illumination"
                    eScore >= 60 -> "Focus & Serenity"
                    else -> "Contemplative"
                }
            )
        }

        return RitualSentimentAnalysisResult(
            overallScore = score,
            dominantState = dominantState,
            progressTrend = trend,
            devotionPercent = devotion,
            clarityPercent = clarity,
            tranquilityPercent = tranquility,
            intensityPercent = intensity,
            esotericSummary = summary,
            recommendedNextWorking = recommendedWorking,
            journalSentiments = entrySummaries
        )
    }
}
