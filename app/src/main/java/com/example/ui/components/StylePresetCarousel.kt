package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StylePreset
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.PrimaryVioletLight

/**
 * Interactive horizontal carousel component allowing users to quickly browse,
 * preview, and switch between popular AI styling presets for their photo batch.
 */
@Composable
fun StylePresetCarousel(
    styles: List<StylePreset>,
    selectedStyle: StylePreset,
    onSelectStyle: (StylePreset) -> Unit,
    onOpenStyleDialog: () -> Unit,
    modifier: Modifier = Modifier,
    queuedPhotosCount: Int = 0
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("style_preset_carousel_section")
    ) {
        // Carousel Header with Current Preset & "Browse All" trigger
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onOpenStyleDialog() }
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
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
                        imageVector = getStyleIcon(selectedStyle.iconKey),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "AI Style Presets",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(selectedStyle.primaryColor).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = selectedStyle.name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(selectedStyle.primaryColor),
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = if (queuedPhotosCount > 0) {
                            "Applying to $queuedPhotosCount queued photos • Tap to switch"
                        } else {
                            selectedStyle.subtitle
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedButton(
                onClick = onOpenStyleDialog,
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier
                    .height(32.dp)
                    .testTag("open_style_dialog_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Open Style Selection Dialog",
                    modifier = Modifier.size(13.dp),
                    tint = PrimaryVioletLight
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Presets (${styles.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryVioletLight
                )
            }
        }

        // Horizontal Carousel Items
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .testTag("style_preset_lazy_row"),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(styles, key = { it.id }) { style ->
                val isSelected = style.id == selectedStyle.id
                StyleCarouselCard(
                    style = style,
                    isSelected = isSelected,
                    onClick = { onSelectStyle(style) },
                    onLongClick = onOpenStyleDialog
                )
            }

            // End card: "Explore All" card button
            item {
                Surface(
                    modifier = Modifier
                        .width(108.dp)
                        .height(118.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = 1.5.dp,
                            color = PrimaryViolet.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onOpenStyleDialog() }
                        .testTag("style_carousel_view_all_card"),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(PrimaryViolet.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CollectionsBookmark,
                                contentDescription = null,
                                tint = PrimaryVioletLight,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Explore All",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Full Catalog",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StyleCarouselCard(
    style: StylePreset,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = Color(style.primaryColor)
    val secondaryColor = Color(style.secondaryColor)

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.03f else 1.0f,
        animationSpec = spring(),
        label = "carousel_scale"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) AccentAmber else Color.Transparent,
        label = "carousel_border"
    )

    Surface(
        modifier = modifier
            .width(136.dp)
            .height(118.dp)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isSelected) 2.5.dp else 1.dp,
                color = if (isSelected) AccentAmber else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .testTag("style_carousel_card_${style.id}"),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = if (isSelected) 8.dp else 2.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(primaryColor, secondaryColor)
                    )
                )
        ) {
            // Subtle scrim at the bottom for readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0x66000000),
                                Color(0xCC000000)
                            )
                        )
                    )
            )

            // Content Column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: Tag badge and Selection Check
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0x99000000)
                    ) {
                        Text(
                            text = style.tag,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(AccentAmber),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.Black,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }

                // Middle Icon
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getStyleIcon(style.iconKey),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // Bottom Labels
                Column {
                    Text(
                        text = style.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = style.subtitle,
                        fontSize = 9.sp,
                        color = Color(0xEEFFFFFF),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
