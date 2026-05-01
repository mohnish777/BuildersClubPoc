package com.example.buildersclubpoc.planner.presentation.timeline

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.buildersclubpoc.R
import com.example.buildersclubpoc.ui.theme.BuildersClubPocTheme

@Composable
fun TimelineRoot(
    onBackClick: () -> Unit,
    viewModel: TimelineViewModel = viewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        viewModel.onAction(TimelineAction.OnScreenShown)
    }

    TimelineScreen(
        state = state,
        onAction = viewModel::onAction,
        onBackClick = onBackClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    state: TimelineState,
    onAction: (TimelineAction) -> Unit,
    onBackClick: () -> Unit,
) {
    val heroGradient = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.primaryContainer,
        ),
    )

    when (val sheet = state.sheetState) {
        is TimelineSheetState.MealEditor -> {
            MealDestinationSheet(
                sheetState = sheet,
                onDismiss = { onAction(TimelineAction.OnSheetDismissed) },
                onOptionSelected = { option ->
                    onAction(TimelineAction.OnFulfillmentOptionSelected(option))
                },
            )
        }
        TimelineSheetState.GroceryEditor -> {
            GroceryCartSheet(
                items = state.cartItems,
                onDismiss = { onAction(TimelineAction.OnSheetDismissed) },
                onItemToggled = { itemId ->
                    onAction(TimelineAction.OnCartItemToggled(itemId))
                },
            )
        }
        null -> Unit
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(R.string.timeline_title))
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
                                imageVector = Icons.Outlined.Bolt,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            )
                            Text(
                                text = stringResource(R.string.timeline_hero_title),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = stringResource(R.string.timeline_hero_copy),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.9f),
                        )
                    }
                }
            }

            itemsIndexed(state.actions, key = { _, action -> action.id }) { index, action ->
                val isVisible = index < state.visibleCount
                val alpha = animateFloatAsState(
                    targetValue = if (isVisible) 1f else 0f,
                    animationSpec = tween(durationMillis = 450),
                    label = "timeline_alpha_$index",
                )
                val translationY = animateFloatAsState(
                    targetValue = if (isVisible) 0f else 26f,
                    animationSpec = tween(durationMillis = 450),
                    label = "timeline_y_$index",
                )

                TimelineItem(
                    action = action,
                    modifier = Modifier.graphicsLayer {
                        this.alpha = alpha.value
                        this.translationY = translationY.value
                    },
                    onClick = { onAction(TimelineAction.OnTimelineEntryClicked(action.id)) },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MealDestinationSheet(
    sheetState: TimelineSheetState.MealEditor,
    onDismiss: () -> Unit,
    onOptionSelected: (FulfillmentOption) -> Unit,
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
                text = stringResource(R.string.timeline_meal_sheet_title, sheetState.mealTitle),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(R.string.timeline_meal_sheet_copy),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            sheetState.allowedOptions.forEach { option ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOptionSelected(option) },
                    shape = RoundedCornerShape(20.dp),
                    color = if (sheetState.selectedOption == option) {
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
                        Text(
                            text = labelForOption(option),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )

                        if (sheetState.selectedOption == option) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GroceryCartSheet(
    items: List<CartItemUi>,
    onDismiss: () -> Unit,
    onItemToggled: (String) -> Unit,
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
                text = stringResource(R.string.timeline_cart_sheet_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(R.string.timeline_cart_sheet_copy),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            items.forEach { item ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = item.isSelected,
                            onCheckedChange = { onItemToggled(item.id) },
                        )
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineItem(
    action: TimelineActionUi,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(width = 2.dp, height = 96.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            )
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = action.time,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.edit_cta),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                Text(
                    text = action.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = action.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun labelForOption(option: FulfillmentOption): String {
    return when (option) {
        FulfillmentOption.OFFICE -> stringResource(R.string.timeline_option_office)
        FulfillmentOption.HOME -> stringResource(R.string.timeline_option_home)
        FulfillmentOption.DINE_OUT -> stringResource(R.string.timeline_option_dineout)
    }
}

@Preview(showBackground = true)
@Composable
private fun TimelineScreenPreview() {
    BuildersClubPocTheme {
        TimelineScreen(
            state = TimelineState(
                visibleCount = 4,
            ),
            onAction = {},
            onBackClick = {},
        )
    }
}
