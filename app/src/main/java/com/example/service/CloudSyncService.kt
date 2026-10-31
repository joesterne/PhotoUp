package com.example.service

import android.content.Context
import com.example.data.StorageConfig
import com.example.data.VariationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Cloud sync is NOT implemented in this build: no Drive/Photos backend is
 * integrated. This class previously ran fake progress delays and returned
 * variations flagged isSyncedToCloud = true, so the UI claimed photos were
 * "Synced to Cloud Storage" / "Backed up to Google Drive" when nothing had
 * ever left the device. It now honestly returns variations unchanged
 * (still unsynced) so callers can tell the user the truth.
 */
class CloudSyncService(private val context: Context) {

    suspend fun uploadToCloud(
        variation: VariationResult,
        config: StorageConfig,
        onProgress: (step: String, fraction: Float) -> Unit
    ): VariationResult = withContext(Dispatchers.IO) {
        onProgress("Cloud sync is not available in this build", 0f)
        variation
    }

    suspend fun syncAllUnsynced(
        variations: List<VariationResult>,
        config: StorageConfig,
        onProgress: (completed: Int, total: Int, currentTitle: String, progress: Float) -> Unit
    ): List<VariationResult> = withContext(Dispatchers.IO) {
        // Nothing can be uploaded - return the list untouched so callers can
        // report that everything is still local-only.
        variations
    }
}
