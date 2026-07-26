package com.example.data.model

data class RitualSentimentAnalysisResult(
    val overallScore: Int, // 0 to 100
    val dominantState: String,
    val progressTrend: String,
    val devotionPercent: Int,
    val clarityPercent: Int,
    val tranquilityPercent: Int,
    val intensityPercent: Int,
    val esotericSummary: String,
    val recommendedNextWorking: String,
    val journalSentiments: List<EntrySentimentSummary>
)

data class EntrySentimentSummary(
    val journalId: Long,
    val title: String,
    val sentimentScore: Int,
    val sentimentTag: String,
    val emotionalTone: String
)
