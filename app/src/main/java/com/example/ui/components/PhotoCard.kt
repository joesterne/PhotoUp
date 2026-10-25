package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.PhotoItem
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.SecondaryCyan

@Composable
fun PhotoCard(
    photo: PhotoItem,
    isSelected: Boolean,
    onToggleSelect: () -> Unit,
    onInspect: () -> Unit,
    onDragStart: (PhotoItem) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) PrimaryViolet else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .pointerInput(photo.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { onDragStart(photo) },
                    onDragEnd = { onDragEnd() },
                    onDragCancel = { onDragEnd() },
                    onDrag = { _, _ -> }
                )
            }
            .clickable { onToggleSelect() }
            .testTag("photo_card_${photo.id}"),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = if (isSelected) 4.dp else 1.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.1f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                // If device URI is available, load via Coil; else render procedural artistic preview
                if (!photo.uriString.isNullOrBlank()) {
                    AsyncImage(
                        model = photo.uriString,
                        contentDescription = photo.title,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    ProceduralPhotoVisual(photo = photo)
                }

                // Top badges row: Auto-curated star badge & Checkbox
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (photo.isAutoSelected) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xDD1E1B4B),
                            modifier = Modifier.testTag("auto_picked_badge_${photo.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Auto Picked",
                                    tint = AccentAmber,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Top Pick ${(photo.aestheticScore * 100).toInt()}%",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xAA000000)
                        ) {
                            Text(
                                text = "${(photo.aestheticScore * 100).toInt()}% Score",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Checkbox
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) PrimaryViolet else Color(0x66000000)
                            )
                            .border(1.5.dp, if (isSelected) PrimaryViolet else Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Drag indicator affordance at bottom right of image
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0x77000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DragHandle,
                        contentDescription = "Hold to drag",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Info Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(
                    text = photo.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = photo.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = SecondaryCyan
                    )

                    Text(
                        text = photo.dateAdded,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Renders procedural artistic visual representation for sample photos.
 */
@Composable
fun ProceduralPhotoVisual(photo: PhotoItem, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val start = Color(photo.sampleGradientStart)
        val end = Color(photo.sampleGradientEnd)

        drawRect(
            brush = Brush.linearGradient(
                listOf(start, end)
            )
        )

        // Draw geometric silhouette features depending on category
        when (photo.category) {
            "Portrait" -> {
                drawCircle(
                    color = Color.White.copy(alpha = 0.25f),
                    radius = size.width * 0.28f,
                    center = center.copy(y = center.y - 20f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.4f),
                    radius = size.width * 0.18f,
                    center = center.copy(y = center.y - 20f)
                )
            }
            "Urban", "Night" -> {
                // Skyscraper silhouette
                drawRect(
                    color = Color.Black.copy(alpha = 0.35f),
                    topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.15f, size.height * 0.35f),
                    size = androidx.compose.ui.geometry.Size(size.width * 0.25f, size.height * 0.65f)
                )
                drawRect(
                    color = Color.Black.copy(alpha = 0.45f),
                    topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.45f, size.height * 0.2f),
                    size = androidx.compose.ui.geometry.Size(size.width * 0.35f, size.height * 0.8f)
                )
            }
            else -> {
                // Mountain / landscape curves
                drawCircle(
                    color = Color.White.copy(alpha = 0.2f),
                    radius = size.width * 0.35f,
                    center = androidx.compose.ui.geometry.Offset(size.width * 0.8f, size.height * 0.25f)
                )
            }
        }
    }
}
