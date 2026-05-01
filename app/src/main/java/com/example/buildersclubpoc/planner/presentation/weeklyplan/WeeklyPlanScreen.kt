package com.example.buildersclubpoc.planner.presentation.weeklyplan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocalDining
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buildersclubpoc.R
import com.example.buildersclubpoc.planner.presentation.PlannerMode
import com.example.buildersclubpoc.ui.theme.BuildersClubPocTheme

@Composable
fun WeeklyPlanRoot(
    plannerMode: PlannerMode,
    onBackClick: () -> Unit,
    viewModel: WeeklyPlanViewModel = viewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(plannerMode) {
        viewModel.onAction(WeeklyPlanAction.OnScreenShown(plannerMode))
    }

    WeeklyPlanScreen(
        state = state,
        onAction = viewModel::onAction,
        onBackClick = onBackClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyPlanScreen(
    state: WeeklyPlanState,
    onAction: (WeeklyPlanAction) -> Unit,
    onBackClick: () -> Unit,
) {
    val heroGradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.tertiaryContainer,
        ),
    )

    if (state.mealEditorSheet != null) {
        MealEditorBottomSheet(
            sheetState = state.mealEditorSheet,
            onDismiss = { onAction(WeeklyPlanAction.OnMealEditorDismissed) },
            onOptionSelected = { option ->
                onAction(WeeklyPlanAction.OnMealOptionSelected(option))
            },
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(R.string.weekly_plan_title))
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.cd_go_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                ) {
                    Column(
                        modifier = Modifier
                            .background(heroGradient)
                            .padding(20.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                            Text(
                                text = when (state.plannerMode) {
                                    PlannerMode.DAY -> stringResource(R.string.day_plan_hero_title)
                                    PlannerMode.WEEK -> stringResource(R.string.weekly_plan_hero_title)
                                },
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = when (state.plannerMode) {
                                PlannerMode.DAY -> stringResource(R.string.day_plan_hero_copy)
                                PlannerMode.WEEK -> stringResource(R.string.weekly_plan_hero_copy)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f),
                        )
                    }
                }
            }

            item {
                SwiggyToolStatusCard(
                    currentToolStatus = state.currentToolStatus,
                    currentDecisionStatus = state.currentDecisionStatus,
                    isGenerating = state.isGenerating,
                )
            }

            items(state.revealedDays, key = { it.day }) { plan ->
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = plan.day,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = stringResource(R.string.edit_day_hint),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }

                        EditableMealRow(
                            label = stringResource(R.string.breakfast_label),
                            slot = plan.breakfast,
                            onClick = {
                                onAction(WeeklyPlanAction.OnMealClicked(plan.day, MealType.BREAKFAST))
                            },
                        )
                        EditableMealRow(
                            label = stringResource(R.string.lunch_label),
                            slot = plan.lunch,
                            onClick = {
                                onAction(WeeklyPlanAction.OnMealClicked(plan.day, MealType.LUNCH))
                            },
                        )
                        EditableMealRow(
                            label = stringResource(R.string.dinner_label),
                            slot = plan.dinner,
                            onClick = {
                                onAction(WeeklyPlanAction.OnMealClicked(plan.day, MealType.DINNER))
                            },
                        )
                    }
                }
            }

            if (state.isGenerating) {
                item {
                    LoadingDayCard(
                        plannerMode = state.plannerMode,
                        revealedCount = state.revealedDays.size,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MealEditorBottomSheet(
    sheetState: MealEditorSheetState,
    onDismiss: () -> Unit,
    onOptionSelected: (String) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = stringResource(R.string.meal_editor_title, mealTypeLabel(sheetState.mealType), sheetState.day),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(R.string.meal_editor_copy),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            sheetState.rankedOptions.forEachIndexed { index, option ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOptionSelected(option) },
                    shape = RoundedCornerShape(20.dp),
                    color = if (option == sheetState.selectedOption) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerHigh
                    },
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = if (index == 0) {
                                    stringResource(R.string.best_pick_label)
                                } else {
                                    stringResource(R.string.backup_pick_label, index + 1)
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }

                        if (option == sheetState.selectedOption) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SwiggyToolStatusCard(
    currentToolStatus: String,
    currentDecisionStatus: String,
    isGenerating: Boolean,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f),
        ),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = stringResource(R.string.weekly_plan_agent_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = stringResource(R.string.weekly_plan_agent_copy),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ToolChip(
                    icon = Icons.Outlined.LocalDining,
                    label = stringResource(R.string.tool_food),
                    modifier = Modifier.weight(1f),
                )
                ToolChip(
                    icon = Icons.Outlined.ShoppingBasket,
                    label = stringResource(R.string.tool_instamart),
                    modifier = Modifier.weight(1f),
                )
                ToolChip(
                    icon = Icons.Outlined.Storefront,
                    label = stringResource(R.string.tool_dineout),
                    modifier = Modifier.weight(1f),
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape,
                            ),
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = currentToolStatus,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = currentDecisionStatus,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun ToolChip(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = true,
        onClick = {},
        enabled = false,
        modifier = modifier,
        label = {
            Text(text = label)
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
            )
        },
    )
}

@Composable
private fun LoadingDayCard(
    plannerMode: PlannerMode,
    revealedCount: Int,
) {
    val nextDay = when (plannerMode) {
        PlannerMode.DAY -> stringResource(R.string.today_label)
        PlannerMode.WEEK -> {
            val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
            days.getOrNull(revealedCount) ?: stringResource(R.string.next_day_fallback)
        }
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                )
                Text(
                    text = stringResource(R.string.loading_day_title, nextDay),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Text(
                text = stringResource(R.string.loading_day_copy),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun EditableMealRow(
    label: String,
    slot: MealSlotUi,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = slot.selectedOption,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.edit_cta),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

private fun mealTypeLabel(mealType: MealType): Int {
    return when (mealType) {
        MealType.BREAKFAST -> R.string.breakfast_label
        MealType.LUNCH -> R.string.lunch_label
        MealType.DINNER -> R.string.dinner_label
    }
}

@Preview(showBackground = true)
@Composable
private fun WeeklyPlanScreenPreview() {
    BuildersClubPocTheme {
        WeeklyPlanScreen(
            state = WeeklyPlanState(
                plannerMode = PlannerMode.WEEK,
                isGenerating = true,
                currentToolStatus = "Checking Swiggy Food recommendations",
                currentDecisionStatus = "Decision engine is locking Wednesday",
                revealedDays = listOf(
                    PlannedDayUi(
                        day = "Monday",
                        breakfast = MealSlotUi(
                            type = MealType.BREAKFAST,
                            selectedOption = "Greek yogurt bowl with berries and chia seeds",
                            rankedOptions = listOf(
                                "Greek yogurt bowl with berries and chia seeds",
                                "Vegetable poha with sprouts",
                                "Oats porridge with banana and nuts",
                            ),
                        ),
                        lunch = MealSlotUi(
                            type = MealType.LUNCH,
                            selectedOption = "Grilled paneer bowl with brown rice and sauteed vegetables",
                            rankedOptions = listOf(
                                "Grilled paneer bowl with brown rice and sauteed vegetables",
                                "Dal, roti, sabzi, and salad combo",
                            ),
                        ),
                        dinner = MealSlotUi(
                            type = MealType.DINNER,
                            selectedOption = "Soup and grilled sandwich combo",
                            rankedOptions = listOf(
                                "Soup and grilled sandwich combo",
                                "Millet roti with mixed veg curry",
                            ),
                        ),
                    ),
                ),
            ),
            onAction = {},
            onBackClick = {},
        )
    }
}
