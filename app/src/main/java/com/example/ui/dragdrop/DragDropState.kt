package com.example.ui.dragdrop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.example.data.PhotoItem

/**
 * Coordinates drag-and-drop hit testing, floating ghost coordinates,
 * and drop target interactions.
 */
class DragDropState {
    var isDragging by mutableStateOf(false)
        private set

    var draggedPhoto by mutableStateOf<PhotoItem?>(null)
        private set

    var dragPosition by mutableStateOf(Offset.Zero)
        private set

    private val dropTargets = mutableStateMapOf<String, Rect>()

    var activeHoverTargetId by mutableStateOf<String?>(null)
        private set

    fun registerDropTarget(targetId: String, boundsInWindow: Rect) {
        dropTargets[targetId] = boundsInWindow
    }

    fun unregisterDropTarget(targetId: String) {
        dropTargets.remove(targetId)
    }

    fun startDragging(photo: PhotoItem, startPosition: Offset) {
        draggedPhoto = photo
        dragPosition = startPosition
        isDragging = true
        updateHoverTarget(startPosition)
    }

    fun onDrag(dragAmount: Offset) {
        if (!isDragging) return
        dragPosition += dragAmount
        updateHoverTarget(dragPosition)
    }

    fun stopDragging(): String? {
        val dropTargetId = activeHoverTargetId
        isDragging = false
        draggedPhoto = null
        activeHoverTargetId = null
        dragPosition = Offset.Zero
        return dropTargetId
    }

    private fun updateHoverTarget(position: Offset) {
        activeHoverTargetId = dropTargets.entries.firstOrNull { (_, rect) ->
            rect.contains(position)
        }?.key
    }
}

@Composable
fun rememberDragDropState(): DragDropState {
    return remember { DragDropState() }
}
