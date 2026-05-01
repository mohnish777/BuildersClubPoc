package com.example.buildersclubpoc.planner.presentation

enum class PlannerMode(val routeValue: String) {
    DAY("day"),
    WEEK("week"),
    ;

    companion object {
        fun fromRouteValue(value: String?): PlannerMode {
            return entries.firstOrNull { it.routeValue == value } ?: WEEK
        }
    }
}
