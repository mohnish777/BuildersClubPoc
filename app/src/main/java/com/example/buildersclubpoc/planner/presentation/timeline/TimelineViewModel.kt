package com.example.buildersclubpoc.planner.presentation.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TimelineActionUi(
    val id: String,
    val time: String,
    val description: String,
)

private val timelinePreview = listOf(
    TimelineActionUi(
        id = "breakfast",
        time = "9:00 AM",
        description = "Breakfast ordered before your first meeting",
    ),
    TimelineActionUi(
        id = "lunch",
        time = "1:00 PM",
        description = "Lunch locked near your midday meeting window",
    ),
    TimelineActionUi(
        id = "dinner",
        time = "7:30 PM",
        description = "Dinner reserved based on your preferred cuisine",
    ),
    TimelineActionUi(
        id = "grocery",
        time = "8:00 PM",
        description = "Groceries added to Instamart for tomorrow",
    ),
)

data class TimelineState(
    val visibleCount: Int = 0,
    val actions: List<TimelineActionUi> = timelinePreview,
)

sealed interface TimelineAction {
    data object OnScreenShown : TimelineAction
}

class TimelineViewModel : ViewModel() {
    private val _state = MutableStateFlow(TimelineState())
    val state: StateFlow<TimelineState> = _state.asStateFlow()

    private var started = false

    fun onAction(action: TimelineAction) {
        when (action) {
            TimelineAction.OnScreenShown -> revealTimeline()
        }
    }

    private fun revealTimeline() {
        if (started) return
        started = true

        viewModelScope.launch {
            timelinePreview.indices.forEach { index ->
                delay(350)
                _state.update { current ->
                    current.copy(visibleCount = index + 1)
                }
            }
        }
    }
}
