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
    assertTrue(styleIds.contains("anime_ghibli"))
    assertTrue(styleIds.contains("renaissance"))
    assertTrue(styleIds.contains("pixar_3d"))
    assertTrue(styleIds.contains("vintage_polaroid"))
    assertTrue(styleIds.contains("synthwave"))
    assertTrue(styleIds.contains("pop_art"))
    assertTrue(styleIds.contains("watercolor"))

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
}
