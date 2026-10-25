package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PhotoItem
import com.example.data.StylePreset
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.PrimaryVioletLight
import com.example.ui.theme.SecondaryCyan

@Composable
fun DragDropBatchTarget(
    selectedCount: Int,
    selectedStyle: StylePreset,
    draggedPhoto: PhotoItem?,
    draggedStyle: StylePreset?,
    onDropPhoto: (PhotoItem) -> Unit,
    onDropStyle: (StylePreset) -> Unit,
    onStartBatch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDragging = draggedPhoto != null || draggedStyle != null

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isDragging) 1.02f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            draggedStyle != null -> Color(selectedStyle.primaryColor)
            draggedPhoto != null -> SecondaryCyan
            selectedCount > 0 -> PrimaryVioletLight
            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        },
        label = "border_color"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .scale(pulseScale)
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = if (isDragging) 2.5.dp else 1.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable {
                if (draggedPhoto != null) {
                    onDropPhoto(draggedPhoto)
                } else if (draggedStyle != null) {
                    onDropStyle(draggedStyle)
                }
            }
            .testTag("drag_drop_batch_target"),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isDragging) 0.95f else 0.7f),
        tonalElevation = if (isDragging) 8.dp else 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
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
                            imageVector = Icons.Default.Layers,
                            contentDescription = "Batch Drop Target",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Drag & Drop Batch Zone",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.DragIndicator,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = when {
                                draggedPhoto != null -> "Release to add '${draggedPhoto.title}' to batch"
                                draggedStyle != null -> "Release to apply '${draggedStyle.name}' to batch"
                                selectedCount > 0 -> "$selectedCount photos queued for ${selectedStyle.name}"
                                else -> "Drop photos or styles here for fast batch transformation"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDragging) SecondaryCyan else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (selectedCount > 0) {
                    Button(
                        onClick = onStartBatch,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryViolet
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("run_batch_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Morph ($selectedCount)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
