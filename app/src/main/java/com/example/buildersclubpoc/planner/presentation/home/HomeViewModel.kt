package com.example.buildersclubpoc.planner.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buildersclubpoc.planner.presentation.PlannerMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeState(
    val isAnalyzingCalendar: Boolean = true,
    val calendarStep: String = "Connecting to your calendar",
    val summaryMessage: String? = null,
    val selectedMode: PlannerMode? = null,
)

sealed interface HomeAction {
    data object OnScreenShown : HomeAction
    data class OnModeSelected(val mode: PlannerMode) : HomeAction
}

class HomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private var hasStartedAnalysis = false

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.OnScreenShown -> runCalendarAnalysis()
            is HomeAction.OnModeSelected -> {
                _state.update { current ->
                    current.copy(selectedMode = action.mode)
                }
            }
        }
    }

    private fun runCalendarAnalysis() {
        if (hasStartedAnalysis) return
        hasStartedAnalysis = true

        val steps = listOf(
            "Connecting to Calendar MCP (mocked)",
            "Reading this week's meetings",
            "Reviewing meeting density across your week",
            "Finding packed days and food gaps",
        )

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isAnalyzingCalendar = true,
                    calendarStep = steps.first(),
                    summaryMessage = null,
                )
            }

            steps.forEach { step ->
                _state.update { current ->
                    current.copy(calendarStep = step)
                }
                delay(4_000)
            }

            _state.update { current ->
                current.copy(
                    isAnalyzingCalendar = false,
                    summaryMessage = "Your meetings are packed for the whole week. Shall I plan for the entire week?",
                    selectedMode = PlannerMode.WEEK,
                )
            }
        }
    }
}
