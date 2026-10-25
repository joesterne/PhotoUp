package com.example.ui.dragdrop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.ProceduralPhotoVisual
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.SecondaryCyan

@Composable
fun DragGhostOverlay(
    dragDropState: DragDropState,
    modifier: Modifier = Modifier
) {
    val photo = dragDropState.draggedPhoto

    AnimatedVisibility(
        visible = dragDropState.isDragging && photo != null,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier.fillMaxSize()
    ) {
        if (photo != null) {
            Box(modifier = Modifier.fillMaxSize()) {
                Surface(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (dragDropState.dragPosition.x - 120).toInt().coerceAtLeast(0),
                                (dragDropState.dragPosition.y - 120).toInt().coerceAtLeast(0)
                            )
                        }
                        .size(160.dp, 160.dp)
                        .rotate(-3.5f)
                        .shadow(16.dp, RoundedCornerShape(18.dp))
                        .clip(RoundedCornerShape(18.dp))
                        .border(
                            2.5.dp,
                            Brush.linearGradient(listOf(SecondaryCyan, PrimaryViolet)),
                            RoundedCornerShape(18.dp)
                        )
                        .testTag("floating_drag_ghost_card"),
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
                    tonalElevation = 12.dp
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

                        // Gradient scrim for text readability
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, Color(0xCC000000))
                                    )
                                )
                        )

                        // Top badge: Dragging
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xDD7C3AED)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Queueing",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // Bottom Title
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = photo.title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = AccentAmber,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${(photo.aestheticScore * 100).toInt()}% Aesthetic Score",
                                    fontSize = 9.sp,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
