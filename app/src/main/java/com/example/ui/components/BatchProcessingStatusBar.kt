package com.example.ui.components

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.QueueItemProgress
import com.example.data.QueueItemStage
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.PrimaryVioletLight
import com.example.ui.theme.SecondaryCyan

/**
 * Batch processing status bar displaying dual transformation & upload animated circular
 * indicators as well as live circular progress rings for each item in the queue.
 */
@Composable
fun BatchProcessingStatusBar(
    isVisible: Boolean,
    title: String,
    subtitle: String,
    transformProgress: Float,
    uploadProgress: Float,
    queueItems: List<QueueItemProgress>,
    onCancel: () -> Unit,
    onSaveAllToGallery: (() -> Unit)? = null,
    onShareLatest: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(true) }

    val animatedTransformProgress by animateFloatAsState(
        targetValue = transformProgress.coerceIn(0f, 1f),
        animationSpec = tween(400),
        label = "anim_transform_progress"
    )

    val animatedUploadProgress by animateFloatAsState(
        targetValue = uploadProgress.coerceIn(0f, 1f),
        animationSpec = tween(400),
        label = "anim_upload_progress"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .testTag("batch_processing_status_bar"),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 8.dp,
            shadowElevation = 10.dp,
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                Brush.horizontalGradient(listOf(PrimaryViolet.copy(alpha = 0.6f), SecondaryCyan.copy(alpha = 0.6f)))
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Header: Title, Step Subtitle, Expand Toggle, Cancel Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(PrimaryVioletLight, SecondaryCyan))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = title.ifBlank { "Batch Processing Active..." },
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = subtitle.ifBlank { "Processing queue items..." },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { isExpanded = !isExpanded },
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("toggle_expand_status_bar_button")
                        ) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isExpanded) "Collapse" else "Expand",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        IconButton(
                            onClick = onCancel,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .testTag("cancel_batch_status_bar_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel Processing",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Visual Linear Progress Bar & Active Loading Spinner
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (animatedTransformProgress < 1.0f) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .testTag("batch_loading_spinner"),
                                    strokeWidth = 2.dp,
                                    color = PrimaryVioletLight
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Complete",
                                    tint = AccentEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (animatedTransformProgress < 1.0f) {
                                    "AI Transformation in Progress..."
                                } else {
                                    "AI Transformation Complete"
                                },
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (animatedTransformProgress < 1.0f) PrimaryVioletLight else AccentEmerald
                            )
                        }
                        Text(
                            text = "${(animatedTransformProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = SecondaryCyan
                        )
                    }

                    LinearProgressIndicator(
                        progress = { animatedTransformProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .testTag("batch_linear_progress_bar"),
                        color = if (animatedTransformProgress >= 1.0f) AccentEmerald else PrimaryVioletLight,
                        trackColor = PrimaryViolet.copy(alpha = 0.2f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Dual Animated Circular Progress Gauges: Transformation vs Upload
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Gauge 1: Transformation Progress
                    CircularProgressGauge(
                        label = "AI Transform",
                        progress = animatedTransformProgress,
                        color = PrimaryVioletLight,
                        trackColor = PrimaryViolet.copy(alpha = 0.2f),
                        icon = Icons.Default.AutoAwesome,
                        testTag = "transformation_circular_indicator"
                    )

                    // Directional transition arrow
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(18.dp)
                    )

                    // Gauge 2: Upload & Cloud Sync Progress
                    CircularProgressGauge(
                        label = "Cloud Upload",
                        progress = animatedUploadProgress,
                        color = SecondaryCyan,
                        trackColor = SecondaryCyan.copy(alpha = 0.2f),
                        icon = Icons.Default.CloudUpload,
                        testTag = "upload_circular_indicator"
                    )
                }

                // Collapsible Queue Items Live Carousel with Circular Indicators
                AnimatedVisibility(
                    visible = isExpanded && queueItems.isNotEmpty(),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Queue Item Progress (${queueItems.count { it.stage == QueueItemStage.COMPLETED }}/${queueItems.size} Completed)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "Live Status",
                                style = MaterialTheme.typography.labelSmall,
                                color = SecondaryCyan
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("queue_items_progress_row"),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            itemsIndexed(queueItems, key = { _, item -> item.photoId }) { index, item ->
                                QueueItemProgressCard(
                                    index = index,
                                    item = item
                                )
                            }
                        }

                        if (transformProgress >= 1.0f && (onSaveAllToGallery != null || onShareLatest != null)) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (onSaveAllToGallery != null) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = PrimaryViolet,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { onSaveAllToGallery() }
                                            .testTag("status_bar_save_to_gallery_button")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Save to Gallery",
                                                tint = Color.White,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Save to Gallery",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                                if (onShareLatest != null) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = SecondaryCyan,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { onShareLatest() }
                                            .testTag("status_bar_share_button")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = "Share",
                                                tint = Color.Black,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Share",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
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

/**
 * Animated Circular Progress Gauge widget displaying percentage in the center
 * and semantic label underneath.
 */
@Composable
fun CircularProgressGauge(
    label: String,
    progress: Float,
    color: Color,
    trackColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(54.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                color = color,
                strokeWidth = 4.5.dp,
                trackColor = trackColor
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (progress >= 1.0f) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Done",
                        tint = AccentEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = when {
                    progress >= 1.0f -> "Completed"
                    progress > 0f -> "Processing..."
                    else -> "Pending"
                },
                style = MaterialTheme.typography.labelSmall,
                color = if (progress >= 1.0f) AccentEmerald else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Compact card for each item in the processing queue featuring an animated circular
 * progress indicator wrapped around its thumbnail.
 */
@Composable
fun QueueItemProgressCard(
    index: Int,
    item: QueueItemProgress,
    modifier: Modifier = Modifier
) {
    val animatedTransform by animateFloatAsState(
        targetValue = item.transformProgress,
        animationSpec = tween(300),
        label = "item_transform"
    )

    val animatedUpload by animateFloatAsState(
        targetValue = item.uploadProgress,
        animationSpec = tween(300),
        label = "item_upload"
    )

    Surface(
        modifier = modifier
            .width(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp,
                when (item.stage) {
                    QueueItemStage.TRANSFORMING -> PrimaryViolet
                    QueueItemStage.UPLOADING -> SecondaryCyan
                    QueueItemStage.COMPLETED -> AccentEmerald
                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                },
                RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Circular Progress Frame encircling thumbnail
            Box(
                modifier = Modifier.size(46.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background thumbnail / procedural canvas
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(item.sampleGradientStart))
                ) {
                    if (!item.uriString.isNullOrBlank()) {
                        AsyncImage(
                            model = item.uriString,
                            contentDescription = item.photoTitle,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                // Animated Circular Ring Indicator
                when (item.stage) {
                    QueueItemStage.QUEUED -> {
                        CircularProgressIndicator(
                            progress = { 0f },
                            modifier = Modifier.fillMaxSize(),
                            strokeWidth = 2.5.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            trackColor = Color.Transparent
                        )
                        Icon(
                            imageVector = Icons.Default.HourglassEmpty,
                            contentDescription = "Queued",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    QueueItemStage.TRANSFORMING -> {
                        CircularProgressIndicator(
                            progress = { animatedTransform },
                            modifier = Modifier.fillMaxSize(),
                            strokeWidth = 3.dp,
                            color = PrimaryVioletLight,
                            trackColor = PrimaryViolet.copy(alpha = 0.2f)
                        )
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                    QueueItemStage.UPLOADING -> {
                        CircularProgressIndicator(
                            progress = { animatedUpload },
                            modifier = Modifier.fillMaxSize(),
                            strokeWidth = 3.dp,
                            color = SecondaryCyan,
                            trackColor = SecondaryCyan.copy(alpha = 0.2f)
                        )
                    }
                    QueueItemStage.COMPLETED -> {
                        CircularProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.fillMaxSize(),
                            strokeWidth = 3.dp,
                            color = AccentEmerald,
                            trackColor = Color.Transparent
                        )
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Done",
                            tint = AccentEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.photoTitle,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Stage status label
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = when (item.stage) {
                    QueueItemStage.QUEUED -> MaterialTheme.colorScheme.surfaceVariant
                    QueueItemStage.TRANSFORMING -> PrimaryViolet.copy(alpha = 0.2f)
                    QueueItemStage.UPLOADING -> SecondaryCyan.copy(alpha = 0.2f)
                    QueueItemStage.COMPLETED -> AccentEmerald.copy(alpha = 0.2f)
                },
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = when (item.stage) {
                        QueueItemStage.QUEUED -> "Queued"
                        QueueItemStage.TRANSFORMING -> "Styling ${(animatedTransform * 100).toInt()}%"
                        QueueItemStage.UPLOADING -> "Syncing ${(animatedUpload * 100).toInt()}%"
                        QueueItemStage.COMPLETED -> "Ready"
                    },
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (item.stage) {
                        QueueItemStage.QUEUED -> MaterialTheme.colorScheme.onSurfaceVariant
                        QueueItemStage.TRANSFORMING -> PrimaryVioletLight
                        QueueItemStage.UPLOADING -> SecondaryCyan
                        QueueItemStage.COMPLETED -> AccentEmerald
                    },
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
        }
    }
}
