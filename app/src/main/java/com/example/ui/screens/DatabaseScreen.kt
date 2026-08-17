package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.platform.LocalContext
import com.example.utils.SigilExportUtils
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.EnochianAudioPlayer
import com.example.data.model.CharacterMasteryEntity
import com.example.data.reference.EnochianCall
import com.example.data.reference.EnochianData
import com.example.data.reference.EnochianLetter
import com.example.data.reference.EnochianSigilEntry
import com.example.data.reference.WatchtowerInfo
import com.example.ui.theme.CelestialCyan
import com.example.ui.theme.ElementalGreen
import com.example.ui.theme.EnochianGold
import com.example.ui.theme.EnochianGoldLight
import com.example.ui.theme.GoldOutline
import com.example.ui.theme.MysticViolet

@Composable
fun DatabaseScreen(
    characterMasteries: List<CharacterMasteryEntity> = emptyList(),
    onRecordFlashcardReview: (String, Int, Boolean) -> Unit = { _, _, _ -> },
    onResetFlashcardProgress: () -> Unit = {},
    onVibrateCall: (Float) -> Unit,
    onStartCallRitual: ((String) -> Unit)? = null,
    onTraceSigilInGenerator: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val audioPlayer = remember { EnochianAudioPlayer(context) }

    DisposableEffect(Unit) {
        onDispose {
            audioPlayer.release()
        }
    }

    var selectedCategoryIndex by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf("19 Keys / Calls", "30 Aethyrs", "Alphabet Flashcards 🎴", "Enochian Alphabet", "Planets & Spheres", "Sigil Glossary", "Watchtowers")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Enochian Sanctum Vault",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = EnochianGold
        )
        Text(
            text = "Authentic audio chants, sigil correspondences, flashcard study & watchtowers",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )


        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("database_search_input"),
            placeholder = { Text("Search keys, sigils, translation, angels, planets...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = EnochianGold) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EnochianGold,
                unfocusedBorderColor = GoldOutline,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Categories Tab Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories.size) { index ->
                val isSelected = selectedCategoryIndex == index
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) EnochianGold else MaterialTheme.colorScheme.surface
                        )
                        .border(
                            1.dp,
                            if (isSelected) EnochianGold else GoldOutline,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedCategoryIndex = index }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = categories[index],
                        color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedCategoryIndex) {
            0 -> CallsList(
                calls = EnochianData.CALLS.filter {
                    it.title.contains(searchQuery, ignoreCase = true) ||
                    it.eNochianPhonetic.contains(searchQuery, ignoreCase = true) ||
                    it.englishTranslation.contains(searchQuery, ignoreCase = true) ||
                    it.element.contains(searchQuery, ignoreCase = true)
                },
                audioPlayer = audioPlayer,
                onVibrateCall = onVibrateCall,
                onStartCallRitual = onStartCallRitual
            )
            1 -> AethyrsList(
                aethyrs = EnochianData.AETHYRS.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.meaning.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
                }
            )
            2 -> EnochianFlashcardsModule(
                masteries = characterMasteries,
                onRecordReview = onRecordFlashcardReview,
                onResetProgress = onResetFlashcardProgress
            )
            3 -> AlphabetList(
                letters = EnochianData.ENNOCHIAN_LETTERS.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.englishChar.toString().contains(searchQuery, ignoreCase = true) ||
                    it.elementalAttribute.contains(searchQuery, ignoreCase = true)
                },
                characterMasteries = characterMasteries,
                onLaunchFlashcards = { selectedCategoryIndex = 2 }
            )
            4 -> PlanetaryCorrespondencesList(
                planets = EnochianData.PLANETARY_CORRESPONDENCES.filter {
                    it.planetName.contains(searchQuery, ignoreCase = true) ||
                    it.enochianSenior.contains(searchQuery, ignoreCase = true) ||
                    it.angelicRuler.contains(searchQuery, ignoreCase = true) ||
                    it.magicalDomain.contains(searchQuery, ignoreCase = true) ||
                    it.metal.contains(searchQuery, ignoreCase = true) ||
                    it.incense.contains(searchQuery, ignoreCase = true) ||
                    it.gemstone.contains(searchQuery, ignoreCase = true)
                },
                onTraceSigil = onTraceSigilInGenerator
            )
            5 -> SigilGlossaryList(
                sigils = EnochianData.SIGIL_GLOSSARY.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.traditionalMeaning.contains(searchQuery, ignoreCase = true) ||
                    it.purpose.contains(searchQuery, ignoreCase = true) ||
                    it.planet.contains(searchQuery, ignoreCase = true) ||
                    it.element.contains(searchQuery, ignoreCase = true) ||
                    it.angelicRuler.contains(searchQuery, ignoreCase = true)
                },
                onTraceSigil = onTraceSigilInGenerator
            )
            6 -> WatchtowersList(
                watchtowers = EnochianData.WATCHTOWERS.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.greatKing.contains(searchQuery, ignoreCase = true) ||
                    it.element.contains(searchQuery, ignoreCase = true)
                }
            )
        }
    }
}

@Composable
fun CallsList(
    calls: List<EnochianCall>,
    audioPlayer: EnochianAudioPlayer,
    onVibrateCall: (Float) -> Unit,
    onStartCallRitual: ((String) -> Unit)? = null
) {
    var expandedCallId by remember { mutableStateOf<Int?>(1) }

    val isPlaying = audioPlayer.isPlaying.value
    val isPaused = audioPlayer.isPaused.value
    val activeCallId = audioPlayer.activeCallId.value
    val progress = audioPlayer.speechProgress.value

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(calls, key = { it.id }) { call ->
            val isExpanded = expandedCallId == call.id
            val isThisActive = activeCallId == call.id

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("call_card_${call.id}")
                    .border(
                        1.dp,
                        if (isThisActive && isPlaying) EnochianGold else GoldOutline,
                        RoundedCornerShape(16.dp)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expandedCallId = if (isExpanded) null else call.id
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(EnochianGold),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${call.id}",
                                        color = Color.Black,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = call.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "${call.subtitle} • ${call.element}",
                                style = MaterialTheme.typography.labelMedium,
                                color = EnochianGoldLight
                            )
                        }

                        IconButton(
                            onClick = {
                                expandedCallId = if (isExpanded) null else call.id
                            }
                        ) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Expand",
                                tint = EnochianGold
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = isExpanded,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier.padding(top = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "ENOCHIAN PHONETIC CHANT",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EnochianGold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = call.eNochianPhonetic,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontFamily = FontFamily.Serif,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "English Translation:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MysticViolet
                            )
                            Text(
                                text = call.englishTranslation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Ritual Purpose & Pronunciation:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EnochianGoldLight
                            )
                            Text(
                                text = "${call.purpose}\n${call.pronunciationGuide}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // AUDIO RECORDING CONTROL BAR
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, GoldOutline, RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "AUDIO RECITATION & DRONE",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EnochianGold
                                        )

                                        Text(
                                            text = if (isThisActive && isPlaying) "PLAYING..." else if (isThisActive && isPaused) "PAUSED" else "READY",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isThisActive && isPlaying) ElementalGreen else MysticViolet
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    if (isThisActive && (isPlaying || isPaused)) {
                                        LinearProgressIndicator(
                                            progress = { progress },
                                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                            color = EnochianGold,
                                            trackColor = GoldOutline
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Play / Pause Button
                                        if (isThisActive && isPlaying) {
                                            Button(
                                                onClick = { audioPlayer.pauseCall() },
                                                colors = ButtonDefaults.buttonColors(containerColor = MysticViolet, contentColor = Color.Black),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(Icons.Default.Pause, contentDescription = "Pause", modifier = Modifier.size(18.dp), tint = Color.Black)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Pause", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                                            }
                                        } else {
                                            Button(
                                                onClick = { audioPlayer.playCall(call) },
                                                colors = ButtonDefaults.buttonColors(containerColor = EnochianGold, contentColor = Color.Black),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f).testTag("play_call_button_${call.id}")
                                            ) {
                                                Icon(Icons.Default.PlayArrow, contentDescription = "Play", modifier = Modifier.size(18.dp), tint = Color.Black)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Play Chant", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                                            }
                                        }

                                        // Replay Button
                                        Button(
                                            onClick = { audioPlayer.replayCall() },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface, contentColor = EnochianGold),
                                            shape = RoundedCornerShape(8.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldOutline),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.Replay, contentDescription = "Replay", modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Replay", fontSize = 12.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Vibrate Tone Button in row below with black font
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Button(
                                            onClick = { onVibrateCall(call.frequencyHz) },
                                            colors = ButtonDefaults.buttonColors(containerColor = CelestialCyan, contentColor = Color.Black),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Tone", modifier = Modifier.size(18.dp), tint = Color.Black)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("${call.frequencyHz.toInt()}Hz Tone Vibration", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                                        }
                                    }

                                    if (onStartCallRitual != null) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        OutlinedButton(
                                            onClick = { onStartCallRitual(call.title) },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EnochianGold),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, EnochianGold)
                                        ) {
                                            Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("⏱️ Begin Ritual Timer for ${call.title}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SigilGlossaryList(
    sigils: List<EnochianSigilEntry>,
    onTraceSigil: ((String) -> Unit)?
) {
    val context = LocalContext.current

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(sigils, key = { it.id }) { sigil ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldOutline, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = sigil.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = EnochianGold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = sigil.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier
                                .background(EnochianGold, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = sigil.traditionalMeaning,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Ritual Purpose: ${sigil.purpose}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Correspondences Grid Tags
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CorrespondenceTag("Planet: ${sigil.planet}", MysticViolet)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CorrespondenceTag("Element: ${sigil.element}", CelestialCyan)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CorrespondenceTag("Ruler: ${sigil.angelicRuler}", ElementalGreen)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CorrespondenceTag("Gem: ${sigil.gemstone}", EnochianGoldLight)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (onTraceSigil != null) {
                            Button(
                                onClick = { onTraceSigil(sigil.intentionPhrase) },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .testTag("trace_sigil_button_${sigil.id}"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = EnochianGold
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldOutline),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = "Trace")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Trace Sigil", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        Button(
                            onClick = {
                                val uri = SigilExportUtils.exportSigilToMediaStore(
                                    context = context,
                                    title = sigil.name,
                                    intentionPhrase = sigil.intentionPhrase,
                                    sigilMethod = "Enochian Watchtower Sigil",
                                    lineColorHex = sigil.wheelColorHex
                                )
                                if (uri != null) {
                                    Toast.makeText(context, "Exported ${sigil.name} PNG to Pictures/EnochianSigils!", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Saved sigil PNG image to gallery.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .testTag("export_glossary_sigil_${sigil.id}"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EnochianGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = "Export PNG")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export PNG", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CorrespondenceTag(text: String, tagColor: Color) {
    Box(
        modifier = Modifier
            .background(tagColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
            .border(1.dp, tagColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            color = tagColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PlanetaryCorrespondencesList(
    planets: List<com.example.data.reference.PlanetaryCorrespondence>,
    onTraceSigil: ((String) -> Unit)?
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(planets, key = { it.id }) { planet ->
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
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .border(1.dp, EnochianGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = planet.symbol,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EnochianGold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "${planet.planetName} Sphere",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = EnochianGold
                                )
                                Text(
                                    text = "Day: ${planet.dayOfWeek} • ${planet.element}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EnochianGoldLight
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .background(MysticViolet.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .border(1.dp, MysticViolet, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = planet.symbol,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MysticViolet
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = planet.magicalDomain,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Key correspondences grid tags
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CorrespondenceTag("Senior: ${planet.enochianSenior}", EnochianGold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CorrespondenceTag("Ruler: ${planet.angelicRuler}", CelestialCyan)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CorrespondenceTag("Metal: ${planet.metal}", MysticViolet)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CorrespondenceTag("Incense: ${planet.incense}", ElementalGreen)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CorrespondenceTag("Gem: ${planet.gemstone}", EnochianGoldLight)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                            .border(1.dp, GoldOutline, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "ASSOCIATED ENOCHIAN CALLS:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EnochianGold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = planet.enochianCallsAssociated,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (onTraceSigil != null) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { onTraceSigil(planet.sigilPhrase) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("trace_planet_sigil_${planet.id}"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = EnochianGold
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldOutline),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "Trace")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Trace ${planet.planetName} Planetary Sigil", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun WatchtowersList(watchtowers: List<WatchtowerInfo>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(watchtowers) { wt ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldOutline, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = wt.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EnochianGold
                        )
                        Text(
                            text = "${wt.direction} • ${wt.element}",
                            style = MaterialTheme.typography.labelSmall,
                            color = EnochianGoldLight
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Great King: ${wt.greatKing}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MysticViolet
                    )

                    Text(
                        text = "Seniors: ${wt.seniors.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "GREAT TABLET GRID:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EnochianGold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        wt.gridLetters.forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                row.forEach { char ->
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .border(
                                                1.dp,
                                                GoldOutline.copy(alpha = 0.5f),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .background(MaterialTheme.colorScheme.surface),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = char,
                                            fontWeight = FontWeight.Bold,
                                            color = EnochianGold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = wt.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun AlphabetList(
    letters: List<EnochianLetter>,
    characterMasteries: List<CharacterMasteryEntity> = emptyList(),
    onLaunchFlashcards: () -> Unit = {}
) {
    val masteryMap = remember(characterMasteries) {
        characterMasteries.associateBy { it.letterName }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onLaunchFlashcards() }
                    .border(1.dp, EnochianGold, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.School, contentDescription = "Flashcards", tint = EnochianGold)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Flashcard Memorization Practice 🎴", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EnochianGold)
                            Text("Test yourself on Enochian glyphs & English letter correspondences", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Start", tint = EnochianGold)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(letters) { letter ->
            val level = masteryMap[letter.name]?.masteryLevel ?: 0
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
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, EnochianGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letter.enochianChar,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = EnochianGold
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${letter.name} (${letter.englishChar})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when (level) {
                                    3 -> "🟢 Mastered"
                                    2 -> "🟠 Familiar"
                                    1 -> "🟡 Learning"
                                    else -> "⚪ New"
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = when (level) {
                                    3 -> ElementalGreen
                                    2 -> EnochianGold
                                    1 -> CelestialCyan
                                    else -> Color.Gray
                                }
                            )
                        }
                        Text(
                            text = "Gematria: ${letter.gematriaValue} • ${letter.elementalAttribute}",
                            style = MaterialTheme.typography.bodySmall,
                            color = EnochianGoldLight
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${letter.wheelAngleDegrees.toInt()}°",
                            fontSize = 11.sp,
                            color = MysticViolet
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnochianFlashcardsModule(
    masteries: List<CharacterMasteryEntity>,
    onRecordReview: (String, Int, Boolean) -> Unit,
    onResetProgress: () -> Unit
) {
    val allLetters = EnochianData.ENNOCHIAN_LETTERS

    var selectedFilterMode by remember { mutableStateOf("All Letters") }
    var isShuffleMode by remember { mutableStateOf(false) }

    val masteryMap = remember(masteries) {
        masteries.associateBy { it.letterName }
    }

    val filteredLetters = remember(selectedFilterMode, isShuffleMode, masteries) {
        val baseList = when (selectedFilterMode) {
            "Needs Practice" -> allLetters.filter { (masteryMap[it.name]?.masteryLevel ?: 0) < 3 }
            "Mastered" -> allLetters.filter { (masteryMap[it.name]?.masteryLevel ?: 0) == 3 }
            else -> allLetters
        }
        if (isShuffleMode) baseList.shuffled() else baseList
    }

    var currentCardIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }
    var isShowResetDialog by remember { mutableStateOf(false) }

    val safeIndex = if (filteredLetters.isNotEmpty()) currentCardIndex % filteredLetters.size else 0
    val activeLetter = filteredLetters.getOrNull(safeIndex) ?: allLetters.first()

    val totalPoints = masteries.sumOf { it.masteryLevel }
    val maxPoints = allLetters.size * 3
    val totalMasteryPercent = if (maxPoints > 0) ((totalPoints.toFloat() / maxPoints.toFloat()) * 100f).toInt() else 0

    val masteredCount = masteries.count { it.masteryLevel == 3 }
    val learningCount = masteries.count { it.masteryLevel in 1..2 }
    val unstudiedCount = allLetters.size - masteredCount - learningCount

    val activeMasteryEntity = masteryMap[activeLetter.name]
    val activeMasteryLevel = activeMasteryEntity?.masteryLevel ?: 0

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. Mastery Analytics Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("flashcard_analytics_card")
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
                            Icon(Icons.Default.School, contentDescription = "Mastery", tint = EnochianGold, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Enochian Character Mastery", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = EnochianGold)
                        }

                        IconButton(
                            onClick = { isShowResetDialog = true },
                            modifier = Modifier.size(32.dp).testTag("flashcard_reset_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Overall Alphabet Mastery:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$totalMasteryPercent%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EnochianGold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { (totalMasteryPercent.toFloat() / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = EnochianGold,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatBadge("🟢 Mastered", "$masteredCount / 21", ElementalGreen)
                        StatBadge("🟡 Learning", "$learningCount / 21", EnochianGold)
                        StatBadge("⚪ Unstudied", "$unstudiedCount / 21", CelestialCyan)
                    }
                }
            }
        }

        // 2. Filter & Deck Mode Controls
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    val filters = listOf("All Letters", "Needs Practice", "Mastered")
                    items(filters) { f ->
                        FilterChip(
                            selected = selectedFilterMode == f,
                            onClick = {
                                selectedFilterMode = f
                                currentCardIndex = 0
                                isFlipped = false
                            },
                            label = { Text(f, fontSize = 11.sp) },
                            modifier = Modifier.testTag("flashcard_filter_${f.replace(" ", "_")}"),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EnochianGold,
                                selectedLabelColor = Color.Black,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            ),
                            border = FilterChipDefaults.filterChipBorder(borderColor = GoldOutline, selectedBorderColor = EnochianGold, enabled = true, selected = selectedFilterMode == f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = {
                        isShuffleMode = !isShuffleMode
                        currentCardIndex = 0
                        isFlipped = false
                    },
                    modifier = Modifier
                        .background(if (isShuffleMode) EnochianGold else MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        .size(36.dp)
                        .testTag("flashcard_shuffle_button")
                ) {
                    Icon(
                        Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (isShuffleMode) Color.Black else EnochianGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // 3. Active Flashcard Interactive Section
        if (filteredLetters.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                        .border(1.dp, GoldOutline, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎉 No characters match this filter deck!\nTry selecting 'All Letters'.",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Card ${safeIndex + 1} of ${filteredLetters.size}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EnochianGold
                        )

                        val masteryBadgeText = when (activeMasteryLevel) {
                            3 -> "🟢 Level 3: Mastered"
                            2 -> "🟠 Level 2: Familiar"
                            1 -> "🟡 Level 1: Learning"
                            else -> "⚪ Level 0: Unstudied"
                        }

                        Text(
                            text = masteryBadgeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (activeMasteryLevel) {
                                3 -> ElementalGreen
                                2 -> EnochianGold
                                1 -> CelestialCyan
                                else -> Color.Gray
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 3D-effect Flip Flashcard
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .testTag("flashcard_main_card")
                            .border(2.dp, EnochianGold, RoundedCornerShape(20.dp))
                            .clickable { isFlipped = !isFlipped },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!isFlipped) {
                                // FRONT OF CARD
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.surface)
                                            .border(2.dp, EnochianGold, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = activeLetter.enochianChar,
                                            fontSize = 48.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EnochianGold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = activeLetter.name,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    AssistChip(
                                        onClick = { isFlipped = true },
                                        label = { Text("Tap Card to Flip / Reveal English Letter 🔄", fontSize = 11.sp) },
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = MaterialTheme.colorScheme.surface,
                                            labelColor = EnochianGold
                                        ),
                                        border = AssistChipDefaults.assistChipBorder(borderColor = GoldOutline, enabled = true),
                                        modifier = Modifier.testTag("flashcard_flip_front")
                                    )
                                }
                            } else {
                                // BACK OF CARD
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "English Letter: ${activeLetter.englishChar}",
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EnochianGold
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Enochian Name: ${activeLetter.name}",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text("Gematria: ${activeLetter.gematriaValue}", fontSize = 13.sp, color = CelestialCyan)
                                        Text("•", fontSize = 13.sp, color = GoldOutline)
                                        Text(activeLetter.elementalAttribute, fontSize = 13.sp, color = MysticViolet)
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    AssistChip(
                                        onClick = { isFlipped = false },
                                        label = { Text("Tap to Flip Back 🔄", fontSize = 11.sp) },
                                        colors = AssistChipDefaults.assistChipColors(
                                            containerColor = MaterialTheme.colorScheme.surface,
                                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        border = AssistChipDefaults.assistChipBorder(borderColor = GoldOutline, enabled = true),
                                        modifier = Modifier.testTag("flashcard_flip_back")
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Flashcard Control Action Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                isFlipped = false
                                currentCardIndex = if (currentCardIndex > 0) currentCardIndex - 1 else filteredLetters.size - 1
                            },
                            modifier = Modifier
                                .border(1.dp, GoldOutline, CircleShape)
                                .testTag("flashcard_prev")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Card", tint = EnochianGold)
                        }

                        // Rating Action Buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    val newLevel = (activeMasteryLevel - 1).coerceAtLeast(1)
                                    onRecordReview(activeLetter.name, newLevel, false)
                                    isFlipped = false
                                    if (filteredLetters.isNotEmpty()) {
                                        currentCardIndex = (currentCardIndex + 1) % filteredLetters.size
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000)),
                                modifier = Modifier.testTag("flashcard_need_practice")
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Hard / Review", fontSize = 12.sp, color = Color.White)
                            }

                            Button(
                                onClick = {
                                    val newLevel = (activeMasteryLevel + 1).coerceAtMost(3)
                                    onRecordReview(activeLetter.name, newLevel, true)
                                    isFlipped = false
                                    if (filteredLetters.isNotEmpty()) {
                                        currentCardIndex = (currentCardIndex + 1) % filteredLetters.size
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006400)),
                                modifier = Modifier.testTag("flashcard_know_it")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Know It 🟢", fontSize = 12.sp, color = Color.White)
                            }
                        }

                        IconButton(
                            onClick = {
                                isFlipped = false
                                currentCardIndex = (currentCardIndex + 1) % filteredLetters.size
                            },
                            modifier = Modifier
                                .border(1.dp, GoldOutline, CircleShape)
                                .testTag("flashcard_next")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Card", tint = EnochianGold)
                        }
                    }
                }
            }
        }

        // 4. Quick Grid Overview of All 21 Letters & Mastery Badges
        item {
            Text(
                text = "Alphabet Matrix & Mastery Status:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = EnochianGold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                allLetters.chunked(3).forEach { rowLetters ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        rowLetters.forEach { letter ->
                            val level = masteryMap[letter.name]?.masteryLevel ?: 0
                            val isCurrentSelected = activeLetter.name == letter.name

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isCurrentSelected) EnochianGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                                    )
                                    .border(
                                        1.dp,
                                        if (isCurrentSelected) EnochianGold else GoldOutline,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        val idx = filteredLetters.indexOfFirst { it.name == letter.name }
                                        if (idx >= 0) {
                                            currentCardIndex = idx
                                            isFlipped = false
                                        }
                                    }
                                    .padding(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(letter.enochianChar, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EnochianGold)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(letter.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                            Text("(${letter.englishChar})", fontSize = 10.sp, color = EnochianGoldLight)
                                        }
                                    }

                                    Text(
                                        text = when (level) {
                                            3 -> "🟢"
                                            2 -> "🟠"
                                            1 -> "🟡"
                                            else -> "⚪"
                                        },
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (isShowResetDialog) {
        AlertDialog(
            onDismissRequest = { isShowResetDialog = false },
            title = { Text("Reset Character Mastery Progress?", color = EnochianGold) },
            text = { Text("This will clear all review history and reset all 21 Enochian characters back to Level 0 (Unstudied).", color = MaterialTheme.colorScheme.onSurface) },
            confirmButton = {
                Button(
                    onClick = {
                        onResetProgress()
                        isShowResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Reset All", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { isShowResetDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurface)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun StatBadge(label: String, value: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, GoldOutline, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun AethyrsList(aethyrs: List<com.example.data.reference.AethyrInfo>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(aethyrs) { aethyr ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldOutline, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${aethyr.number}. ${aethyr.name}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EnochianGold
                        )
                        Text(
                            text = aethyr.governors,
                            style = MaterialTheme.typography.labelSmall,
                            color = EnochianGoldLight
                        )
                    }
                    Text(
                        text = aethyr.meaning,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MysticViolet
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = aethyr.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

