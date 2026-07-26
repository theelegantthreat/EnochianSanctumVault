package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brightness3
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.utils.PlanetaryHourInfo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CelestialCyan
import com.example.ui.theme.ElementalGreen
import com.example.ui.theme.EnochianBorder
import com.example.ui.theme.EnochianGold
import com.example.ui.theme.EnochianGoldLight
import com.example.ui.theme.EnochianHeroContainer
import com.example.ui.theme.GoldOutline
import com.example.ui.theme.MysticViolet
import com.example.utils.DetailedLunarPhase
import com.example.utils.EsotericUtils
import com.example.utils.UpcomingLunarMilestone

@Composable
fun LunarCalendarScreen(
    onNavigateToTracker: () -> Unit,
    onNavigateToSigils: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lunarPhase: DetailedLunarPhase = EsotericUtils.getDetailedLunarPhase()
    val upcomingMilestones: List<UpcomingLunarMilestone> = EsotericUtils.getUpcomingMilestones()
    val planetaryHour: String = EsotericUtils.getCurrentPlanetaryHour()
    val planetaryHours24: List<PlanetaryHourInfo> = EsotericUtils.get24PlanetaryHoursOfDay()

    var is24HoursExpanded by remember { mutableStateOf(false) }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Enochian Lunar Ritual Calendar",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = EnochianGold
        )
        Text(
            text = "Astromagical moon phases, planetary hours & ritual potency",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // HERO MOON PHASE CARD
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, EnochianGold, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = EnochianHeroContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CURRENT LUNAR PHASE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EnochianGold
                            )

                            Box(
                                modifier = Modifier
                                    .background(EnochianGold.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    .border(1.dp, EnochianGold, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = lunarPhase.zodiacSign,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EnochianGold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // ARTISTIC MOON CANVAS RENDERING
                        Box(
                            modifier = Modifier.size(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(110.dp)) {
                                val radius = size.minDimension / 2
                                val center = Offset(size.width / 2, size.height / 2)

                                // Background dark moon disk
                                drawCircle(
                                    color = Color(0xFF1E1B29),
                                    radius = radius,
                                    center = center
                                )

                                // Moon illumination arc approximation
                                val illuminationFraction = lunarPhase.illuminationPercent / 100f
                                drawArc(
                                    color = EnochianGoldLight,
                                    startAngle = -90f,
                                    sweepAngle = 180f,
                                    useCenter = true,
                                    size = Size(radius * 2 * illuminationFraction, radius * 2),
                                    topLeft = Offset(center.x - radius * illuminationFraction, center.y - radius)
                                )

                                // Outer glow border
                                drawCircle(
                                    color = EnochianGold,
                                    radius = radius,
                                    center = center,
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "${lunarPhase.phaseName} ${lunarPhase.phaseEmoji}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Illumination: ${lunarPhase.illuminationPercent}% • Moon Age: ${String.format("%.1f", lunarPhase.moonAgeDays)} Days",
                            style = MaterialTheme.typography.bodySmall,
                            color = CelestialCyan
                        )
                    }
                }
            }

            // RITUAL SUITABILITY & RECOMMENDED WORKING
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = "Ritual",
                                tint = EnochianGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Enochian Ritual Potency & Timing",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = EnochianGold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = lunarPhase.ritualSuitability,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                                .border(1.dp, GoldOutline, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "RECOMMENDED RITUAL WORKING:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MysticViolet
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = lunarPhase.recommendedEnochianWorking,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // PLANETARY HOUR & DAY RULER
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
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .border(1.dp, EnochianGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "♄",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EnochianGold
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Current Active Planetary Hour",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = planetaryHour,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = EnochianGold
                                )
                            }

                            IconButton(
                                onClick = { is24HoursExpanded = !is24HoursExpanded },
                                modifier = Modifier.testTag("toggle_24h_planetary_table")
                            ) {
                                Icon(
                                    imageVector = if (is24HoursExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "Expand 24 Hours",
                                    tint = EnochianGold
                                )
                            }
                        }

                        if (is24HoursExpanded) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "24-HOUR PLANETARY SCHEDULE (TODAY)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EnochianGold
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                planetaryHours24.forEach { hourInfo ->
                                    val rowBg = if (hourInfo.isCurrentHour) EnochianGold.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant
                                    val rowBorder = if (hourInfo.isCurrentHour) EnochianGold else Color.Transparent

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(rowBg)
                                            .border(1.dp, rowBorder, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = hourInfo.timeRangeLabel,
                                                    fontSize = 12.sp,
                                                    fontWeight = if (hourInfo.isCurrentHour) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (hourInfo.isCurrentHour) EnochianGold else MaterialTheme.colorScheme.onSurface
                                                )
                                                if (hourInfo.isCurrentHour) {
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = "• Active Now",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = ElementalGreen
                                                    )
                                                }
                                            }

                                            Text(
                                                text = hourInfo.planetRuler,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (hourInfo.isCurrentHour) EnochianGold else MysticViolet
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }


            // UPCOMING LUNAR MILESTONES
            item {
                Text(
                    text = "Upcoming Lunar Phases",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EnochianGold
                )
            }

            items(upcomingMilestones) { milestone ->
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = milestone.phaseEmoji,
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = milestone.phaseName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = if (milestone.estimatedDaysRemaining == 0) "Today!" else "in ~${milestone.estimatedDaysRemaining} Days",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (milestone.estimatedDaysRemaining == 0) ElementalGreen else EnochianGoldLight
                        )
                    }
                }
            }

            // ACTION BUTTONS
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onNavigateToTracker,
                        modifier = Modifier.weight(1f).testTag("lunar_to_tracker_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = EnochianGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Start Timed Ritual", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = onNavigateToSigils,
                        modifier = Modifier.weight(1f).testTag("lunar_to_sigils_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MysticViolet, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Consecrate Sigil", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
