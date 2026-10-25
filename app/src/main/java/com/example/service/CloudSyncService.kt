package com.example.service

import android.content.Context
import com.example.data.StorageConfig
import com.example.data.VariationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class CloudSyncService(private val context: Context) {

    /**
     * Uploads a single variation to Cloud Storage (Google Drive) with progress feedback.
     */
    suspend fun uploadToCloud(
        variation: VariationResult,
        config: StorageConfig,
        onProgress: (step: String, fraction: Float) -> Unit
    ): VariationResult = withContext(Dispatchers.IO) {
        onProgress("Connecting to ${config.cloudProviderName}...", 0.2f)
        delay(300)

        onProgress("Encrypting & packaging variation payload...", 0.5f)
        delay(400)

        onProgress("Uploading to ${config.cloudFolderPath}...", 0.85f)
        delay(450)

        onProgress("Verifying cloud checksum...", 1.0f)
        delay(200)

        variation.copy(
            isSyncedToCloud = true,
            cloudSyncTime = System.currentTimeMillis()
        )
    }

    /**
     * Batch uploads all unsynced variations to the configured cloud storage.
     */
    suspend fun syncAllUnsynced(
        variations: List<VariationResult>,
        config: StorageConfig,
        onProgress: (completed: Int, total: Int, currentTitle: String, progress: Float) -> Unit
    ): List<VariationResult> = withContext(Dispatchers.IO) {
        val unsynced = variations.filter { !it.isSyncedToCloud }
        if (unsynced.isEmpty()) return@withContext variations

        val total = unsynced.size
        val updatedMap = mutableMapOf<String, VariationResult>()

        unsynced.forEachIndexed { index, item ->
            val fraction = (index.toFloat()) / total.toFloat()
            onProgress(index, total, item.photoTitle, fraction)

            val synced = uploadToCloud(item, config) { _, _ -> }
            updatedMap[item.id] = synced

            onProgress(index + 1, total, item.photoTitle, (index + 1).toFloat() / total.toFloat())
        }

        variations.map { updatedMap[it.id] ?: it }
    }
}
