package com.example.buildersclubpoc.planner.presentation.weeklyplan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buildersclubpoc.agent.preferences.MockUserPreferenceProfiles
import com.example.buildersclubpoc.planner.presentation.PlannerMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class MealType {
    BREAKFAST,
    LUNCH,
    DINNER,
}

data class MealSlotUi(
    val type: MealType,
    val selectedOption: String,
    val rankedOptions: List<String>,
)

data class PlannedDayUi(
    val day: String,
    val breakfast: MealSlotUi,
    val lunch: MealSlotUi,
    val dinner: MealSlotUi,
)

data class MealEditorSheetState(
    val day: String,
    val mealType: MealType,
    val selectedOption: String,
    val rankedOptions: List<String>,
)

data class WeeklyPlanState(
    val plannerMode: PlannerMode = PlannerMode.WEEK,
    val isGenerating: Boolean = true,
    val currentToolStatus: String = "Checking Swiggy Food recommendations",
    val currentDecisionStatus: String = "The agent is preparing your week",
    val revealedDays: List<PlannedDayUi> = emptyList(),
    val mealEditorSheet: MealEditorSheetState? = null,
)

sealed interface WeeklyPlanAction {
    data class OnScreenShown(val plannerMode: PlannerMode) : WeeklyPlanAction
    data class OnMealClicked(val day: String, val mealType: MealType) : WeeklyPlanAction
    data class OnMealOptionSelected(val option: String) : WeeklyPlanAction
    data object OnMealEditorDismissed : WeeklyPlanAction
}

class WeeklyPlanViewModel : ViewModel() {
    private val _state = MutableStateFlow(WeeklyPlanState())
    val state: StateFlow<WeeklyPlanState> = _state.asStateFlow()

    private var startedMode: PlannerMode? = null

    fun onAction(action: WeeklyPlanAction) {
        when (action) {
            is WeeklyPlanAction.OnScreenShown -> generatePlan(action.plannerMode)
            is WeeklyPlanAction.OnMealClicked -> openMealEditor(action.day, action.mealType)
            is WeeklyPlanAction.OnMealOptionSelected -> updateSelectedMeal(action.option)
            WeeklyPlanAction.OnMealEditorDismissed -> dismissMealEditor()
        }
    }

    private fun generatePlan(plannerMode: PlannerMode) {
        if (startedMode == plannerMode) return
        startedMode = plannerMode

        val plan = when (plannerMode) {
            PlannerMode.DAY -> buildDayPlan()
            PlannerMode.WEEK -> buildWeekPlan()
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
                    mealEditorSheet = null,
                )
            }

            toolStatuses.forEach { status ->
                _state.update { current ->
                    current.copy(currentToolStatus = status)
                }
                delay(900)
            }

            plan.forEach { dayPlan ->
                _state.update { current ->
                    current.copy(
                        currentDecisionStatus = "Decision engine is locking ${dayPlan.day}",
                    )
                }
                delay(2_000)
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

    private fun openMealEditor(day: String, mealType: MealType) {
        val plannedDay = _state.value.revealedDays.firstOrNull { it.day == day } ?: return
        val mealSlot = plannedDay.slotFor(mealType)

        _state.update { current ->
            current.copy(
                mealEditorSheet = MealEditorSheetState(
                    day = day,
                    mealType = mealType,
                    selectedOption = mealSlot.selectedOption,
                    rankedOptions = mealSlot.rankedOptions,
                ),
            )
        }
    }

    private fun updateSelectedMeal(option: String) {
        val currentSheet = _state.value.mealEditorSheet ?: return

        _state.update { current ->
            current.copy(
                revealedDays = current.revealedDays.map { day ->
                    if (day.day != currentSheet.day) {
                        day
                    } else {
                        day.withUpdatedSlot(
                            mealType = currentSheet.mealType,
                            selectedOption = option,
                        )
                    }
                },
                mealEditorSheet = null,
            )
        }
    }

    private fun dismissMealEditor() {
        _state.update { current ->
            current.copy(mealEditorSheet = null)
        }
    }

    private fun buildWeekPlan(): List<PlannedDayUi> {
        val profile = MockUserPreferenceProfiles.healthyBalancedProfile.mealPreferences
        val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

        return days.mapIndexed { index, day ->
            PlannedDayUi(
                day = day,
                breakfast = MealSlotUi(
                    type = MealType.BREAKFAST,
                    selectedOption = rotate(profile.breakfastOptions, index).first(),
                    rankedOptions = rotate(profile.breakfastOptions, index),
                ),
                lunch = MealSlotUi(
                    type = MealType.LUNCH,
                    selectedOption = rotate(profile.lunchOptions, index + 1).first(),
                    rankedOptions = rotate(profile.lunchOptions, index + 1),
                ),
                dinner = MealSlotUi(
                    type = MealType.DINNER,
                    selectedOption = rotate(profile.dinnerOptions, index + 2).first(),
                    rankedOptions = rotate(profile.dinnerOptions, index + 2),
                ),
            )
        }
    }

    private fun buildDayPlan(): List<PlannedDayUi> {
        val profile = MockUserPreferenceProfiles.healthyBalancedProfile.mealPreferences

        return listOf(
            PlannedDayUi(
                day = "Today",
                breakfast = MealSlotUi(
                    type = MealType.BREAKFAST,
                    selectedOption = profile.breakfastOptions.first(),
                    rankedOptions = profile.breakfastOptions,
                ),
                lunch = MealSlotUi(
                    type = MealType.LUNCH,
                    selectedOption = profile.lunchOptions.first(),
                    rankedOptions = profile.lunchOptions,
                ),
                dinner = MealSlotUi(
                    type = MealType.DINNER,
                    selectedOption = profile.dinnerOptions.first(),
                    rankedOptions = profile.dinnerOptions,
                ),
            ),
        )
    }

    private fun rotate(options: List<String>, steps: Int): List<String> {
        if (options.isEmpty()) return options
        val normalizedSteps = steps % options.size
        return options.drop(normalizedSteps) + options.take(normalizedSteps)
    }

    private fun PlannedDayUi.slotFor(mealType: MealType): MealSlotUi {
        return when (mealType) {
            MealType.BREAKFAST -> breakfast
            MealType.LUNCH -> lunch
            MealType.DINNER -> dinner
        }
    }

    private fun PlannedDayUi.withUpdatedSlot(
        mealType: MealType,
        selectedOption: String,
    ): PlannedDayUi {
        return when (mealType) {
            MealType.BREAKFAST -> copy(breakfast = breakfast.copy(selectedOption = selectedOption))
            MealType.LUNCH -> copy(lunch = lunch.copy(selectedOption = selectedOption))
            MealType.DINNER -> copy(dinner = dinner.copy(selectedOption = selectedOption))
        }
    }
}
