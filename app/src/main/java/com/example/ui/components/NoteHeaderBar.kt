package com.example.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.theme.EnochianGold

private data class MenuNavigationItem(
    val title: String,
    val route: String,
    val icon: ImageVector,
    val testTag: String
)

private val menuNavItems = listOf(
    MenuNavigationItem("Enochian Grimoire Backup & Sync", "backup", Icons.Default.CloudSync, "menu_nav_backup"),
    MenuNavigationItem("Enochian Lunar Ritual Calendar", "lunar", Icons.Default.NightlightRound, "menu_nav_lunar"),
    MenuNavigationItem("Enochian Sanctum Vault", "database", Icons.AutoMirrored.Filled.MenuBook, "menu_nav_database"),
    MenuNavigationItem("Enochian Sigil Generator", "sigil", Icons.Default.AutoAwesome, "menu_nav_sigil"),
    MenuNavigationItem("Invocation Progress Tracker", "tracker", Icons.Default.Timer, "menu_nav_tracker"),
    MenuNavigationItem("Journal Calendar", "calendar", Icons.Default.CalendarMonth, "menu_nav_calendar"),
    MenuNavigationItem("Ritual Outcome Journal", "journal", Icons.AutoMirrored.Filled.NoteAdd, "menu_nav_journal")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteHeaderBar(
    title: String = "Enochian Grimoire",
    onExportJson: () -> Unit = {},
    onImportJson: () -> Unit = {},
    onNavigateToScreen: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = EnochianGold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.testTag("hamburger_menu_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = EnochianGold
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.testTag("hamburger_dropdown_menu")
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Export JSON",
                                fontWeight = FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Export JSON",
                                tint = EnochianGold
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onExportJson()
                        },
                        modifier = Modifier.testTag("menu_export_json")
                    )

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Import JSON",
                                fontWeight = FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Upload,
                                contentDescription = "Import JSON",
                                tint = EnochianGold
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onImportJson()
                        },
                        modifier = Modifier.testTag("menu_import_json")
                    )

                    HorizontalDivider(
                        color = EnochianGold,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    menuNavItems.forEach { item ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = item.title,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = EnochianGold
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onNavigateToScreen(item.route)
                            },
                            modifier = Modifier.testTag(item.testTag)
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = EnochianGold,
            navigationIconContentColor = EnochianGold
        ),
        modifier = modifier.testTag("note_header_bar")
    )
}
