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
    @field:Json(name = "Age") val age: Double?,
    @field:Json(name = "Phase") val phase: String?,
    @field:Json(name = "Illumination") val illumination: Double?,
    @field:Json(name = "Moon") val moon: List<String>?,
    @field:Json(name = "Distance") val distance: Double?,
    @field:Json(name = "AngularDiameter") val angularDiameter: Double?,
    @field:Json(name = "DistanceToSun") val distanceToSun: Double?,
    @field:Json(name = "SunAngularDiameter") val sunAngularDiameter: Double?,
    @field:Json(name = "TargetDate") val targetDate: Long?
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
        val meta = EsotericUtils.getPhaseMetadataByName(apiPhase, ageDays)

        return basePhase.copy(
            phaseName = meta.phaseName,
            phaseEmoji = meta.phaseEmoji,
            illuminationPercent = illumPercent,
            moonAgeDays = ageDays,
            ritualSuitability = meta.ritualSuitability,
            recommendedEnochianWorking = meta.recommendedEnochianWorking
        )
    }
}
