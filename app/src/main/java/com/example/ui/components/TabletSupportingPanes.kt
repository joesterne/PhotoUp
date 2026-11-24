package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.AIProviderConfig
import com.example.data.PhotoItem
import com.example.data.StorageConfig
import com.example.data.StylePreset
import com.example.data.VariationResult
import com.example.ui.dragdrop.DragDropState
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.PrimaryVioletLight
import com.example.ui.theme.SecondaryCyan

/**
 * Tablet Supporting Pane for Tab 0 (Photo Library):
 * Displays active AI style preview, interactive batch queue shelf,
 * stylization controls, and immediate batch pipeline execution.
 */
@Composable
fun TabletBatchStudioPane(
    queuedPhotos: List<PhotoItem>,
    selectedIds: Set<String>,
    selectedStyle: StylePreset,
    styles: List<StylePreset>,
    aiConfig: AIProviderConfig,
    storageConfig: StorageConfig,
    dragDropState: DragDropState,
    onSelectStyle: (StylePreset) -> Unit,
    onOpenStyleDialog: () -> Unit,
    onRemoveFromQueue: (String) -> Unit,
    onReorderQueue: (Int, Int) -> Unit,
    onClearQueue: () -> Unit,
    onStartPipeline: () -> Unit,
    onOpenAIProviderSettings: () -> Unit,
    onOpenStorageSettings: () -> Unit,
    modifier: Modifier = Modifier,
    isProcessing: Boolean = false,
    processingProgress: Float = 0f
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("tablet_batch_studio_pane"),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Pane Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(PrimaryViolet.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = PrimaryVioletLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Batch Studio",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${queuedPhotos.size} selected for styling",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryVioletLight
                        )
                    }
                }

                if (queuedPhotos.isNotEmpty()) {
                    OutlinedButton(
                        onClick = onClearQueue,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("tablet_clear_queue_button")
                    ) {
                        Text("Clear (${queuedPhotos.size})", fontSize = 11.sp)
                    }
                }
            }

            // Active Style Preset Banner Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(selectedStyle.primaryColor)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenStyleDialog() }
                    .testTag("tablet_active_style_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(selectedStyle.primaryColor).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = selectedStyle.tag,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(selectedStyle.primaryColor),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Button(
                            onClick = onOpenStyleDialog,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet.copy(alpha = 0.2f)),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Change Style", fontSize = 11.sp, color = PrimaryVioletLight, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = selectedStyle.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = selectedStyle.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryCyan,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = selectedStyle.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Prompt preview chip
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "AI Modifiers: \"${selectedStyle.promptModifiers}\"",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }

            // Interactive Batch Queue Drop Shelf
            BatchQueueDropShelf(
                queuedPhotos = queuedPhotos,
                selectedStyle = selectedStyle,
                dragDropState = dragDropState,
                onRemoveFromQueue = onRemoveFromQueue,
                onReorderQueue = onReorderQueue,
                onStartPipeline = onStartPipeline,
                onClearQueue = onClearQueue,
                isProcessing = isProcessing,
                processingProgress = processingProgress,
                onOpenStyleSelection = onOpenStyleDialog,
                modifier = Modifier.fillMaxWidth()
            )

            // AI Pipeline Configuration & Telemetry
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Engine & Telemetry",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Provider", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { onOpenAIProviderSettings() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (aiConfig.activeProvider.isCloud) SecondaryCyan else AccentEmerald)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${aiConfig.activeProvider.displayName} (${aiConfig.selectedModel})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Storage Target", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { onOpenStorageSettings() }
                        ) {
                            Text(
                                text = if (storageConfig.isCloudSyncEnabled) "Cloud (${storageConfig.cloudProviderName})" else "Local Storage Only",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (storageConfig.isCloudSyncEnabled) SecondaryCyan else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            if (isProcessing) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .testTag("tablet_studio_loading_spinner"),
                                    strokeWidth = 2.dp,
                                    color = SecondaryCyan
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "AI Transformation in Progress...",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = SecondaryCyan
                                )
                            }
                            Text(
                                text = "${(processingProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryVioletLight
                            )
                        }
                        LinearProgressIndicator(
                            progress = { processingProgress.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .testTag("tablet_studio_progress_bar"),
                            color = SecondaryCyan,
                            trackColor = PrimaryViolet.copy(alpha = 0.25f)
                        )
                    }
                }
            }

            // Prominent Execution Button
            Button(
                onClick = onStartPipeline,
                enabled = queuedPhotos.isNotEmpty() && !isProcessing,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("tablet_start_batch_button")
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Transforming... ${(processingProgress * 100).toInt()}%",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                } else {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = AccentAmber)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (queuedPhotos.isNotEmpty()) "Morph ${queuedPhotos.size} Photos with ${selectedStyle.name}" else "Select Photos to Morph",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * Tablet Supporting Pane for Tab 1 (Popular Styles):
 * Provides expanded art-director preview for the focused style preset.
 */
@Composable
fun TabletStyleDetailPane(
    style: StylePreset,
    queuedPhotosCount: Int,
    onApplyToBatch: (StylePreset) -> Unit,
    onOpenCatalog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("tablet_style_detail_pane"),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Style Hero Banner with visual gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(style.primaryColor), Color(style.secondaryColor))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.align(Alignment.BottomStart)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = style.tag,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = style.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = style.subtitle,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            // Description
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Artistic Direction",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = style.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Prompt Expansion Modifiers
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "AI Prompt Expansion Engine",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "The following style modifiers are injected into the neural diffusion pipeline:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = style.promptModifiers,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = PrimaryVioletLight,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // Palette Swatches
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Palette Accents:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(style.primaryColor))
                        .border(1.5.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(style.secondaryColor))
                        .border(1.5.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                )
            }

            // Action Buttons
            Button(
                onClick = { onApplyToBatch(style) },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("tablet_apply_style_batch_button")
            ) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = AccentAmber)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (queuedPhotosCount > 0) "Apply to $queuedPhotosCount Queued Photos" else "Select This Style for Batch",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Tablet Supporting Pane for Tab 2 (Variations):
 * Provides an inline interactive Before/After comparison pane right next
 * to the gallery on tablet screens, without needing a modal popup.
 */
@Composable
fun TabletVariationDetailPane(
    variation: VariationResult,
    originalPhoto: PhotoItem?,
    onClose: () -> Unit,
    onSyncCloud: () -> Unit,
    onSaveToDevice: () -> Unit,
    onShare: () -> Unit = {},
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var splitPosition by remember { mutableFloatStateOf(0.5f) }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("tablet_variation_detail_pane"),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = variation.photoTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${variation.styleName} • ${variation.provider.displayName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryCyan
                    )
                }

                IconButton(onClick = onClose, modifier = Modifier.testTag("tablet_close_variation_button")) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close Inspector")
                }
            }

            // Side-by-side / Split Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black)
            ) {
                if (!variation.localFilePath.isNullOrBlank()) {
                    AsyncImage(
                        model = variation.localFilePath,
                        contentDescription = "Styled Variation",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(variation.primaryColor), Color(variation.secondaryColor))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = variation.styleName,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Prompt description
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "Synthesized Prompt", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = variation.promptUsed,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Primary Export Actions: Save to Gallery & Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSaveToDevice,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet),
                    modifier = Modifier.weight(1f).testTag("tablet_export_gallery_button")
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = "Save to Gallery", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save to Gallery", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onShare,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryCyan),
                    modifier = Modifier.weight(1f).testTag("tablet_share_variation_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Variation",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }

            // Secondary Actions: Sync cloud & Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onSyncCloud,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).testTag("tablet_sync_cloud_button")
                ) {
                    Icon(
                        imageVector = if (variation.isSyncedToCloud) Icons.Default.CloudDone else Icons.Default.CloudQueue,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (variation.isSyncedToCloud) "Synced" else "Sync Cloud", fontSize = 12.sp)
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("tablet_delete_variation_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Variation",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
