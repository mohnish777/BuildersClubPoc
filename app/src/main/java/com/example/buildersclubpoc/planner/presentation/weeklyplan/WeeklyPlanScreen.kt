package com.example.buildersclubpoc.planner.presentation.weeklyplan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.buildersclubpoc.R
import com.example.buildersclubpoc.planner.presentation.PlannerMode
import com.example.buildersclubpoc.ui.theme.BuildersClubPocTheme

private data class DayPreview(
    val day: String,
    val breakfast: String,
    val lunch: String,
    val dinner: String,
)

private val weeklyPreview = listOf(
    DayPreview("Monday", "Greek yogurt bowl", "Paneer grain bowl", "Soup and veg toast"),
    DayPreview("Tuesday", "Veg poha", "Dal, roti, salad", "Millet roti and curry"),
    DayPreview("Wednesday", "Oats and fruit", "Quinoa khichdi", "Roasted veggie bowl"),
    DayPreview("Thursday", "Smoothie bowl", "Healthy thali", "Light paneer salad"),
    DayPreview("Friday", "Toast and eggs", "Brown rice lunch box", "Dinner booking suggestion"),
    DayPreview("Saturday", "Fruit yogurt cup", "Mediterranean bowl", "Comfort dinner plan"),
    DayPreview("Sunday", "Brunch recommendation", "Flexible lunch", "Groceries prep dinner"),
)

@Composable
fun WeeklyPlanRoot(
    plannerMode: PlannerMode,
    onBackClick: () -> Unit,
) {
    WeeklyPlanScreen(
        plannerMode = plannerMode,
        onBackClick = onBackClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyPlanScreen(
    plannerMode: PlannerMode,
    onBackClick: () -> Unit,
) {
    val heroGradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.tertiaryContainer,
        ),
    )

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
                                text = when (plannerMode) {
                                    PlannerMode.DAY -> stringResource(R.string.day_plan_hero_title)
                                    PlannerMode.WEEK -> stringResource(R.string.weekly_plan_hero_title)
                                },
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = when (plannerMode) {
                                PlannerMode.DAY -> stringResource(R.string.day_plan_hero_copy)
                                PlannerMode.WEEK -> stringResource(R.string.weekly_plan_hero_copy)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f),
                        )
                    }
                }
            }

            items(weeklyPreview, key = { it.day }) { plan ->
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
                        Text(
                            text = plan.day,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )

                        PlannerRow(label = stringResource(R.string.breakfast_label), value = plan.breakfast)
                        PlannerRow(label = stringResource(R.string.lunch_label), value = plan.lunch)
                        PlannerRow(label = stringResource(R.string.dinner_label), value = plan.dinner)
                    }
                }
            }
        }
    }
}

@Composable
private fun PlannerRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WeeklyPlanScreenPreview() {
    BuildersClubPocTheme {
        WeeklyPlanScreen(
            plannerMode = PlannerMode.WEEK,
            onBackClick = {},
        )
    }
}
