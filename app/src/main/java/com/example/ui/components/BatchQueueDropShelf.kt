package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.PhotoItem
import com.example.data.StylePreset
import com.example.ui.dragdrop.DragDropState
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.SecondaryCyan

/**
 * Interactive visual drag-and-drop component using Compose drag gesture modifiers
 * to queue, reorder, and trigger batches of photos for the processing pipeline.
 */
@Composable
fun BatchQueueDropShelf(
    queuedPhotos: List<PhotoItem>,
    selectedStyle: StylePreset,
    dragDropState: DragDropState,
    onRemoveFromQueue: (String) -> Unit,
    onReorderQueue: (fromIndex: Int, toIndex: Int) -> Unit,
    onStartPipeline: () -> Unit,
    onClearQueue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDragging = dragDropState.isDragging
    val hoverTarget = dragDropState.activeHoverTargetId

    val isQueueHovered = hoverTarget == "target_queue_drop"
    val isDirectProcessHovered = hoverTarget == "target_direct_process"

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isDragging) 1.025f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val queueBorderColor by animateColorAsState(
        targetValue = when {
            isQueueHovered -> SecondaryCyan
            isDragging -> Color(selectedStyle.primaryColor)
            queuedPhotos.isNotEmpty() -> PrimaryViolet
            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        },
        label = "queue_border"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = if (isQueueHovered || isDirectProcessHovered) 2.5.dp else 1.2.dp,
                color = queueBorderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .testTag("batch_queue_drop_shelf"),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isDragging) 0.96f else 0.85f),
        tonalElevation = if (isDragging) 8.dp else 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: Drop Zone Indicators & Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(selectedStyle.primaryColor),
                                        Color(selectedStyle.secondaryColor)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Queue,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Pipeline Batch Queue",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = PrimaryViolet.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "${queuedPhotos.size} photos",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryViolet,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = when {
                                isQueueHovered -> "Release to add to pipeline queue!"
                                isDirectProcessHovered -> "Release to trigger instant morph!"
                                isDragging -> "Hover over target below to drop"
                                queuedPhotos.isNotEmpty() -> "Ready to process with ${selectedStyle.name}"
                                else -> "Drag photos here using long-press to build your batch"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isQueueHovered || isDirectProcessHovered) SecondaryCyan else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (queuedPhotos.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Clear",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .clickable { onClearQueue() }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                                .testTag("clear_queue_button")
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Button(
                            onClick = onStartPipeline,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("start_pipeline_from_shelf_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Morph (${queuedPhotos.size})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Two Interactive Drop Targets
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Drop Target 1: Add to Queue
                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .height(64.dp)
                        .scale(if (isQueueHovered) 1.03f else 1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isQueueHovered) SecondaryCyan.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                        )
                        .border(
                            width = if (isQueueHovered) 2.dp else 1.dp,
                            color = if (isQueueHovered) SecondaryCyan else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .onGloballyPositioned { coordinates ->
                            dragDropState.registerDropTarget("target_queue_drop", coordinates.boundsInWindow())
                        }
                        .testTag("target_queue_drop"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = null,
                            tint = if (isQueueHovered) SecondaryCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isQueueHovered) "DROP TO QUEUE" else "Drop Zone (Queue)",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isQueueHovered) SecondaryCyan else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Accumulates in batch shelf",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Drop Target 2: Instant Morph Pipeline
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(64.dp)
                        .scale(if (isDirectProcessHovered) 1.03f else 1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isDirectProcessHovered) PrimaryViolet.copy(alpha = 0.25f)
                            else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                        )
                        .border(
                            width = if (isDirectProcessHovered) 2.dp else 1.dp,
                            color = if (isDirectProcessHovered) PrimaryViolet else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .onGloballyPositioned { coordinates ->
                            dragDropState.registerDropTarget("target_direct_process", coordinates.boundsInWindow())
                        }
                        .testTag("target_direct_process"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = null,
                            tint = if (isDirectProcessHovered) PrimaryViolet else AccentAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = if (isDirectProcessHovered) "DROP TO RUN" else "Instant Morph",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isDirectProcessHovered) PrimaryViolet else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Runs immediately",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Queued Thumbnails Horizontal Shelf
            if (queuedPhotos.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Queued Items (Drag handle to reorder)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("queued_items_carousel"),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(queuedPhotos, key = { _, item -> item.id }) { index, photo ->
                        QueuedPhotoItemChip(
                            index = index,
                            photo = photo,
                            onRemove = { onRemoveFromQueue(photo.id) },
                            onDragReorderLeft = {
                                if (index > 0) onReorderQueue(index, index - 1)
                            },
                            onDragReorderRight = {
                                if (index < queuedPhotos.size - 1) onReorderQueue(index, index + 1)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual queued photo chip in the batch shelf with drag gesture reorder modifiers.
 */
@Composable
fun QueuedPhotoItemChip(
    index: Int,
    photo: PhotoItem,
    onRemove: () -> Unit,
    onDragReorderLeft: () -> Unit,
    onDragReorderRight: () -> Unit,
    modifier: Modifier = Modifier
) {
    var dragDeltaX by remember { mutableFloatStateOf(0f) }

    Surface(
        modifier = modifier
            .width(110.dp)
            .height(110.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .pointerInput(photo.id) {
                // Drag gesture modifier to reorder within batch
                detectDragGestures(
                    onDragStart = { dragDeltaX = 0f },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragDeltaX += dragAmount.x
                        if (dragDeltaX > 50f) {
                            onDragReorderRight()
                            dragDeltaX = 0f
                        } else if (dragDeltaX < -50f) {
                            onDragReorderLeft()
                            dragDeltaX = 0f
                        }
                    }
                )
            }
            .testTag("queued_chip_${photo.id}"),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (!photo.uriString.isNullOrBlank()) {
                AsyncImage(
                    model = photo.uriString,
                    contentDescription = photo.title,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                ProceduralPhotoVisual(photo = photo)
            }

            // Top gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .background(
                        Brush.verticalGradient(listOf(Color(0xBB000000), Color.Transparent))
                    )
            )

            // Index tag at top-left
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp),
                shape = RoundedCornerShape(4.dp),
                color = Color(0xDD000000)
            ) {
                Text(
                    text = "#${index + 1}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }

            // Delete button at top-right
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(3.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color(0xDD000000))
                    .clickable { onRemove() }
                    .testTag("remove_queued_item_${photo.id}"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }

            // Bottom title and drag handle
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color(0xCC000000))
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = photo.title,
                    fontSize = 10.sp,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.DragIndicator,
                    contentDescription = "Drag to reorder",
                    tint = Color.LightGray,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
