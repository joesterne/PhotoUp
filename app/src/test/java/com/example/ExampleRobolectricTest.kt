package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.SamplePhotoLibrary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("StyleMorph", appName)
  }

  @Test
  fun `auto select best photos selects top scoring candidates`() {
    val initial = SamplePhotoLibrary.initialPhotos
    val curated = SamplePhotoLibrary.autoSelectBestPhotos(initial, topCount = 3)
    val selected = curated.filter { it.isAutoSelected }
    assertEquals(3, selected.size)
    assertTrue(selected.all { it.aestheticScore >= 0.90f })
  }

  @Test
  fun `queue item progress initializes correctly`() {
    val queueItem = com.example.data.QueueItemProgress(
      photoId = "photo_1",
      photoTitle = "Sunset",
      stage = com.example.data.QueueItemStage.TRANSFORMING,
      transformProgress = 0.5f,
      uploadProgress = 0.2f
    )
    assertEquals(com.example.data.QueueItemStage.TRANSFORMING, queueItem.stage)
    assertEquals(0.5f, queueItem.transformProgress, 0.01f)
    assertEquals(0.2f, queueItem.uploadProgress, 0.01f)
  }

  @Test
  fun `style presets contain expected popular styling varieties`() {
    val styles = SamplePhotoLibrary.popularStyles
    assertTrue("Should contain at least 8 popular style presets", styles.size >= 8)

    val styleIds = styles.map { it.id }
    assertTrue(styleIds.contains("cyberpunk"))
    assertTrue(styleIds.contains("oil_painting"))
    assertTrue(styleIds.contains("vintage_film"))
    assertTrue(styleIds.contains("anime_ghibli"))
    assertTrue(styleIds.contains("renaissance"))
    assertTrue(styleIds.contains("pixar_3d"))
    assertTrue(styleIds.contains("vintage_polaroid"))
    assertTrue(styleIds.contains("synthwave"))
    assertTrue(styleIds.contains("pop_art"))
    assertTrue(styleIds.contains("watercolor"))

    val styleNames = styles.map { it.name }
    assertTrue(styleNames.contains("Oil Painting"))
    assertTrue(styleNames.contains("Cyberpunk"))
    assertTrue(styleNames.contains("Vintage Film"))

    styles.forEach { preset ->
      assertTrue(preset.name.isNotBlank())
      assertTrue(preset.promptModifiers.isNotBlank())
      assertTrue(preset.description.isNotBlank())
    }
  }

  @Test
  fun `filter styles by search query matches correctly`() {
    val styles = SamplePhotoLibrary.popularStyles
    val ghibliMatches = styles.filter {
      it.name.contains("Anime", ignoreCase = true) || it.description.contains("Ghibli", ignoreCase = true)
    }
    assertEquals(1, ghibliMatches.size)
    assertEquals("anime_ghibli", ghibliMatches.first().id)

    val retroMatches = styles.filter {
      it.name.contains("Retro", ignoreCase = true) || it.tag.contains("Retro", ignoreCase = true)
    }
    assertTrue(retroMatches.any { it.id == "synthwave" })
  }

  @Test
  fun `addCapturedPhoto stores photo with camera category and selects it`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = com.example.viewmodel.StyleMorphViewModel(app)
    val testUri = android.net.Uri.parse("file:///data/user/0/com.example/cache/test_photo.jpg")
    val initialPhotoCount = viewModel.photos.value.size

    val photoId = viewModel.addCapturedPhoto(testUri)

    assertTrue(photoId.startsWith("camera_capture_"))
    assertEquals(initialPhotoCount + 1, viewModel.photos.value.size)
    assertTrue(viewModel.selectedPhotoIds.value.contains(photoId))

    val capturedPhoto = viewModel.photos.value.first()
    assertEquals(photoId, capturedPhoto.id)
    assertEquals("Camera", capturedPhoto.category)
    assertEquals(testUri.toString(), capturedPhoto.uriString)
    assertTrue(capturedPhoto.aestheticScore >= 0.85f)
  }

  @Test
  fun `selectTab changes tab index correctly`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = com.example.viewmodel.StyleMorphViewModel(app)
    assertEquals(0, viewModel.selectedTabIndex.value)

    viewModel.selectTab(2)
    assertEquals(2, viewModel.selectedTabIndex.value)

    viewModel.selectTab(3)
    assertEquals(3, viewModel.selectedTabIndex.value)
  }

  @Test
  fun `addCapturedPhotoAndMorph switches tab and selects style`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = com.example.viewmodel.StyleMorphViewModel(app)
    val cyberpunkStyle = SamplePhotoLibrary.popularStyles.first { it.id == "cyberpunk" }
    val testUri = android.net.Uri.parse("file:///data/user/0/com.example/cache/snap.jpg")

    viewModel.addCapturedPhotoAndMorph(testUri, cyberpunkStyle)

    assertEquals(cyberpunkStyle.id, viewModel.selectedStyle.value.id)
    assertEquals(2, viewModel.selectedTabIndex.value) // Switches to Variations tab
  }

  @Test
  fun `togglePhotoSelection and clearSelection update batch candidate ids`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = com.example.viewmodel.StyleMorphViewModel(app)

    viewModel.clearSelection()
    assertEquals(0, viewModel.selectedPhotoIds.value.size)

    val testPhotoId = viewModel.photos.value.first().id
    viewModel.togglePhotoSelection(testPhotoId)
    assertTrue(viewModel.selectedPhotoIds.value.contains(testPhotoId))
    assertEquals(1, viewModel.selectedPhotoIds.value.size)

    // Toggle off
    viewModel.togglePhotoSelection(testPhotoId)
    assertEquals(0, viewModel.selectedPhotoIds.value.size)

    // Select all
    viewModel.selectAllPhotos()
    assertEquals(viewModel.photos.value.size, viewModel.selectedPhotoIds.value.size)
  }

  @Test
  fun `setPhotoSelection and applyStyleFilterToSelected update selection and active style`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = com.example.viewmodel.StyleMorphViewModel(app)

    viewModel.clearSelection()
    val firstTwoIds = viewModel.photos.value.take(2).map { it.id }
    firstTwoIds.forEach { id -> viewModel.setPhotoSelection(id, true) }
    assertEquals(2, viewModel.selectedPhotoIds.value.size)

    val oilPainting = SamplePhotoLibrary.popularStyles.first { it.id == "oil_painting" }
    viewModel.applyStyleFilterToSelected(oilPainting)
    assertEquals("oil_painting", viewModel.selectedStyle.value.id)
    viewModel.cancelBatchProcessing()
  }

  @Test
  fun `scanDeviceLocalStorage discovers image files in app internal directories`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val testPhotoDir = java.io.File(app.filesDir, "photos")
    testPhotoDir.mkdirs()
    val testImg = java.io.File(testPhotoDir, "landscape_autumn.jpg")
    testImg.writeText("fake-image-bytes")

    val viewModel = com.example.viewmodel.StyleMorphViewModel(app)
    val discovered = viewModel.scanDeviceLocalStorage()

    assertTrue("Discovered at least 1 local image", discovered >= 1)
    val localItem = viewModel.photos.value.firstOrNull { it.id.contains("landscape_autumn") }
    org.junit.Assert.assertNotNull("Local storage image should be added to photos", localItem)
    assertEquals("Local Storage", localItem?.category)
  }

  @Test
  fun `exportToDeviceGallery and shareVariationImage export transformed image to local storage`() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    val engine = com.example.service.ImageProcessingEngine(app)
    val sampleVariation = SamplePhotoLibrary.initialVariations.first()

    val exported = kotlinx.coroutines.runBlocking {
      engine.exportToDeviceGallery(sampleVariation)
    }
    assertTrue("Export to device gallery should succeed", exported)

    val galleryDir = java.io.File(app.filesDir, "Pictures/StyleMorph")
    assertTrue("Gallery directory should exist", galleryDir.exists())
    val exportedFiles = galleryDir.listFiles()?.filter { it.name.endsWith(".png") }.orEmpty()
    assertTrue("At least one PNG file should be written to Pictures/StyleMorph", exportedFiles.isNotEmpty())

    val shared = kotlinx.coroutines.runBlocking {
      engine.shareVariationImage(sampleVariation)
    }
    assertTrue("Share variation image should prepare file and launch chooser intent", shared)
  }
}


