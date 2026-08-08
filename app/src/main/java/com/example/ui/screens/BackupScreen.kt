package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.data.api.GitHubVaultClient
import kotlinx.coroutines.launch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InvocationRecord
import com.example.data.model.JournalEntry
import com.example.data.model.SavedSigil
import com.example.ui.theme.EnochianGold
import com.example.ui.theme.GoldOutline
import com.example.ui.theme.MysticViolet
import org.json.JSONArray
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupScreen(
    journalEntries: List<JournalEntry>,
    invocations: List<InvocationRecord>,
    savedSigils: List<SavedSigil>,
    isSyncingCloud: Boolean,
    lastCloudSyncTime: Long?,
    onTriggerCloudSync: () -> Unit,
    onSaveJournalEntry: (String, String, String, String, String, String, String, Int, String) -> Unit,
    onSaveInvocation: (String, String, String) -> Unit,
    onSaveSigil: (String, String, String, String, String, String) -> Unit,
    onExportJsonFile: () -> Unit = {},
    onImportJsonFile: () -> Unit = {}
) {
    val context = LocalContext.current
    var activeTab by remember { mutableIntStateOf(0) } // 0: GitHub Sync, 1: Export JSON, 2: Import JSON
    var jsonInputText by remember { mutableStateOf("") }
    var importStatusMessage by remember { mutableStateOf<String?>(null) }
    var importIsSuccess by remember { mutableStateOf(false) }

    val exportedJsonString = remember(journalEntries, invocations, savedSigils) {
        exportBackupDataToJson(journalEntries, invocations, savedSigils)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("backup_screen_container"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = CardBorder(GoldOutline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FolderZip,
                        contentDescription = "Backup Vault",
                        tint = EnochianGold,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Enochian Grimoire Backup & Sync",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = EnochianGold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Archive your ritual logs, sigil patterns, and invocation progress to GitHub or JSON backups.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Tab Selector Options
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            SegmentedButton(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = GoldOutline.copy(alpha = 0.3f),
                    activeContentColor = EnochianGold,
                    inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("GitHub Sync", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            SegmentedButton(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = GoldOutline.copy(alpha = 0.3f),
                    activeContentColor = EnochianGold,
                    inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("Export JSON", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            SegmentedButton(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = GoldOutline.copy(alpha = 0.3f),
                    activeContentColor = EnochianGold,
                    inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("Import JSON", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        // Active Option Panel Content
        when (activeTab) {
            0 -> GitHubSyncPanel(
                journalEntries = journalEntries,
                invocations = invocations,
                sigils = savedSigils,
                onSaveJournalEntry = onSaveJournalEntry,
                onSaveInvocation = onSaveInvocation,
                onSaveSigil = onSaveSigil
            )
            1 -> ExportJsonPanel(
                jsonContent = exportedJsonString,
                journalCount = journalEntries.size,
                invocationCount = invocations.size,
                sigilCount = savedSigils.size,
                onCopyJson = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Enochian Backup JSON", exportedJsonString)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Backup JSON copied to clipboard!", Toast.LENGTH_SHORT).show()
                },
                onExportJsonFile = onExportJsonFile
            )
            2 -> ImportJsonPanel(
                jsonText = jsonInputText,
                onJsonTextChanged = { jsonInputText = it },
                importStatusMessage = importStatusMessage,
                importIsSuccess = importIsSuccess,
                onPerformImport = {
                    val result = parseAndImportBackupJson(
                        jsonText = jsonInputText,
                        onSaveJournalEntry = onSaveJournalEntry,
                        onSaveInvocation = onSaveInvocation,
                        onSaveSigil = onSaveSigil
                    )
                    importStatusMessage = result.message
                    importIsSuccess = result.isSuccess
                    if (result.isSuccess) {
                        Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                    }
                },
                onImportJsonFile = onImportJsonFile
            )
        }
    }
}

@Composable
private fun CardBorder(color: Color) = androidx.compose.foundation.BorderStroke(1.dp, color)

@Composable
private fun GitHubSyncPanel(
    journalEntries: List<JournalEntry>,
    invocations: List<InvocationRecord>,
    sigils: List<SavedSigil>,
    onSaveJournalEntry: (String, String, String, String, String, String, String, Int, String) -> Unit,
    onSaveInvocation: (String, String, String) -> Unit,
    onSaveSigil: (String, String, String, String, String, String) -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("enochian_github_vault", Context.MODE_PRIVATE) }
    val coroutineScope = rememberCoroutineScope()

    var patToken by remember { mutableStateOf(prefs.getString("github_token", "") ?: "") }
    var githubRepo by remember { mutableStateOf(prefs.getString("github_repo", "theelegantthreat/enochiangrimoire") ?: "theelegantthreat/enochiangrimoire") }
    var githubPath by remember { mutableStateOf(prefs.getString("github_path", "journal.json") ?: "journal.json") }
    var isTokenVisible by remember { mutableStateOf(false) }

    var isBusy by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("Ready to connect to GitHub repository.") }
    // 0: Neutral/Info, 1: Success, 2: Warning, 3: Error
    var statusType by remember { mutableIntStateOf(0) }

    val primaryGold = EnochianGold
    val vibrantGreen = Color(0xFF4CAF50)

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardBorder(GoldOutline.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("github_synced_vault_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Modal Dialog / Card Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Cloud,
                        contentDescription = "GitHub Synced Vault",
                        tint = primaryGold,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "GitHub Synced Vault",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = primaryGold
                    )
                }
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }

            // A. Setup Instructions Callout Box
            Surface(
                color = GoldOutline.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                border = CardBorder(GoldOutline.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "How to generate a Personal Access Token (PAT):",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = primaryGold
                    )
                    Text(
                        text = "1. Go to GitHub Settings > Developer Settings > Personal Access Tokens (Classic).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "2. Generate a token with 'repo' scope authorized.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "3. Repository must exist and be formatted as 'username/repository-name'.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // B. Configuration Input Fields
            // Section 1: Authentication
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Authentication",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                OutlinedTextField(
                    value = patToken,
                    onValueChange = { patToken = it },
                    label = { Text("Personal Access Token (PAT)") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "Key Icon",
                            tint = primaryGold
                        )
                    },
                    visualTransformation = if (isTokenVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isTokenVisible = !isTokenVisible }) {
                            Icon(
                                imageVector = if (isTokenVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isTokenVisible) "Hide token" else "Show token",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Section 2: Repository Setup
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Repository Setup",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                OutlinedTextField(
                    value = githubRepo,
                    onValueChange = { githubRepo = it },
                    label = { Text("Repository (owner/repo-name)") },
                    placeholder = { Text("theelegantthreat/enochiangrimoire") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = "Repo Icon",
                            tint = primaryGold
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Section 3: Synced Target File
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Synced Target File",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                OutlinedTextField(
                    value = githubPath,
                    onValueChange = { githubPath = it },
                    label = { Text("File Path in Repo") },
                    placeholder = { Text("journal.json") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "File Icon",
                            tint = primaryGold
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Section 4: Sync & Verification Controls
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Sync & Verification Controls",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (!com.example.utils.NetworkUtils.isNetworkAvailable(context)) {
                                statusMessage = "Offline: No active internet connection available."
                                statusType = 3
                                Toast.makeText(context, "Offline: Please check your network connection.", Toast.LENGTH_SHORT).show()
                                return@OutlinedButton
                            }
                            if (patToken.isBlank() || githubRepo.isBlank() || githubPath.isBlank()) {
                                statusMessage = "Fill in GitHub Settings first"
                                statusType = 2
                                return@OutlinedButton
                            }
                            isBusy = true
                            statusMessage = "Testing connection to GitHub repository..."
                            statusType = 0
                            coroutineScope.launch {
                                val result = GitHubVaultClient.testConnection(
                                    patToken,
                                    githubRepo,
                                    githubPath,
                                    context
                                )
                                isBusy = false
                                when (result) {
                                    is GitHubVaultClient.VaultResult.Success -> {
                                        statusMessage = result.message
                                        statusType = 1
                                    }
                                    is GitHubVaultClient.VaultResult.Error -> {
                                        statusMessage = result.errorMessage
                                        statusType = 3
                                    }
                                }
                            }
                        },
                        enabled = !isBusy,
                        border = CardBorder(GoldOutline),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Wifi, contentDescription = null, modifier = Modifier.size(18.dp), tint = primaryGold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Test Conn", fontWeight = FontWeight.Bold, color = primaryGold)
                    }

                    Button(
                        onClick = {
                            if (patToken.isBlank() || githubRepo.isBlank() || githubPath.isBlank()) {
                                statusMessage = "Fill in GitHub Settings first"
                                statusType = 2
                                return@Button
                            }
                            prefs.edit()
                                .putString("github_token", patToken.trim())
                                .putString("github_repo", githubRepo.trim())
                                .putString("github_path", githubPath.trim())
                                .apply()
                            Toast.makeText(context, "GitHub credentials saved locally!", Toast.LENGTH_SHORT).show()
                            statusMessage = "GitHub credentials saved locally!"
                            statusType = 1
                        },
                        enabled = !isBusy,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryGold,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // D. Status Feedback Card
            val statusColor = when (statusType) {
                1 -> vibrantGreen // Green Checkmark Success
                2 -> Color(0xFFFFB300) // Amber Warning
                3 -> Color(0xFFE53935) // Red Error
                else -> primaryGold     // Neutral
            }
            val statusIcon = when (statusType) {
                1 -> Icons.Default.CheckCircle
                2 -> Icons.Default.Warning
                3 -> Icons.Default.Error
                else -> Icons.Default.Info
            }

            Surface(
                color = statusColor.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp),
                border = CardBorder(statusColor.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    if (isBusy) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = statusColor,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = "Status",
                            tint = statusColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = statusMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = statusColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // E. Primary Sync Action Buttons (Push Cloud & Pull Cloud)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        if (!com.example.utils.NetworkUtils.isNetworkAvailable(context)) {
                            statusMessage = "Offline: No active internet connection available."
                            statusType = 3
                            Toast.makeText(context, "Offline: Please check your network connection.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (patToken.isBlank() || githubRepo.isBlank() || githubPath.isBlank()) {
                            statusMessage = "Fill in GitHub Settings first"
                            statusType = 2
                            return@Button
                        }
                        isBusy = true
                        statusMessage = "Pushing data to GitHub..."
                        statusType = 0
                        coroutineScope.launch {
                            val jsonPayload = exportBackupDataToJson(
                                journalEntries,
                                invocations,
                                sigils
                            )
                            val result = GitHubVaultClient.pushBackupFile(
                                patToken,
                                githubRepo,
                                githubPath,
                                jsonPayload,
                                context
                            )
                            isBusy = false
                            when (result) {
                                is GitHubVaultClient.VaultResult.Success -> {
                                    val timestamp = java.text.SimpleDateFormat(
                                        "yyyy-MM-dd HH:mm:ss",
                                        java.util.Locale.getDefault()
                                    ).format(java.util.Date())
                                    statusMessage = "${result.message} ($timestamp)"
                                    statusType = 1
                                }
                                is GitHubVaultClient.VaultResult.Error -> {
                                    statusMessage = result.errorMessage
                                    statusType = 3
                                }
                            }
                        }
                    },
                    enabled = !isBusy,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryGold,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("push_database_up_button")
                ) {
                    Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Push Cloud", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        if (!com.example.utils.NetworkUtils.isNetworkAvailable(context)) {
                            statusMessage = "Offline: No active internet connection available."
                            statusType = 3
                            Toast.makeText(context, "Offline: Please check your network connection.", Toast.LENGTH_SHORT).show()
                            return@OutlinedButton
                        }
                        if (patToken.isBlank() || githubRepo.isBlank() || githubPath.isBlank()) {
                            statusMessage = "Fill in GitHub Settings first"
                            statusType = 2
                            return@OutlinedButton
                        }
                        isBusy = true
                        statusMessage = "Pulling data from GitHub..."
                        statusType = 0
                        coroutineScope.launch {
                            val result = GitHubVaultClient.pullBackupFile(
                                patToken,
                                githubRepo,
                                githubPath,
                                context
                            )
                            isBusy = false
                            when (result) {
                                is GitHubVaultClient.VaultResult.Success -> {
                                    try {
                                        val content = result.content
                                            ?: throw Exception("Empty remote file content")
                                        val importResult = parseAndImportBackupJson(
                                            jsonText = content,
                                            onSaveJournalEntry = onSaveJournalEntry,
                                            onSaveInvocation = onSaveInvocation,
                                            onSaveSigil = onSaveSigil
                                        )
                                        statusMessage = importResult.message
                                        statusType = if (importResult.isSuccess) 1 else 3
                                    } catch (e: Exception) {
                                        statusMessage = "Corrupted Remote JSON: ${e.localizedMessage ?: "Parsing failed without modifying local database"}"
                                        statusType = 3
                                    }
                                }
                                is GitHubVaultClient.VaultResult.Error -> {
                                    statusMessage = result.errorMessage
                                    statusType = 3
                                }
                            }
                        }
                    },
                    enabled = !isBusy,
                    border = CardBorder(GoldOutline),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("pull_database_down_button")
                ) {
                    Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(20.dp), tint = primaryGold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Pull Cloud", fontWeight = FontWeight.Bold, color = primaryGold)
                }
            }
        }
    }
}

@Composable
private fun StatPill(label: String, count: Int) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp),
        border = CardBorder(GoldOutline.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = EnochianGold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun ExportJsonPanel(
    jsonContent: String,
    journalCount: Int,
    invocationCount: Int,
    sigilCount: Int,
    onCopyJson: () -> Unit,
    onExportJsonFile: () -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardBorder(GoldOutline.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Export JSON",
                        tint = EnochianGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Export Backup JSON",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = EnochianGold
                    )
                }
            }

            Text(
                text = "Generates a structured JSON archive containing $journalCount journal entries, $invocationCount invocations, and $sigilCount saved sigils.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Button(
                onClick = onExportJsonFile,
                colors = ButtonDefaults.buttonColors(containerColor = EnochianGold, contentColor = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("export_json_file_button")
            ) {
                Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Export JSON to Downloads File", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onCopyJson,
                colors = ButtonDefaults.buttonColors(containerColor = EnochianGold, contentColor = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("copy_json_button")
            ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Copy JSON to Clipboard", fontWeight = FontWeight.Bold)
            }

            Text(
                text = "JSON Payload Preview:",
                style = MaterialTheme.typography.labelMedium,
                color = EnochianGold
            )

            SelectionContainer {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp)
                        .background(Color(0xFF121216), shape = RoundedCornerShape(8.dp))
                        .border(1.dp, GoldOutline.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = jsonContent,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Color(0xFFFFE082),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ImportJsonPanel(
    jsonText: String,
    onJsonTextChanged: (String) -> Unit,
    importStatusMessage: String?,
    importIsSuccess: Boolean,
    onPerformImport: () -> Unit,
    onImportJsonFile: () -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardBorder(GoldOutline.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Upload,
                    contentDescription = "Import JSON",
                    tint = EnochianGold,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Import JSON Backup",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = EnochianGold
                )
            }

            Text(
                text = "Select a JSON backup file from Downloads or paste a valid JSON string below to restore your vault.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Button(
                onClick = onImportJsonFile,
                colors = ButtonDefaults.buttonColors(containerColor = EnochianGold, contentColor = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("import_json_file_button")
            ) {
                Icon(imageVector = Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Select JSON File from Downloads", fontWeight = FontWeight.Bold)
            }

            OutlinedTextField(
                value = jsonText,
                onValueChange = onJsonTextChanged,
                placeholder = { Text("Or paste JSON backup payload here...", fontSize = 12.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .testTag("import_json_input"),
                textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 12.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EnochianGold,
                    unfocusedBorderColor = GoldOutline.copy(alpha = 0.5f)
                )
            )

            Button(
                onClick = onPerformImport,
                enabled = jsonText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = EnochianGold, contentColor = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("import_json_button")
            ) {
                Icon(imageVector = Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Validate & Restore Data", fontWeight = FontWeight.Bold)
            }

            AnimatedVisibility(visible = importStatusMessage != null) {
                val bannerColor = if (importIsSuccess) Color(0xFF1B5E20) else Color(0xFFB71C1C)
                Surface(
                    color = bannerColor.copy(alpha = 0.2f),
                    border = CardBorder(bannerColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = importStatusMessage ?: "",
                        color = if (importIsSuccess) Color(0xFF81C784) else Color(0xFFEF9A9A),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

fun exportBackupDataToJson(
    journalEntries: List<JournalEntry>,
    invocations: List<InvocationRecord>,
    sigils: List<SavedSigil>
): String {
    val root = JSONObject()
    root.put("app", "Enochian Grimoire")
    root.put("version", "1.0")
    root.put("exportedAt", System.currentTimeMillis())

    val journalArr = JSONArray()
    journalEntries.forEach { entry ->
        val obj = JSONObject()
        obj.put("title", entry.title)
        obj.put("keyOrCallUsed", entry.keyOrCallUsed)
        obj.put("planetaryHour", entry.planetaryHour)
        obj.put("moonPhase", entry.moonPhase)
        obj.put("intention", entry.intention)
        obj.put("outcomeNotes", entry.outcomeNotes)
        obj.put("insights", entry.insights)
        obj.put("rating", entry.rating)
        obj.put("mood", entry.mood)
        journalArr.put(obj)
    }
    root.put("journalEntries", journalArr)

    val invocationArr = JSONArray()
    invocations.forEach { inv ->
        val obj = JSONObject()
        obj.put("callNumber", inv.callNumber)
        obj.put("callTitle", inv.callTitle)
        obj.put("durationSeconds", inv.durationSeconds)
        obj.put("vibrationCount", inv.vibrationCount)
        obj.put("watchtower", inv.watchtower)
        obj.put("notes", inv.notes)
        invocationArr.put(obj)
    }
    root.put("invocations", invocationArr)

    val sigilArr = JSONArray()
    sigils.forEach { sig ->
        val obj = JSONObject()
        obj.put("title", sig.title)
        obj.put("originalPhrase", sig.originalPhrase)
        obj.put("eNochianLetters", sig.eNochianLetters)
        obj.put("sigilMethod", sig.sigilMethod)
        obj.put("pointsJson", sig.pointsJson)
        obj.put("colorHex", sig.colorHex)
        sigilArr.put(obj)
    }
    root.put("savedSigils", sigilArr)

    return root.toString(2)
}

data class ImportResult(val isSuccess: Boolean, val message: String)

fun parseAndImportBackupJson(
    jsonText: String,
    onSaveJournalEntry: (String, String, String, String, String, String, String, Int, String) -> Unit,
    onSaveInvocation: (String, String, String) -> Unit,
    onSaveSigil: (String, String, String, String, String, String) -> Unit
): ImportResult {
    return try {
        val root = JSONObject(jsonText)
        var journalImportCount = 0
        var invocationImportCount = 0
        var sigilImportCount = 0

        if (root.has("journalEntries")) {
            val journalArr = root.getJSONArray("journalEntries")
            for (i in 0 until journalArr.length()) {
                val obj = journalArr.getJSONObject(i)
                onSaveJournalEntry(
                    obj.optString("title", "Imported Entry"),
                    obj.optString("keyOrCallUsed", "General Call"),
                    obj.optString("planetaryHour", "Sol"),
                    obj.optString("moonPhase", "Waxing Crescent"),
                    obj.optString("intention", ""),
                    obj.optString("outcomeNotes", ""),
                    obj.optString("insights", ""),
                    obj.optInt("rating", 3),
                    obj.optString("mood", "Serene 🕯️")
                )
                journalImportCount++
            }
        }

        if (root.has("invocations")) {
            val invArr = root.getJSONArray("invocations")
            for (i in 0 until invArr.length()) {
                val obj = invArr.getJSONObject(i)
                onSaveInvocation(
                    obj.optString("callTitle", "1st Key: Divinity"),
                    obj.optString("watchtower", "East - Air"),
                    obj.optString("notes", "Imported invocation record")
                )
                invocationImportCount++
            }
        }

        if (root.has("savedSigils")) {
            val sigArr = root.getJSONArray("savedSigils")
            for (i in 0 until sigArr.length()) {
                val obj = sigArr.getJSONObject(i)
                onSaveSigil(
                    obj.optString("title", "Imported Sigil"),
                    obj.optString("originalPhrase", "INTENTION"),
                    obj.optString("eNochianLetters", "I-N-T-E-N-T-I-O-N"),
                    obj.optString("sigilMethod", "Traditional Rose Cross"),
                    obj.optString("pointsJson", "[]"),
                    obj.optString("colorHex", "#FFD700")
                )
                sigilImportCount++
            }
        }

        ImportResult(
            isSuccess = true,
            message = "Successfully restored $journalImportCount journal entries, $invocationImportCount invocations, and $sigilImportCount sigils!"
        )
    } catch (e: Exception) {
        ImportResult(
            isSuccess = false,
            message = "Invalid JSON format: ${e.localizedMessage ?: "Parsing error"}"
        )
    }
}

private fun formatTimestamp(millis: Long): String {
    val sdf = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(millis))
}
