package com.example.buildersclubpoc.planner.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.buildersclubpoc.planner.presentation.home.HomeRoot
import com.example.buildersclubpoc.planner.presentation.weeklyplan.WeeklyPlanRoot

@Composable
fun PlannerApp() {
    val navController = rememberNavController()

    NavHost(
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
}
