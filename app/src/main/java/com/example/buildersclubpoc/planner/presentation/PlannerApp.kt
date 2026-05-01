package com.example.buildersclubpoc.planner.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.buildersclubpoc.R
import com.example.buildersclubpoc.planner.presentation.home.HomeRoot
import com.example.buildersclubpoc.planner.presentation.timeline.TimelineRoot
import com.example.buildersclubpoc.planner.presentation.weeklyplan.WeeklyPlanRoot

@Composable
fun PlannerApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomDestinations = listOf(
        BottomDestination(
            label = stringResource(R.string.bottom_nav_home),
            icon = Icons.Outlined.Home,
            route = PlannerDestination.Home.route,
        ),
        BottomDestination(
            label = stringResource(R.string.bottom_nav_plan),
            icon = Icons.Outlined.CalendarMonth,
            route = PlannerDestination.WeeklyPlan.createRoute(PlannerMode.WEEK),
        ),
        BottomDestination(
            label = stringResource(R.string.bottom_nav_timeline),
            icon = Icons.Outlined.Bolt,
            route = PlannerDestination.Timeline.route,
        ),
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomDestinations.forEach { destination ->
                    val selected = currentDestination
                        ?.hierarchy
                        ?.any { navDestination ->
                            when (destination.route) {
                                PlannerDestination.Home.route -> navDestination.route == PlannerDestination.Home.route
                                PlannerDestination.Timeline.route -> navDestination.route == PlannerDestination.Timeline.route
                                else -> navDestination.route == PlannerDestination.WeeklyPlan.route
                            }
                        } == true

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label,
                            )
                        },
                        label = {
                            Text(text = destination.label)
                        },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = PlannerDestination.Home.route,
        ) {
            composable(route = PlannerDestination.Home.route) {
                HomeRoot(
                    onContinueClick = { mode ->
                        navController.navigate(PlannerDestination.WeeklyPlan.createRoute(mode))
                    },
                )
            }

            composable(route = PlannerDestination.WeeklyPlan.route) { backStackEntry ->
                WeeklyPlanRoot(
                    plannerMode = PlannerMode.fromRouteValue(
                        backStackEntry.arguments?.getString(PlannerDestination.WeeklyPlan.modeArg),
                    ),
                    onBackClick = { navController.popBackStack() },
                )
            }

            composable(route = PlannerDestination.Timeline.route) {
                TimelineRoot(
                    onBackClick = { navController.popBackStack() },
                )
            }
        }
    }
}

sealed class PlannerDestination(val route: String) {
    data object Home : PlannerDestination("home")
    data object WeeklyPlan : PlannerDestination("weekly_plan/{mode}") {
        const val modeArg = "mode"

        fun createRoute(mode: PlannerMode): String {
            return "weekly_plan/${mode.routeValue}"
        }
    }

    data object Timeline : PlannerDestination("timeline")
}

private data class BottomDestination(
    val label: String,
    val icon: ImageVector,
    val route: String,
)
