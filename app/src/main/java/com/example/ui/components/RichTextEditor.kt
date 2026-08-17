package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CelestialCyan
import com.example.ui.theme.ElementalGreen
import com.example.ui.theme.ElementalRed
import com.example.ui.theme.EnochianGold
import com.example.ui.theme.EnochianGoldLight
import com.example.ui.theme.GoldOutline
import com.example.ui.theme.MysticViolet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Suggested standard Enochian & esoteric ritual categories
 */
val DEFAULT_RITUAL_TAGS = listOf(
    "evocation",
    "scrying",
    "astral_vision",
    "protection",
    "healing",
    "angelic_call",
    "watchtower_east",
    "watchtower_south",
    "watchtower_west",
    "watchtower_north",
    "tablet_of_union",
    "initiation",
    "planetary_talisman"
)

/**
 * Sacred planetary and esoteric glyphs for quick insertion
 */
val SACRED_GLYPHS = listOf(
    "🜚" to "Gold / Sol",
    "🜛" to "Silver / Luna",
    "☉" to "Sun",
    "☽" to "Moon",
    "☿" to "Mercury",
    "♀" to "Venus",
    "♂" to "Mars",
    "♃" to "Jupiter",
    "♄" to "Saturn",
    "🜂" to "Fire",
    "🜄" to "Water",
    "🜁" to "Air",
    "🜃" to "Earth",
    "⚚" to "Caduceus"
)

/**
 * Parses markdown and rich formatting into styled AnnotatedString.
 */
fun parseRichTextToAnnotatedString(
    rawText: String,
    baseColor: Color = Color.Unspecified
): AnnotatedString {
    if (rawText.isBlank()) return AnnotatedString("")

    return buildAnnotatedString {
        val lines = rawText.lines()
        lines.forEachIndexed { lineIndex, line ->
            var remaining = line
            var isHeading = false

            // Heading 1 / 2 / 3
            if (remaining.startsWith("### ")) {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EnochianGold))
                remaining = remaining.removePrefix("### ")
                isHeading = true
            } else if (remaining.startsWith("## ")) {
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EnochianGold))
                remaining = remaining.removePrefix("## ")
                isHeading = true
            } else if (remaining.startsWith("# ")) {
                pushStyle(SpanStyle(fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = EnochianGold))
                remaining = remaining.removePrefix("# ")
                isHeading = true
            } else if (remaining.startsWith("> ")) {
                pushStyle(SpanStyle(fontStyle = FontStyle.Italic, color = EnochianGoldLight))
                append("▍ ")
                remaining = remaining.removePrefix("> ")
                isHeading = true
            } else if (remaining.startsWith("• ") || remaining.startsWith("- ") || remaining.startsWith("* ")) {
                pushStyle(SpanStyle(color = EnochianGold, fontWeight = FontWeight.Bold))
                append(" • ")
                pop()
                remaining = remaining.substring(2)
            }

            parseInlineFormatting(remaining, baseColor)

            if (isHeading) {
                pop()
            }

            if (lineIndex < lines.size - 1) {
                append("\n")
            }
        }
    }
}

/**
 * Helper to parse inline tags, bold, italics, highlights, timestamps, and hashtags.
 */
private fun AnnotatedString.Builder.parseInlineFormatting(text: String, defaultColor: Color) {
    // Regex matching tokens:
    // **bold**
    // *italic* or _italic_
    // ==highlight==
    // `code/glyph`
    // [HH:mm:ss] or [timestamp]
    // #tag
    val pattern = Regex(
        "(\\*\\*([^*]+)\\*\\*)|" +                     // Group 1 & 2: **bold**
        "(\\*(?!\\*)([^*]+)\\*)|" +                     // Group 3 & 4: *italic*
        "(_([^_]+)_)|" +                               // Group 5 & 6: _italic_
        "(==([^=]+)==)|" +                             // Group 7 & 8: ==highlight==
        "(`([^`]+)`)|" +                               // Group 9 & 10: `code`
        "(\\[(\\d{2}:\\d{2}(?::\\d{2})?|[^\\]]+)\\])|" + // Group 11 & 12: [timestamp]
        "(#([a-zA-Z0-9_]+))"                           // Group 13 & 14: #tag
    )

    var lastIndex = 0
    pattern.findAll(text).forEach { matchResult ->
        // Append plain text preceding match
        if (matchResult.range.first > lastIndex) {
            append(text.substring(lastIndex, matchResult.range.first))
        }

        when {
            // **bold**
            matchResult.groups[1] != null -> {
                val content = matchResult.groups[2]?.value ?: ""
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold, color = EnochianGoldLight))
                append(content)
                pop()
            }
            // *italic*
            matchResult.groups[3] != null -> {
                val content = matchResult.groups[4]?.value ?: ""
                pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                append(content)
                pop()
            }
            // _italic_
            matchResult.groups[5] != null -> {
                val content = matchResult.groups[6]?.value ?: ""
                pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                append(content)
                pop()
            }
            // ==highlight==
            matchResult.groups[7] != null -> {
                val content = matchResult.groups[8]?.value ?: ""
                pushStyle(SpanStyle(background = EnochianGold.copy(alpha = 0.25f), color = EnochianGold, fontWeight = FontWeight.SemiBold))
                append(" $content ")
                pop()
            }
            // `code`
            matchResult.groups[9] != null -> {
                val content = matchResult.groups[10]?.value ?: ""
                pushStyle(SpanStyle(fontFamily = FontFamily.Monospace, background = MysticViolet.copy(alpha = 0.2f), color = CelestialCyan))
                append(" $content ")
                pop()
            }
            // [timestamp]
            matchResult.groups[11] != null -> {
                val content = matchResult.groups[12]?.value ?: ""
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold, color = CelestialCyan, background = CelestialCyan.copy(alpha = 0.15f)))
                append(" ⏱ $content ")
                pop()
            }
            // #tag
            matchResult.groups[13] != null -> {
                val content = matchResult.groups[14]?.value ?: ""
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold, color = MysticViolet, background = MysticViolet.copy(alpha = 0.15f)))
                append(" #$content ")
                pop()
            }
        }
        lastIndex = matchResult.range.last + 1
    }

    if (lastIndex < text.length) {
        append(text.substring(lastIndex))
    }
}

/**
 * Rich Text Formatted Reflection View
 */
@Composable
fun RichTextReflectionView(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    fontSize: androidx.compose.ui.unit.TextUnit = 12.sp
) {
    if (text.isBlank()) return

    val annotated = remember(text, textColor) {
        parseRichTextToAnnotatedString(text, textColor)
    }

    Text(
        text = annotated,
        modifier = modifier,
        fontSize = fontSize,
        lineHeight = 18.sp,
        color = textColor
    )
}

/**
 * Rich-Text Editor Component with formatting toolbar, timestamp insertion, glyph picker, and live preview.
 */
@Composable
fun RichTextEditor(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Ritual Reflections & Visions",
    placeholder: String = "Record spiritual revelations, timestamps [HH:mm:ss], **bold insights**, #tags...",
    minLines: Int = 4,
    testTagPrefix: String = "rich_editor"
) {
    var textFieldValue by remember(value) {
        mutableStateOf(
            TextFieldValue(
                text = value,
                selection = TextRange(value.length)
            )
        )
    }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Edit, 1: Live Preview
    var isGlyphMenuExpanded by remember { mutableStateOf(false) }

    fun updateTextAndSelection(newText: String, newCursorPos: Int) {
        val safePos = newCursorPos.coerceIn(0, newText.length)
        textFieldValue = TextFieldValue(
            text = newText,
            selection = TextRange(safePos)
        )
        onValueChange(newText)
    }

    fun applyFormatting(prefix: String, suffix: String = "") {
        val currentText = textFieldValue.text
        val selection = textFieldValue.selection

        if (selection.start != selection.end) {
            // Text is selected: wrap selection
            val selectedPart = currentText.substring(selection.start, selection.end)
            val replaced = prefix + selectedPart + suffix
            val newText = currentText.replaceRange(selection.start, selection.end, replaced)
            val newCursor = selection.start + replaced.length
            updateTextAndSelection(newText, newCursor)
        } else {
            // No selection: insert template
            val pos = selection.start
            val inserted = "$prefix$suffix"
            val newText = currentText.substring(0, pos) + inserted + currentText.substring(pos)
            val newCursor = pos + prefix.length
            updateTextAndSelection(newText, newCursor)
        }
    }

    fun insertTimestamp() {
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val tagToInsert = "\n[$timestamp] "
        val pos = textFieldValue.selection.start
        val currentText = textFieldValue.text
        val newText = currentText.substring(0, pos) + tagToInsert + currentText.substring(pos)
        val newCursor = pos + tagToInsert.length
        updateTextAndSelection(newText, newCursor)
    }

    fun insertGlyph(glyph: String) {
        val pos = textFieldValue.selection.start
        val currentText = textFieldValue.text
        val inserted = " $glyph "
        val newText = currentText.substring(0, pos) + inserted + currentText.substring(pos)
        val newCursor = pos + inserted.length
        updateTextAndSelection(newText, newCursor)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, GoldOutline, RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
            .padding(8.dp)
    ) {
        // Top Toolbar: Tabs (Edit vs Preview) + Formatting Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = EnochianGold
            )

            // Segmented Edit / Preview switch
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (selectedTab == 0) EnochianGold else Color.Transparent)
                        .clickable { selectedTab = 0 }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                        .testTag("${testTagPrefix}_tab_edit")
                ) {
                    Text(
                        text = "Edit",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTab == 0) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (selectedTab == 1) EnochianGold else Color.Transparent)
                        .clickable { selectedTab = 1 }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                        .testTag("${testTagPrefix}_tab_preview")
                ) {
                    Text(
                        text = "Preview",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTab == 1) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Action Toolbar (Bold, Italic, Headings, Highlights, Timestamps, Glyphs, Lists, Quotes)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bold
            EditorToolButton(
                label = "B",
                isBold = true,
                onClick = { applyFormatting("**", "**") },
                contentDescription = "Bold",
                testTag = "${testTagPrefix}_btn_bold"
            )

            // Italic
            EditorToolButton(
                label = "I",
                isItalic = true,
                onClick = { applyFormatting("*", "*") },
                contentDescription = "Italic",
                testTag = "${testTagPrefix}_btn_italic"
            )

            // Heading
            EditorToolButton(
                label = "H#",
                onClick = { applyFormatting("### ", "") },
                contentDescription = "Heading",
                testTag = "${testTagPrefix}_btn_heading"
            )

            // Highlight
            EditorToolButton(
                label = "==" ,
                onClick = { applyFormatting("==", "==") },
                contentDescription = "Highlight",
                testTag = "${testTagPrefix}_btn_highlight"
            )

            // Bullet list
            EditorToolButton(
                label = "• List",
                onClick = { applyFormatting("• ", "") },
                contentDescription = "Bullet List",
                testTag = "${testTagPrefix}_btn_bullet"
            )

            // Quote
            EditorToolButton(
                label = "“ Quote",
                onClick = { applyFormatting("> ", "") },
                contentDescription = "Quote",
                testTag = "${testTagPrefix}_btn_quote"
            )

            // Timestamp button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(CelestialCyan.copy(alpha = 0.18f))
                    .border(1.dp, CelestialCyan, RoundedCornerShape(6.dp))
                    .clickable { insertTimestamp() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("${testTagPrefix}_btn_timestamp")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Timestamp",
                        tint = CelestialCyan,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "+Time",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CelestialCyan
                    )
                }
            }

            // Sacred Glyphs Dropdown
            Box {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MysticViolet.copy(alpha = 0.18f))
                        .border(1.dp, MysticViolet, RoundedCornerShape(6.dp))
                        .clickable { isGlyphMenuExpanded = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("${testTagPrefix}_btn_glyphs")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🜚 Glyphs ▾",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EnochianGoldLight
                        )
                    }
                }

                DropdownMenu(
                    expanded = isGlyphMenuExpanded,
                    onDismissRequest = { isGlyphMenuExpanded = false }
                ) {
                    SACRED_GLYPHS.forEach { (glyph, name) ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(glyph, fontSize = 16.sp, color = EnochianGold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(name, fontSize = 12.sp)
                                }
                            },
                            onClick = {
                                insertGlyph(glyph)
                                isGlyphMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (selectedTab == 0) {
            // Edit Mode
            OutlinedTextField(
                value = textFieldValue,
                onValueChange = {
                    textFieldValue = it
                    onValueChange(it.text)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("${testTagPrefix}_input"),
                placeholder = { Text(placeholder, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                minLines = minLines,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EnochianGold,
                    unfocusedBorderColor = GoldOutline.copy(alpha = 0.5f)
                )
            )
        } else {
            // Live Preview Mode
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 110.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                    .border(1.dp, GoldOutline.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(12.dp)
                    .testTag("${testTagPrefix}_preview_box")
            ) {
                if (value.isBlank()) {
                    Text(
                        text = "No text to preview. Type reflections in Edit mode to view rich formatting.",
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    RichTextReflectionView(
                        text = value,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Word count & formatting hint
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Supports **bold**, *italic*, ==highlight==, #tags, [HH:mm:ss]",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val wordCount = if (value.isBlank()) 0 else value.trim().split(Regex("\\s+")).size
            Text(
                text = "$wordCount ${if (wordCount == 1) "word" else "words"}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = EnochianGold
            )
        }
    }
}

/**
 * Individual tool button for toolbar
 */
@Composable
private fun EditorToolButton(
    label: String,
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
    isBold: Boolean = false,
    isItalic: Boolean = false,
    testTag: String = ""
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, GoldOutline.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Medium,
            fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Tag Manager Component for categorizing ritual entries with preset suggestions and custom tags.
 */
@Composable
fun TagCategorizer(
    selectedTags: List<String>,
    onTagsChanged: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    suggestedTags: List<String> = DEFAULT_RITUAL_TAGS
) {
    var newTagInput by remember { mutableStateOf("") }
    var isAddingTag by remember { mutableStateOf(false) }

    fun addTag(tag: String) {
        val cleanTag = tag.trim().removePrefix("#").lowercase().replace(" ", "_")
        if (cleanTag.isNotBlank() && !selectedTags.contains(cleanTag)) {
            onTagsChanged(selectedTags + cleanTag)
        }
        newTagInput = ""
        isAddingTag = false
    }

    fun removeTag(tag: String) {
        onTagsChanged(selectedTags.filter { it != tag })
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, GoldOutline, RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocalOffer,
                    contentDescription = "Tags",
                    tint = EnochianGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Categorization & Ritual Tags:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EnochianGold
                )
            }

            Text(
                text = "${selectedTags.size} tags selected",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Active Selected Tags (with remove '✕' icon)
        if (selectedTags.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                selectedTags.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(MysticViolet.copy(alpha = 0.25f))
                            .border(1.dp, MysticViolet, RoundedCornerShape(16.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                            .testTag("tag_chip_$tag")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "#$tag",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EnochianGold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove tag",
                                tint = EnochianGoldLight,
                                modifier = Modifier
                                    .size(12.dp)
                                    .clickable { removeTag(tag) }
                            )
                        }
                    }
                }
            }
        }

        // Suggested / Quick Add Tags
        Text(
            text = "Suggested Categories:",
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            suggestedTags.filter { !selectedTags.contains(it) }.forEach { tag ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, GoldOutline.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .clickable { addTag(tag) }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                        .testTag("suggested_tag_$tag")
                ) {
                    Text(
                        text = "+ #$tag",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Custom Tag Input
        AnimatedVisibility(visible = isAddingTag) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedTextField(
                    value = newTagInput,
                    onValueChange = { newTagInput = it },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("custom_tag_input"),
                    placeholder = { Text("Enter tag e.g. astral_gate", fontSize = 11.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EnochianGold,
                        unfocusedBorderColor = GoldOutline
                    )
                )
                Button(
                    onClick = { addTag(newTagInput) },
                    enabled = newTagInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = EnochianGold, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("add_custom_tag_button")
                ) {
                    Text("Add", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = { isAddingTag = false }) {
                    Icon(Icons.Default.Close, contentDescription = "Cancel", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        if (!isAddingTag) {
            TextButton(
                onClick = { isAddingTag = true },
                modifier = Modifier.testTag("open_custom_tag_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = EnochianGold, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Custom Category Tag", fontSize = 11.sp, color = EnochianGold, fontWeight = FontWeight.Bold)
            }
        }
    }
}
