package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AIProviderConfig
import com.example.data.StorageConfig
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.PrimaryVioletLight
import com.example.ui.theme.SecondaryCyan

@Composable
fun SettingsDrawerContent(
    aiConfig: AIProviderConfig,
    storageConfig: StorageConfig,
    onOpenAIProviderSettings: () -> Unit,
    onOpenStorageSettings: () -> Unit,
    onOpenStyleDialog: () -> Unit,
    onLaunchCamera: () -> Unit,
    onScanLibrary: () -> Unit,
    onAutoCurate: () -> Unit,
    onSyncAllCloud: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier
            .width(320.dp)
            .fillMaxHeight()
            .testTag("settings_modal_drawer_sheet"),
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerContentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // Header Banner with gradient and branding
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF0F172A))
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 28.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(PrimaryViolet, SecondaryCyan))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        IconButton(
                            onClick = onClose,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0x33FFFFFF))
                                .testTag("close_settings_drawer_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Menu",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "StyleMorph",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = PrimaryViolet
                        ) {
                            Text(
                                text = "v1.0",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "AI Photo Studio & Batch Engine",
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Section 1: AI Provider & Processing Configuration
            DrawerSectionHeader(title = "AI & ENGINE SETTINGS")

            NavigationDrawerItem(
                label = {
                    Column {
                        Text(
                            text = "AI Provider & Model",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${aiConfig.activeProvider.displayName} (${aiConfig.selectedModel})",
                            fontSize = 11.sp,
                            color = SecondaryCyan
                        )
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = PrimaryVioletLight
                    )
                },
                badge = {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "${(aiConfig.stylizationStrength * 100).toInt()}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                },
                selected = false,
                onClick = {
                    onClose()
                    onOpenAIProviderSettings()
                },
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 2.dp)
                    .testTag("drawer_ai_provider_item")
            )

            NavigationDrawerItem(
                label = {
                    Column {
                        Text(
                            text = "AI Styling Presets",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Explore 8 popular visual styles",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = AccentAmber
                    )
                },
                selected = false,
                onClick = {
                    onClose()
                    onOpenStyleDialog()
                },
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 2.dp)
                    .testTag("drawer_style_presets_item")
            )

            // Section 2: Storage & Cloud Connectivity
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(8.dp))

            DrawerSectionHeader(title = "STORAGE & CONNECTIVITY")

            NavigationDrawerItem(
                label = {
                    Column {
                        Text(
                            text = "Cloud Sync Destination",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (storageConfig.isCloudSyncEnabled) storageConfig.cloudProviderName else "Local Only",
                            fontSize = 11.sp,
                            color = if (storageConfig.isCloudSyncEnabled) AccentEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (storageConfig.isCloudSyncEnabled) Icons.Default.CloudDone else Icons.Default.Folder,
                        contentDescription = null,
                        tint = if (storageConfig.isCloudSyncEnabled) SecondaryCyan else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                selected = false,
                onClick = {
                    onClose()
                    onOpenStorageSettings()
                },
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 2.dp)
                    .testTag("drawer_storage_item")
            )

            NavigationDrawerItem(
                label = {
                    Text(text = "Sync Pending to Cloud", fontSize = 14.sp)
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = SecondaryCyan
                    )
                },
                selected = false,
                onClick = {
                    onClose()
                    onSyncAllCloud()
                },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
            )

            // Section 3: Camera & Library Tools
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(8.dp))

            DrawerSectionHeader(title = "STUDIO ACTIONS")

            NavigationDrawerItem(
                label = {
                    Text(text = "CameraX Photo Capture", fontSize = 14.sp)
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null,
                        tint = PrimaryVioletLight
                    )
                },
                selected = false,
                onClick = {
                    onClose()
                    onLaunchCamera()
                },
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 2.dp)
                    .testTag("drawer_camera_item")
            )

            NavigationDrawerItem(
                label = {
                    Text(text = "Scan Photo Library", fontSize = 14.sp)
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                selected = false,
                onClick = {
                    onClose()
                    onScanLibrary()
                },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
            )

            NavigationDrawerItem(
                label = {
                    Text(text = "Re-Run Auto-Curation", fontSize = 14.sp)
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = AccentAmber
                    )
                },
                selected = false,
                onClick = {
                    onClose()
                    onAutoCurate()
                },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
            )

            // Section 4: About & Privacy
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "About StyleMorph",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Combines Google Gemini API, CameraX in-app capture, and an accelerated on-device neural canvas engine with drag-and-drop batch queueing.",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DrawerSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 11.sp,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
    )
}
