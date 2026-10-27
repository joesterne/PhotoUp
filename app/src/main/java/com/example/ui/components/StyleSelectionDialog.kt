package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.StylePreset
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.PrimaryVioletLight
import com.example.ui.theme.SecondaryCyan

/**
 * Selection Dialog component allowing users to choose from various AI styling
 * presets for their images with category filtering, detailed style descriptions,
 * prompt previews, and direct batch application.
 */
@Composable
fun StyleSelectionDialog(
    styles: List<StylePreset>,
    currentStyle: StylePreset,
    queuedPhotosCount: Int,
    onDismiss: () -> Unit,
    onSelectStyle: (StylePreset) -> Unit,
    onApplyAndMorph: ((StylePreset) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedPreset by remember { mutableStateOf(currentStyle) }
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf("All", "Trending", "Fine Art", "Animation & 3D", "Retro", "Graphic")

    val filteredStyles = styles.filter { preset ->
        val matchesCategory = when (selectedCategory) {
            "All" -> true
            "Trending" -> preset.tag.contains("Trending", ignoreCase = true) || preset.tag.contains("Favorite", ignoreCase = true)
            "Fine Art" -> preset.id in listOf("renaissance", "watercolor")
            "Animation & 3D" -> preset.id in listOf("anime_ghibli", "pixar_3d")
            "Retro" -> preset.id in listOf("vintage_polaroid", "synthwave")
            "Graphic" -> preset.id in listOf("pop_art", "cyberpunk")
            else -> true
        }

        val matchesSearch = if (searchQuery.isBlank()) true else {
            preset.name.contains(searchQuery, ignoreCase = true) ||
                    preset.subtitle.contains(searchQuery, ignoreCase = true) ||
                    preset.description.contains(searchQuery, ignoreCase = true) ||
                    preset.tag.contains(searchQuery, ignoreCase = true)
        }

        matchesCategory && matchesSearch
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .widthIn(max = 560.dp)
                .clip(RoundedCornerShape(24.dp))
                .testTag("style_selection_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(selectedPreset.primaryColor),
                                    Color(selectedPreset.secondaryColor)
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x33FFFFFF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getStyleIcon(selectedPreset.iconKey),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "Choose AI Styling Preset",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (queuedPhotosCount > 0) {
                                        "Styling $queuedPhotosCount selected photos with ${selectedPreset.name}"
                                    } else {
                                        "Select an aesthetic style for batch generation"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xEEFFFFFF)
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0x44000000))
                                .testTag("close_style_dialog_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Dialog",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Filter & Search Controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Category Filter Chips
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { category ->
                            val isSelected = selectedCategory == category
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = category },
                                label = {
                                    Text(
                                        text = category,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                leadingIcon = if (category == "All") {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Palette,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryViolet.copy(alpha = 0.2f),
                                    selectedLabelColor = PrimaryVioletLight
                                ),
                                modifier = Modifier.testTag("dialog_category_chip_$category")
                            )
                        }
                    }

                    // Quick Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("style_dialog_search_input"),
                        placeholder = {
                            Text(
                                "Search styles (e.g. Cyberpunk, Ghibli, Oil...)",
                                fontSize = 12.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { searchQuery = "" },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear search",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // Scrollable List of Style Presets
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("style_dialog_list"),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (filteredStyles.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.FilterList,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No styling presets match \"$searchQuery\"",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(filteredStyles, key = { it.id }) { preset ->
                            val isChosen = preset.id == selectedPreset.id
                            PresetDialogItem(
                                preset = preset,
                                isSelected = isChosen,
                                onClick = { selectedPreset = preset }
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // Bottom Action Footer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("dialog_cancel_button")
                    ) {
                        Text("Cancel")
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                onSelectStyle(selectedPreset)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("dialog_select_preset_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Select Preset")
                        }

                        if (queuedPhotosCount > 0 && onApplyAndMorph != null) {
                            Button(
                                onClick = {
                                    onSelectStyle(selectedPreset)
                                    onApplyAndMorph(selectedPreset)
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SecondaryCyan),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("dialog_apply_morph_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Morph ($queuedPhotosCount)",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetDialogItem(
    preset: StylePreset,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = Color(preset.primaryColor)
    val secondaryColor = Color(preset.secondaryColor)

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) primaryColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
        label = "preset_border"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .testTag("dialog_preset_item_${preset.id}"),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) primaryColor.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = if (isSelected) 4.dp else 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Color swatch / icon thumbnail
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.linearGradient(listOf(primaryColor, secondaryColor))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getStyleIcon(preset.iconKey),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Title and Subtitle
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = preset.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = primaryColor.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = preset.tag,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = preset.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Radio Button indicator
                RadioButton(
                    selected = isSelected,
                    onClick = onClick,
                    colors = RadioButtonDefaults.colors(selectedColor = primaryColor),
                    modifier = Modifier.testTag("radio_preset_${preset.id}")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Aesthetic Description
            Text(
                text = preset.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // AI Prompt Modifiers Preview Box
            AnimatedVisibility(visible = isSelected) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AccentAmber,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AI Prompt Modifier:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentAmber
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = preset.promptModifiers,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
