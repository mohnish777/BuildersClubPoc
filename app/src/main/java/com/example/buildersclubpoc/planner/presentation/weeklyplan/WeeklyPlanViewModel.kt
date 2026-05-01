package com.example.buildersclubpoc.planner.presentation.weeklyplan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buildersclubpoc.planner.presentation.PlannerMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlannedDayUi(
    val day: String,
    val breakfast: String,
    val lunch: String,
    val dinner: String,
)

data class WeeklyPlanState(
    val plannerMode: PlannerMode = PlannerMode.WEEK,
    val isGenerating: Boolean = true,
    val currentToolStatus: String = "Checking Swiggy Food recommendations",
    val currentDecisionStatus: String = "The agent is preparing your week",
    val revealedDays: List<PlannedDayUi> = emptyList(),
)

sealed interface WeeklyPlanAction {
    data class OnScreenShown(val plannerMode: PlannerMode) : WeeklyPlanAction
}

class WeeklyPlanViewModel : ViewModel() {
    private val _state = MutableStateFlow(WeeklyPlanState())
    val state: StateFlow<WeeklyPlanState> = _state.asStateFlow()

    private var startedMode: PlannerMode? = null

    fun onAction(action: WeeklyPlanAction) {
        when (action) {
            is WeeklyPlanAction.OnScreenShown -> generatePlan(action.plannerMode)
        }
    }

    private fun generatePlan(plannerMode: PlannerMode) {
        if (startedMode == plannerMode) return
        startedMode = plannerMode

        val plan = when (plannerMode) {
            PlannerMode.DAY -> dayPlan
            PlannerMode.WEEK -> weekPlan
        }

        val toolStatuses = listOf(
            "Checking Swiggy Food recommendations",
            "Pulling Instamart staples for the week",
            "Scanning Dineout options near your likely routes",
        )

        viewModelScope.launch {
            _state.update {
                it.copy(
                    plannerMode = plannerMode,
                    isGenerating = true,
                    currentToolStatus = toolStatuses.first(),
                    currentDecisionStatus = "The agent is preparing your plan",
                    revealedDays = emptyList(),
                )
            }

            toolStatuses.forEach { status ->
                _state.update { current ->
                    current.copy(currentToolStatus = status)
                }
                delay(1000)
            }

            plan.forEachIndexed { idx,  dayPlan ->
                _state.update { current ->
                    current.copy(
                        currentDecisionStatus = "Decision engine is locking ${dayPlan.day}",
                    )
                }
                if (idx == 0) delay(7_000) else delay(4_000)

                _state.update { current ->
                    current.copy(
                        revealedDays = current.revealedDays + dayPlan,
                    )
                }
            }

            _state.update { current ->
                current.copy(
                    isGenerating = false,
                    currentToolStatus = "Swiggy options ready",
                    currentDecisionStatus = "Your plan is ready to review",
                )
            }
        }
    }

    private companion object {
        val weekPlan = listOf(
            PlannedDayUi("Monday", "Greek yogurt bowl", "Paneer grain bowl", "Soup and veg toast"),
            PlannedDayUi("Tuesday", "Veg poha", "Dal, roti, salad", "Millet roti and curry"),
            PlannedDayUi("Wednesday", "Oats and fruit", "Quinoa khichdi", "Roasted veggie bowl"),
            PlannedDayUi("Thursday", "Smoothie bowl", "Healthy thali", "Light paneer salad"),
            PlannedDayUi("Friday", "Toast and eggs", "Brown rice lunch box", "Dinner booking suggestion"),
            PlannedDayUi("Saturday", "Fruit yogurt cup", "Mediterranean bowl", "Comfort dinner plan"),
            PlannedDayUi("Sunday", "Brunch recommendation", "Flexible lunch", "Groceries prep dinner"),
        )

        val dayPlan = listOf(
            PlannedDayUi("Today", "Greek yogurt bowl", "Paneer grain bowl", "Soup and veg toast"),
        )
    }
}
