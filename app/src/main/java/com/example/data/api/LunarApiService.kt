package com.example.data.api

import com.example.utils.DetailedLunarPhase
import com.example.utils.EsotericUtils
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class FarmsenseMoonPhaseResponse(
    @Json(name = "Age") val age: Double?,
    @Json(name = "Phase") val phase: String?,
    @Json(name = "Illumination") val illumination: Double?,
    @Json(name = "Moon") val moon: List<String>?,
    @Json(name = "Distance") val distance: Double?,
    @Json(name = "AngularDiameter") val angularDiameter: Double?,
    @Json(name = "DistanceToSun") val distanceToSun: Double?,
    @Json(name = "SunAngularDiameter") val sunAngularDiameter: Double?,
    @Json(name = "TargetDate") val targetDate: Long?
)

interface FarmsenseLunarService {
    @GET("v1/moonphases/")
    suspend fun getMoonPhase(
        @Query("d") unixTimestamp: Long
    ): List<FarmsenseMoonPhaseResponse>
}

object AstronomicalLunarRepository {
    private const val BASE_URL = "https://api.farmsense.net/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val service: FarmsenseLunarService by lazy {
        retrofit.create(FarmsenseLunarService::class.java)
    }

    suspend fun fetchCurrentLunarPhase(): DetailedLunarPhase {
        return try {
            val unixTimestamp = System.currentTimeMillis() / 1000L
            val responseList = service.getMoonPhase(unixTimestamp)
            if (responseList.isNotEmpty()) {
                val apiData = responseList.first()
                val offlinePhase = EsotericUtils.getDetailedLunarPhase()
                val ageDays = apiData.age ?: offlinePhase.moonAgeDays
                val illumPercent = if (apiData.illumination != null) {
                    (apiData.illumination * 100).toInt().coerceIn(0, 100)
                } else {
                    offlinePhase.illuminationPercent
                }

                val apiPhaseName = apiData.phase ?: "Full Moon"
                val mappedPhase = mapApiPhaseToEnochian(apiPhaseName, ageDays, illumPercent)

                val moonNames = apiData.moon
                val tradName = if (!moonNames.isNullOrEmpty() && moonNames.first().isNotBlank()) {
                    moonNames.first()
                } else {
                    EsotericUtils.getSeasonalMoonName()
                }

                // Farmsense returns distance in Earth Radii (~60.3 = ~384,400 km)
                val distKm = if (apiData.distance != null) apiData.distance * 6371.0 else 384400.0
                val angDiam = apiData.angularDiameter ?: 0.52
                // Farmsense returns distanceToSun in AU or Earth radii
                val solDistKm = if (apiData.distanceToSun != null && apiData.distanceToSun > 1000.0) {
                    apiData.distanceToSun * 6371.0
                } else if (apiData.distanceToSun != null) {
                    apiData.distanceToSun * 149597870.7
                } else {
                    149600000.0
                }
                val solAngDiam = apiData.sunAngularDiameter ?: 0.53

                mappedPhase.copy(
                    isFromApi = true,
                    apiSourceName = "Farmsense Astronomical API v1",
                    traditionalMoonName = tradName,
                    distanceKm = distKm,
                    angularDiameterDegrees = angDiam,
                    distanceToSunKm = solDistKm,
                    sunAngularDiameterDegrees = solAngDiam,
                    lastFetchedTimestampMillis = System.currentTimeMillis()
                )
            } else {
                EsotericUtils.getDetailedLunarPhase().copy(
                    isFromApi = false,
                    apiSourceName = "Astronomical Ephemeris (Offline Engine)",
                    lastFetchedTimestampMillis = System.currentTimeMillis()
                )
            }
        } catch (e: Exception) {
            // Network fallback to offline astronomical calculation
            EsotericUtils.getDetailedLunarPhase().copy(
                isFromApi = false,
                apiSourceName = "Astronomical Ephemeris (Offline Engine)",
                lastFetchedTimestampMillis = System.currentTimeMillis()
            )
        }
    }

    private fun mapApiPhaseToEnochian(apiPhase: String, ageDays: Double, illumPercent: Int): DetailedLunarPhase {
        val basePhase = EsotericUtils.getDetailedLunarPhase()
        val normalizedPhase = apiPhase.trim().lowercase()
        val (emoji, suit, working) = when {
            normalizedPhase.contains("new") -> Triple(
                "🌑",
                "High potency for initiation, consecration of new Sigils & dark scrying.",
                "Consecration of the Sigillum Dei Aemeth, 30th Aethyr TEX invocation, and spiritual reset."
            )
            (normalizedPhase.contains("crescent") && ageDays < 7) || normalizedPhase.contains("waxing crescent") -> Triple(
                "🌒",
                "Building spiritual momentum, invoking mental clarity and East Air elementals.",
                "First Key recitation, Watchtower of Air (King BATAIVAH), and intellect expansion."
            )
            normalizedPhase.contains("1st quarter") || normalizedPhase.contains("first quarter") -> Triple(
                "🌓",
                "Balanced power for willpower, determination, and Fire elementals.",
                "Watchtower of Fire (King EDLPRNAA), Tablet of Union EXARP/BITOM vibration."
            )
            (normalizedPhase.contains("gibbous") && ageDays < 14) || normalizedPhase.contains("waxing gibbous") -> Triple(
                "🌔",
                "Culminating psychic power, deep scrying, and water emotional harmonization.",
                "Watchtower of West (King RAAGIOSL), 18th Key vibration, and crystal stone scrying."
            )
            normalizedPhase.contains("full") -> Triple(
                "🌕",
                "MAXIMUM SPIRITUAL POTENCY: Ideal for major angelic invocations and high Aethyr ascension.",
                "Recitation of all 19 Enochian Keys, invoking the 4 Elemental Kings & First Aethyr LIL."
            )
            (normalizedPhase.contains("gibbous") && ageDays > 16) || normalizedPhase.contains("waning gibbous") -> Triple(
                "🌖",
                "Gratitude, distributing wisdom, and grounding material stability.",
                "Watchtower of Earth (King ICZHIHAL), grounding ritual notes, and sanctuary sealing."
            )
            normalizedPhase.contains("3rd quarter") || normalizedPhase.contains("third quarter") || normalizedPhase.contains("last quarter") -> Triple(
                "🌗",
                "Releasing old contracts, dissolving blockages, and purifying the ritual space.",
                "Lesser Enochian banishing, 10th Key invocation, and elemental purification."
            )
            else -> Triple(
                "🌘",
                "Rest, contemplative scrying, inner temple reflection, and preparation.",
                "Silent meditation on the Holy Table, studying the Aethyrs, and dream work."
            )
        }

        return basePhase.copy(
            phaseName = apiPhase.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
            phaseEmoji = emoji,
            illuminationPercent = illumPercent,
            moonAgeDays = ageDays,
            ritualSuitability = suit,
            recommendedEnochianWorking = working
        )
    }
}
