package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GridOff
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.example.data.StylePreset
import com.example.ui.components.StyleSelectionDialog
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.PrimaryViolet
import com.example.ui.theme.PrimaryVioletLight
import com.example.ui.theme.SecondaryCyan
import com.example.viewmodel.StyleMorphViewModel
import java.io.File

/**
 * CameraScreen: Full-screen interactive camera powered by Android CameraX.
 * Allows users to snap photos directly within StyleMorph, preview framing with
 * rule-of-thirds grid, select target AI style presets on-the-fly, and immediately
 * initiate AI style morphing or add photos to the batch queue.
 */
@Composable
fun CameraScreen(
    viewModel: StyleMorphViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val styles = viewModel.styles
    val globalSelectedStyle by viewModel.selectedStyle.collectAsState()
    var localSelectedStyle by remember { mutableStateOf(globalSelectedStyle) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Camera Configuration States
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_AUTO) }
    var showGridLines by remember { mutableStateOf(true) }
    var isCapturing by remember { mutableStateOf(false) }
    var capturedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var showStyleSelectionDialog by remember { mutableStateOf(false) }

    // Intercept back button: if reviewing a captured shot, go back to viewfinder; else exit camera
    BackHandler {
        if (capturedPhotoUri != null) {
            capturedPhotoUri = null
        } else {
            onNavigateBack()
        }
    }

    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setFlashMode(flashMode)
            .build()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("camera_screen_view")
    ) {
        if (!hasCameraPermission) {
            // Permission Rationale / Request State
            CameraPermissionRationale(
                onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                onNavigateBack = onNavigateBack
            )
        } else if (capturedPhotoUri != null) {
            // Post-Capture Review Mode
            PostCaptureReviewView(
                photoUri = capturedPhotoUri!!,
                selectedStyle = localSelectedStyle,
                styles = styles,
                onSelectStyle = { style ->
                    localSelectedStyle = style
                    viewModel.selectStyle(style)
                },
                onOpenStyleDialog = { showStyleSelectionDialog = true },
                onMorphNow = { uri, style ->
                    viewModel.addCapturedPhotoAndMorph(uri, style)
                    onNavigateBack()
                },
                onAddToQueue = { uri ->
                    viewModel.addCapturedPhoto(uri)
                    onNavigateBack()
                },
                onRetake = {
                    capturedPhotoUri = null
                }
            )
        } else {
            // Live CameraX Viewfinder
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    }

                    try {
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            try {
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }
                                val cameraSelector = CameraSelector.Builder()
                                    .requireLensFacing(lensFacing)
                                    .build()

                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview,
                                    imageCapture
                                )
                            } catch (exc: Exception) {
                                Log.e("CameraScreen", "ProcessCameraProvider setup error", exc)
                            }
                        }, ContextCompat.getMainExecutor(ctx))
                    } catch (exc: Exception) {
                        Log.e("CameraScreen", "Camera provider instance init error", exc)
                    }

                    previewView
                },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("camera_preview_view"),
                update = { previewView ->
                    try {
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(previewView.context)
                        cameraProviderFuture.addListener({
                            try {
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }
                                val cameraSelector = CameraSelector.Builder()
                                    .requireLensFacing(lensFacing)
                                    .build()

                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview,
                                    imageCapture
                                )
                            } catch (exc: Exception) {
                                Log.e("CameraScreen", "Camera binding update error", exc)
                            }
                        }, ContextCompat.getMainExecutor(previewView.context))
                    } catch (exc: Exception) {
                        Log.e("CameraScreen", "Camera update error", exc)
                    }
                }
            )

            // Rule of Thirds Grid Overlay
            if (showGridLines) {
                CameraGridOverlay(modifier = Modifier.fillMaxSize())
            }

            // Top Camera HUD: Back, Title, Grid Toggle, Flash Toggle, Lens Switcher
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0x66000000))
                        .testTag("camera_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Gallery",
                        tint = Color.White
                    )
                }

                // AI Camera Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0x88000000),
                    modifier = Modifier.clip(RoundedCornerShape(20.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = SecondaryCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI Style Camera",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Right action buttons: Grid, Flash, Flip
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Grid Toggle
                    IconButton(
                        onClick = { showGridLines = !showGridLines },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0x66000000))
                            .testTag("camera_grid_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (showGridLines) Icons.Default.GridOn else Icons.Default.GridOff,
                            contentDescription = "Toggle Grid Lines",
                            tint = if (showGridLines) SecondaryCyan else Color.White
                        )
                    }

                    // Flash Toggle
                    IconButton(
                        onClick = {
                            flashMode = when (flashMode) {
                                ImageCapture.FLASH_MODE_AUTO -> ImageCapture.FLASH_MODE_ON
                                ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_OFF
                                else -> ImageCapture.FLASH_MODE_AUTO
                            }
                            imageCapture.flashMode = flashMode
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0x66000000))
                            .testTag("camera_flash_toggle_button")
                    ) {
                        Icon(
                            imageVector = when (flashMode) {
                                ImageCapture.FLASH_MODE_ON -> Icons.Default.FlashOn
                                ImageCapture.FLASH_MODE_OFF -> Icons.Default.FlashOff
                                else -> Icons.Default.FlashAuto
                            },
                            contentDescription = "Toggle Flash Mode",
                            tint = if (flashMode != ImageCapture.FLASH_MODE_OFF) SecondaryCyan else Color.White
                        )
                    }

                    // Flip Camera Lens
                    IconButton(
                        onClick = {
                            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                CameraSelector.LENS_FACING_FRONT
                            } else {
                                CameraSelector.LENS_FACING_BACK
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0x66000000))
                            .testTag("camera_flip_lens_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cameraswitch,
                            contentDescription = "Switch Camera Lens",
                            tint = Color.White
                        )
                    }
                }
            }

            // Bottom Camera HUD: Style Selection Strip + Shutter Row
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0xCC000000), Color(0xF5000000))
                        )
                    )
                    .navigationBarsPadding()
                    .padding(bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Style Selector Strip docked right above shutter
                CameraStylePresetBar(
                    styles = styles,
                    selectedStyle = localSelectedStyle,
                    onSelectStyle = { style ->
                        localSelectedStyle = style
                        viewModel.selectStyle(style)
                    },
                    onOpenStyleDialog = { showStyleSelectionDialog = true }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Shutter Control Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Style Catalog shortcut
                    IconButton(
                        onClick = { showStyleSelectionDialog = true },
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(0x44FFFFFF))
                            .testTag("camera_open_style_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Open Style Preset Catalog",
                            tint = SecondaryCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Center: Large Shutter Button
                    if (isCapturing) {
                        CircularProgressIndicator(
                            color = SecondaryCyan,
                            modifier = Modifier.size(76.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .border(4.dp, Color.White, CircleShape)
                                .padding(6.dp)
                                .clickable {
                                    isCapturing = true
                                    executePhotoCapture(
                                        context = context,
                                        imageCapture = imageCapture,
                                        onSuccess = { uri ->
                                            isCapturing = false
                                            capturedPhotoUri = uri
                                        },
                                        onError = { exc ->
                                            isCapturing = false
                                            Log.e("CameraScreen", "Photo capture failed", exc)
                                        }
                                    )
                                }
                                .testTag("camera_shutter_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(listOf(PrimaryViolet, SecondaryCyan))
                                    )
                            )
                        }
                    }

                    // Right: Quick Preset details chip
                    Surface(
                        onClick = { showStyleSelectionDialog = true },
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0x33FFFFFF),
                        modifier = Modifier.clip(RoundedCornerShape(16.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(AccentEmerald)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = localSelectedStyle.tag,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Full AI Styling Presets Dialog
        if (showStyleSelectionDialog) {
            StyleSelectionDialog(
                styles = styles,
                currentStyle = localSelectedStyle,
                queuedPhotosCount = 1,
                onDismiss = { showStyleSelectionDialog = false },
                onSelectStyle = { style ->
                    localSelectedStyle = style
                    viewModel.selectStyle(style)
                },
                onApplyAndMorph = { style ->
                    localSelectedStyle = style
                    viewModel.selectStyle(style)
                    showStyleSelectionDialog = false
                    // If a photo was already snapped in review mode, execute morph
                    capturedPhotoUri?.let { uri ->
                        viewModel.addCapturedPhotoAndMorph(uri, style)
                        onNavigateBack()
                    }
                }
            )
        }
    }
}

/**
 * Interactive style selector bar right on top of the live camera viewfinder.
 */
@Composable
private fun CameraStylePresetBar(
    styles: List<StylePreset>,
    selectedStyle: StylePreset,
    onSelectStyle: (StylePreset) -> Unit,
    onOpenStyleDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("camera_style_selector_row")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Preset: ${selectedStyle.name}",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Browse all styles ›",
                color = SecondaryCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onOpenStyleDialog() }
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(styles, key = { it.id }) { style ->
                val isSelected = style.id == selectedStyle.id
                Surface(
                    onClick = { onSelectStyle(style) },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) PrimaryViolet else Color(0x55000000),
                    border = if (isSelected) {
                        androidx.compose.foundation.BorderStroke(1.5.dp, SecondaryCyan)
                    } else {
                        androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF))
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .testTag("camera_style_chip_${style.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = style.name,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

/**
 * Post-Capture Review View:
 * Allows user to review their captured shot, adjust or pick an AI style preset,
 * and either morph immediately or add to the batch queue.
 */
@Composable
private fun PostCaptureReviewView(
    photoUri: Uri,
    selectedStyle: StylePreset,
    styles: List<StylePreset>,
    onSelectStyle: (StylePreset) -> Unit,
    onOpenStyleDialog: () -> Unit,
    onMorphNow: (Uri, StylePreset) -> Unit,
    onAddToQueue: (Uri) -> Unit,
    onRetake: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("camera_review_panel")
    ) {
        // Captured Image Full Display
        AsyncImage(
            model = photoUri,
            contentDescription = "Captured Photo Preview",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Top Gradient & Back / Retake Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onRetake,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0x88000000))
                    .testTag("camera_review_close_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Retake Photo",
                    tint = Color.White
                )
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xAA000000),
                modifier = Modifier.clip(RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = AccentEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Captured & Ready",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Bottom Review Card with Style Action Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
            color = Color(0xF212121A),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(20.dp)
            ) {
                // Style Selector Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Target AI Style",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                        Text(
                            text = selectedStyle.name,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = onOpenStyleDialog,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SecondaryCyan),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Change", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Horizontal quick style chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(styles, key = { it.id }) { style ->
                        val isSelected = style.id == selectedStyle.id
                        Surface(
                            onClick = { onSelectStyle(style) },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) PrimaryViolet else Color(0x33FFFFFF),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, SecondaryCyan) else null,
                            modifier = Modifier.clip(RoundedCornerShape(16.dp))
                        ) {
                            Text(
                                text = style.name,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Primary Action: Morph Photo Now
                Button(
                    onClick = { onMorphNow(photoUri, selectedStyle) },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("camera_morph_now_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = AccentAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Morph with ${selectedStyle.name}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Secondary Actions: Add to Batch Queue & Retake
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { onAddToQueue(photoUri) },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("camera_add_to_queue_button")
                    ) {
                        Text("Add to Batch Queue", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }

                    TextButton(
                        onClick = onRetake,
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("camera_retake_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Retake", color = Color.LightGray, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

/**
 * Semi-translucent Rule-of-Thirds Grid Overlay for photographic composition.
 */
@Composable
private fun CameraGridOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val gridColor = Color(0x44FFFFFF)
        val strokeWidth = 1.dp.toPx()

        // Vertical lines at 1/3 and 2/3
        drawLine(
            color = gridColor,
            start = Offset(width / 3f, 0f),
            end = Offset(width / 3f, height),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = gridColor,
            start = Offset(2f * width / 3f, 0f),
            end = Offset(2f * width / 3f, height),
            strokeWidth = strokeWidth
        )

        // Horizontal lines at 1/3 and 2/3
        drawLine(
            color = gridColor,
            start = Offset(0f, height / 3f),
            end = Offset(width, height / 3f),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = gridColor,
            start = Offset(0f, 2f * height / 3f),
            end = Offset(width, 2f * height / 3f),
            strokeWidth = strokeWidth
        )
    }
}

/**
 * Camera Permission Rationale view with inviting styling and clean action buttons.
 */
@Composable
private fun CameraPermissionRationale(
    onRequestPermission: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(PrimaryViolet.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = null,
                    tint = PrimaryViolet,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Camera Permission Required",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "StyleMorph utilizes your camera so you can snap portraits, landscapes, and objects directly within the app and instantly generate AI style variations.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.LightGray)
                ) {
                    Text("Return to Library")
                }

                Button(
                    onClick = onRequestPermission,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryViolet),
                    modifier = Modifier.testTag("grant_camera_permission_button")
                ) {
                    Text("Grant Permission", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Executes photo capture using CameraX ImageCapture and writes output to app cache.
 */
private fun executePhotoCapture(
    context: Context,
    imageCapture: ImageCapture,
    onSuccess: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val photoFile = File(
        context.cacheDir,
        "stylemorph_capture_${System.currentTimeMillis()}.jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                onSuccess(savedUri)
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        }
    )
}
