package com.example.utils

import java.util.Calendar
import java.util.Date
import kotlin.math.floor

data class DetailedLunarPhase(
    val phaseName: String,
    val phaseEmoji: String,
    val illuminationPercent: Int,
    val moonAgeDays: Double,
    val zodiacSign: String,
    val ritualSuitability: String,
    val recommendedEnochianWorking: String,
    val isFromApi: Boolean = false,
    val apiSourceName: String = "Astronomical Ephemeris (Offline Engine)",
    val traditionalMoonName: String? = EsotericUtils.getSeasonalMoonName(),
    val distanceKm: Double? = 384400.0,
    val angularDiameterDegrees: Double? = 0.52,
    val distanceToSunKm: Double? = 149600000.0,
    val sunAngularDiameterDegrees: Double? = 0.53,
    val lastFetchedTimestampMillis: Long = System.currentTimeMillis()
)

data class UpcomingLunarMilestone(
    val phaseName: String,
    val phaseEmoji: String,
    val estimatedDaysRemaining: Int
)

data class PlanetaryHourInfo(
    val hourIndex: Int,
    val timeRangeLabel: String,
    val planetRuler: String,
    val isCurrentHour: Boolean,
    val isDayHour: Boolean
)


object EsotericUtils {

    fun getDetailedLunarPhase(): DetailedLunarPhase {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Conway / Astronomical approximate lunar age calculation
        var r = year % 100
        r %= 19
        if (r > 9) r -= 19
        r = ((r * 11) % 30) + month + day
        if (month < 3) r += 2
        var ageDays = (r % 30).toDouble()
        if (ageDays < 0) ageDays += 30.0

        val synodicMonth = 29.530588
        val phaseRatio = ageDays / synodicMonth

        // Calculate illumination % (100% at full moon ~ 14.76 days, 0% at new moon ~ 0 days)
        val illuminationFraction = (1.0 - kotlin.math.cos(phaseRatio * 2 * Math.PI)) / 2.0
        val illuminationPercent = (illuminationFraction * 100).toInt().coerceIn(0, 100)

        // Zodiac sign approximation
        val zodiacSigns = listOf(
            "Aries ♈", "Taurus ♉", "Gemini ♊", "Cancer ♋",
            "Leo ♌", "Virgo ♍", "Libra ♎", "Scorpio ♏",
            "Sagittarius ♐", "Capricorn ♑", "Aquarius ♒", "Pisces ♓"
        )
        val zodiacIndex = (floor((day + month * 2.5) % 12)).toInt().coerceIn(0, 11)
        val currentZodiac = zodiacSigns[zodiacIndex]

        return when {
            ageDays < 1.84 -> DetailedLunarPhase(
                phaseName = "New Moon",
                phaseEmoji = "🌑",
                illuminationPercent = illuminationPercent,
                moonAgeDays = ageDays,
                zodiacSign = currentZodiac,
                ritualSuitability = "High potency for initiation, consecration of new Sigils & dark scrying.",
                recommendedEnochianWorking = "Consecration of the Sigillum Dei Aemeth, 30th Aethyr TEX invocation, and spiritual reset."
            )
            ageDays < 5.53 -> DetailedLunarPhase(
                phaseName = "Waxing Crescent",
                phaseEmoji = "🌒",
                illuminationPercent = illuminationPercent,
                moonAgeDays = ageDays,
                zodiacSign = currentZodiac,
                ritualSuitability = "Building spiritual momentum, invoking mental clarity and East Air elementals.",
                recommendedEnochianWorking = "First Key recitation, Watchtower of Air (King BATAIVAH), and intellect expansion."
            )
            ageDays < 9.22 -> DetailedLunarPhase(
                phaseName = "First Quarter",
                phaseEmoji = "🌓",
                illuminationPercent = illuminationPercent,
                moonAgeDays = ageDays,
                zodiacSign = currentZodiac,
                ritualSuitability = "Balanced power for willpower, determination, and Fire elementals.",
                recommendedEnochianWorking = "Watchtower of Fire (King EDLPRNAA), Tablet of Union EXARP/BITOM vibration."
            )
            ageDays < 12.91 -> DetailedLunarPhase(
                phaseName = "Waxing Gibbous",
                phaseEmoji = "🌔",
                illuminationPercent = illuminationPercent,
                moonAgeDays = ageDays,
                zodiacSign = currentZodiac,
                ritualSuitability = "Culminating psychic power, deep scrying, and water emotional harmonization.",
                recommendedEnochianWorking = "Watchtower of West (King RAAGIOSL), 18th Key vibration, and crystal stone scrying."
            )
            ageDays < 16.61 -> DetailedLunarPhase(
                phaseName = "Full Moon",
                phaseEmoji = "🌕",
                illuminationPercent = illuminationPercent,
                moonAgeDays = ageDays,
                zodiacSign = currentZodiac,
                ritualSuitability = "MAXIMUM SPIRITUAL POTENCY: Ideal for major angelic invocations and high Aethyr ascension.",
                recommendedEnochianWorking = "Recitation of all 19 Enochian Keys, invoking the 4 Elemental Kings & First Aethyr LIL."
            )
            ageDays < 20.30 -> DetailedLunarPhase(
                phaseName = "Waning Gibbous",
                phaseEmoji = "🌖",
                illuminationPercent = illuminationPercent,
                moonAgeDays = ageDays,
                zodiacSign = currentZodiac,
                ritualSuitability = "Gratitude, distributing wisdom, and grounding material stability.",
                recommendedEnochianWorking = "Watchtower of Earth (King ICZHIHAL), grounding ritual notes, and sanctuary sealing."
            )
            ageDays < 23.99 -> DetailedLunarPhase(
                phaseName = "Last Quarter",
                phaseEmoji = "🌗",
                illuminationPercent = illuminationPercent,
                moonAgeDays = ageDays,
                zodiacSign = currentZodiac,
                ritualSuitability = "Banishing negative astral residue, releasing blockages, and boundary protection.",
                recommendedEnochianWorking = "Tablet of Union NANTA sealing, banishing pentagrams, and crystal cleansing."
            )
            else -> DetailedLunarPhase(
                phaseName = "Waning Crescent",
                phaseEmoji = "🌘",
                illuminationPercent = illuminationPercent,
                moonAgeDays = ageDays,
                zodiacSign = currentZodiac,
                ritualSuitability = "Rest, introspection, inner sanctuary meditation, and dream work.",
                recommendedEnochianWorking = "Silent meditation on Enochian alphabet Gematria and reflective journal logging."
            )
        }
    }

    fun getUpcomingMilestones(): List<UpcomingLunarMilestone> {
        val currentAge = getDetailedLunarPhase().moonAgeDays
        val synodic = 29.53

        fun daysUntil(targetAge: Double): Int {
            var diff = targetAge - currentAge
            if (diff < 0) diff += synodic
            return diff.toInt()
        }

        return listOf(
            UpcomingLunarMilestone("New Moon", "🌑", daysUntil(0.0)),
            UpcomingLunarMilestone("First Quarter", "🌓", daysUntil(7.38)),
            UpcomingLunarMilestone("Full Moon", "🌕", daysUntil(14.76)),
            UpcomingLunarMilestone("Last Quarter", "🌗", daysUntil(22.14))
        ).sortedBy { it.estimatedDaysRemaining }
    }

    fun getCurrentMoonPhase(): String {
        val detailed = getDetailedLunarPhase()
        return "${detailed.phaseName} ${detailed.phaseEmoji}"
    }

    fun getCurrentPlanetaryHour(): String {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1 = Sun, 2 = Mon, 3 = Tue, ...
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)

        val rulers = listOf("Saturn ♄", "Jupiter ♃", "Mars ♂", "Sun ☉", "Venus ♀", "Mercury ☿", "Moon ☽")

        val dayStartRulerIndex = when (dayOfWeek) {
            Calendar.SUNDAY -> 3    // Sun
            Calendar.MONDAY -> 6    // Moon
            Calendar.TUESDAY -> 2   // Mars
            Calendar.WEDNESDAY -> 5 // Mercury
            Calendar.THURSDAY -> 1  // Jupiter
            Calendar.FRIDAY -> 4    // Venus
            Calendar.SATURDAY -> 0  // Saturn
            else -> 0
        }

        val currentRulerIndex = (dayStartRulerIndex + (hourOfDay % 24)) % 7
        return rulers[currentRulerIndex]
    }

    fun get24PlanetaryHoursOfDay(): List<PlanetaryHourInfo> {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

        val rulers = listOf("Saturn ♄", "Jupiter ♃", "Mars ♂", "Sun ☉", "Venus ♀", "Mercury ☿", "Moon ☽")

        val dayStartRulerIndex = when (dayOfWeek) {
            Calendar.SUNDAY -> 3    // Sun
            Calendar.MONDAY -> 6    // Moon
            Calendar.TUESDAY -> 2   // Mars
            Calendar.WEDNESDAY -> 5 // Mercury
            Calendar.THURSDAY -> 1  // Jupiter
            Calendar.FRIDAY -> 4    // Venus
            Calendar.SATURDAY -> 0  // Saturn
            else -> 0
        }

        return (0..23).map { hour ->
            val rulerIndex = (dayStartRulerIndex + hour) % 7
            val startStr = String.format("%02d:00", hour)
            val endStr = String.format("%02d:00", (hour + 1) % 24)
            PlanetaryHourInfo(
                hourIndex = hour,
                timeRangeLabel = "$startStr - $endStr",
                planetRuler = rulers[rulerIndex],
                isCurrentHour = (hour == currentHour),
                isDayHour = (hour in 6..17)
            )
        }
    }

    fun getSeasonalMoonName(): String {
        val month = Calendar.getInstance().get(Calendar.MONTH)
        return when (month) {
            Calendar.JANUARY -> "Wolf Moon"
            Calendar.FEBRUARY -> "Snow Moon"
            Calendar.MARCH -> "Worm Moon"
            Calendar.APRIL -> "Pink Moon"
            Calendar.MAY -> "Flower Moon"
            Calendar.JUNE -> "Strawberry Moon"
            Calendar.JULY -> "Buck Moon"
            Calendar.AUGUST -> "Sturgeon Moon"
            Calendar.SEPTEMBER -> "Harvest Moon"
            Calendar.OCTOBER -> "Hunter's Moon"
            Calendar.NOVEMBER -> "Beaver Moon"
            else -> "Cold Moon"
        }
    }
}


