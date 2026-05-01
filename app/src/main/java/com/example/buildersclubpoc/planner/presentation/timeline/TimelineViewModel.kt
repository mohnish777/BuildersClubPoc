package com.example.buildersclubpoc.planner.presentation.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class TimelineEntryType {
    BREAKFAST,
    LUNCH,
    DINNER,
    GROCERY,
}

enum class FulfillmentOption {
    OFFICE,
    HOME,
    DINE_OUT,
}

data class CartItemUi(
    val id: String,
    val name: String,
    val isSelected: Boolean,
)

data class TimelineActionUi(
    val id: String,
    val time: String,
    val title: String,
    val description: String,
    val type: TimelineEntryType,
    val selectedOption: FulfillmentOption? = null,
)

sealed interface TimelineSheetState {
    data class MealEditor(
        val actionId: String,
        val mealTitle: String,
        val selectedOption: FulfillmentOption,
        val allowedOptions: List<FulfillmentOption>,
    ) : TimelineSheetState

    data object GroceryEditor : TimelineSheetState
}

data class TimelineState(
    val visibleCount: Int = 0,
    val actions: List<TimelineActionUi> = initialTimelineActions,
    val cartItems: List<CartItemUi> = initialCartItems,
    val sheetState: TimelineSheetState? = null,
)

sealed interface TimelineAction {
    data object OnScreenShown : TimelineAction
    data class OnTimelineEntryClicked(val actionId: String) : TimelineAction
    data class OnFulfillmentOptionSelected(val option: FulfillmentOption) : TimelineAction
    data class OnCartItemToggled(val itemId: String) : TimelineAction
    data object OnSheetDismissed : TimelineAction
}

private val initialTimelineActions = listOf(
    TimelineActionUi(
        id = "breakfast",
        time = "9:00 AM",
        title = "Breakfast ordered",
        description = "Breakfast sent to your office address before the first meeting",
        type = TimelineEntryType.BREAKFAST,
        selectedOption = FulfillmentOption.OFFICE,
    ),
    TimelineActionUi(
        id = "lunch",
        time = "1:00 PM",
        title = "Lunch locked in",
        description = "Lunch is currently set for your office address",
        type = TimelineEntryType.LUNCH,
        selectedOption = FulfillmentOption.OFFICE,
    ),
    TimelineActionUi(
        id = "dinner",
        time = "7:30 PM",
        title = "Dinner planned",
        description = "Dinner is currently set for your home address",
        type = TimelineEntryType.DINNER,
        selectedOption = FulfillmentOption.HOME,
    ),
    TimelineActionUi(
        id = "grocery",
        time = "8:00 PM",
        title = "Groceries added to cart",
        description = "4 items are currently ready in your Instamart cart",
        type = TimelineEntryType.GROCERY,
    ),
)

private val initialCartItems = listOf(
    CartItemUi(id = "oats", name = "Oats", isSelected = true),
    CartItemUi(id = "banana", name = "Bananas", isSelected = true),
    CartItemUi(id = "greek_yogurt", name = "Greek yogurt", isSelected = true),
    CartItemUi(id = "spinach", name = "Spinach", isSelected = true),
    CartItemUi(id = "berries", name = "Berries", isSelected = false),
)

class TimelineViewModel : ViewModel() {
    private val _state = MutableStateFlow(TimelineState())
    val state: StateFlow<TimelineState> = _state.asStateFlow()

    private var started = false

    fun onAction(action: TimelineAction) {
        when (action) {
            TimelineAction.OnScreenShown -> revealTimeline()
            is TimelineAction.OnTimelineEntryClicked -> openEditor(action.actionId)
            is TimelineAction.OnFulfillmentOptionSelected -> updateMealOption(action.option)
            is TimelineAction.OnCartItemToggled -> toggleCartItem(action.itemId)
            TimelineAction.OnSheetDismissed -> dismissSheet()
        }
    }

    private fun revealTimeline() {
        if (started) return
        started = true

        viewModelScope.launch {
            initialTimelineActions.indices.forEach { index ->
                delay(350)
                _state.update { current ->
                    current.copy(visibleCount = index + 1)
                }
            }
        }
    }

    private fun openEditor(actionId: String) {
        val action = _state.value.actions.firstOrNull { it.id == actionId } ?: return

        val sheetState = when (action.type) {
            TimelineEntryType.BREAKFAST -> TimelineSheetState.MealEditor(
                actionId = action.id,
                mealTitle = action.title,
                selectedOption = action.selectedOption ?: FulfillmentOption.OFFICE,
                allowedOptions = listOf(FulfillmentOption.OFFICE, FulfillmentOption.HOME),
            )
            TimelineEntryType.LUNCH, TimelineEntryType.DINNER -> TimelineSheetState.MealEditor(
                actionId = action.id,
                mealTitle = action.title,
                selectedOption = action.selectedOption ?: FulfillmentOption.HOME,
                allowedOptions = listOf(
                    FulfillmentOption.OFFICE,
                    FulfillmentOption.HOME,
                    FulfillmentOption.DINE_OUT,
                ),
            )
            TimelineEntryType.GROCERY -> TimelineSheetState.GroceryEditor
        }

        _state.update { current ->
            current.copy(sheetState = sheetState)
        }
    }

    private fun updateMealOption(option: FulfillmentOption) {
        val sheet = _state.value.sheetState as? TimelineSheetState.MealEditor ?: return

        _state.update { current ->
            current.copy(
                actions = current.actions.map { action ->
                    if (action.id != sheet.actionId) {
                        action
                    } else {
                        action.copy(
                            selectedOption = option,
                            description = descriptionFor(action.type, option),
                        )
                    }
                },
                sheetState = null,
            )
        }
    }

    private fun toggleCartItem(itemId: String) {
        _state.update { current ->
            val updatedItems = current.cartItems.map { item ->
                if (item.id == itemId) item.copy(isSelected = !item.isSelected) else item
            }

            current.copy(
                cartItems = updatedItems,
                actions = current.actions.map { action ->
                    if (action.type == TimelineEntryType.GROCERY) {
                        action.copy(
                            description = groceryDescription(updatedItems.count { it.isSelected }),
                        )
                    } else {
                        action
                    }
                },
            )
        }
    }

    private fun dismissSheet() {
        _state.update { current ->
            current.copy(sheetState = null)
        }
    }

    private fun descriptionFor(
        type: TimelineEntryType,
        option: FulfillmentOption,
    ): String {
        return when (type) {
            TimelineEntryType.BREAKFAST -> when (option) {
                FulfillmentOption.OFFICE -> "Breakfast sent to your office address before the first meeting"
                FulfillmentOption.HOME -> "Breakfast redirected to your home address"
                FulfillmentOption.DINE_OUT -> "Breakfast dine out is not supported"
            }
            TimelineEntryType.LUNCH -> when (option) {
                FulfillmentOption.OFFICE -> "Lunch is currently set for your office address"
                FulfillmentOption.HOME -> "Lunch is now redirected to your home address"
                FulfillmentOption.DINE_OUT -> "Lunch is now set as a dine out plan"
            }
            TimelineEntryType.DINNER -> when (option) {
                FulfillmentOption.OFFICE -> "Dinner is now set for your office address"
                FulfillmentOption.HOME -> "Dinner is currently set for your home address"
                FulfillmentOption.DINE_OUT -> "Dinner is now set as a dine out plan"
            }
            TimelineEntryType.GROCERY -> groceryDescription(_state.value.cartItems.count { it.isSelected })
        }
    }

    private fun groceryDescription(selectedCount: Int): String {
        return "$selectedCount items are currently ready in your Instamart cart"
    }
}
